package com.daedafusion.knowledge.trinity.triples.update;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.knowledge.trinity.dictionary.Dictionary;
import com.daedafusion.knowledge.trinity.util.HashBytes;
import com.daedafusion.sparql.Literal;
import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.util.Hash;
import com.daedafusion.knowledge.trinity.conf.Schema;
import org.apache.commons.codec.Charsets;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by mphilpot on 7/9/14.
 */
public abstract class AbstractWriter implements Closeable
{
    private static final Logger log = Logger.getLogger(AbstractWriter.class);

    public static class T
    {
        public String s;
        public String p;
        public String o;

        public T()
        {

        }

        public T(String s, String p, String o)
        {
            this.s = s;
            this.p = p;
            this.o = o;
        }

    }

    protected HTableInterface spoTable;
    protected HTableInterface posTable;
    protected HTableInterface ospTable;
    protected HTableInterface spopTable;
    protected HTableInterface pospTable;
    protected HTableInterface osppTable;

    protected Dictionary dictionary;

    /**
     * All resources go to dictionary
     *
     * spo => write all resources inline
     * so => write s & o inline
     * s => write s inline
     * o => write o inline
     */
    protected Set<String> resourceStrategy;

    protected Map<String, HashBytes> partitionCache;

    public void init() throws IOException
    {
        spoTable = HBasePool.getInstance().getTable(Schema.SPO);
        posTable = HBasePool.getInstance().getTable(Schema.POS);
        ospTable = HBasePool.getInstance().getTable(Schema.OSP);

        spopTable = HBasePool.getInstance().getTable(Schema.SPO_P);
        pospTable = HBasePool.getInstance().getTable(Schema.POS_P);
        osppTable = HBasePool.getInstance().getTable(Schema.OSP_P);

        spoTable.setAutoFlushTo(false);
        posTable.setAutoFlushTo(false);
        ospTable.setAutoFlushTo(false);

        spopTable.setAutoFlushTo(false);
        pospTable.setAutoFlushTo(false);
        osppTable.setAutoFlushTo(false);

        dictionary = new Dictionary();
        dictionary.init();

        resourceStrategy = new HashSet<>();

        for (char c : Configuration.getInstance().getString("resource.strategy", "so").toCharArray())
        {
            resourceStrategy.add(String.valueOf(c));
        }

        partitionCache = new HashMap<>();
    }

    public void setAutoFlush(boolean autoFlush)
    {
        spoTable.setAutoFlushTo(autoFlush);
        posTable.setAutoFlushTo(autoFlush);
        ospTable.setAutoFlushTo(autoFlush);

        spopTable.setAutoFlushTo(autoFlush);
        pospTable.setAutoFlushTo(autoFlush);
        osppTable.setAutoFlushTo(autoFlush);

        dictionary.setAutoFlush(autoFlush);
    }

    protected void delete(String partition, List<T> triples) throws IOException
    {
        HashBytes partitionHash;

        if(partitionCache.containsKey(partition))
        {
            partitionHash = partitionCache.get(partition);
        }
        else
        {
            partitionHash = Hash.hashString(partition);
            partitionCache.put(partition, partitionHash);
        }

        Map<HTableInterface, List<Delete>> deletes = new HashMap<>();
        deletes.put(spoTable, new ArrayList<Delete>());
        deletes.put(posTable, new ArrayList<Delete>());
        deletes.put(ospTable, new ArrayList<Delete>());
        deletes.put(spopTable, new ArrayList<Delete>());
        deletes.put(pospTable, new ArrayList<Delete>());
        deletes.put(osppTable, new ArrayList<Delete>());

        for(T t : triples)
        {
            HashBytes subjectHash = Hash.hashString(t.s);
            HashBytes predicateHash = Hash.hashString(t.p);
            HashBytes objectHash = Hash.hashString(t.o);

            Delete delete = buildDelete(
                    buildKey(
                            partitionHash.getBytes(),
                            subjectHash.getBytes(),
                            predicateHash.getBytes(),
                            objectHash.getBytes()
                    )
            );
            deletes.get(spoTable).add(delete);

            delete = buildDelete(
                    buildKey(
                            partitionHash.getBytes(),
                            predicateHash.getBytes(),
                            objectHash.getBytes(),
                            subjectHash.getBytes()
                    )
            );
            deletes.get(posTable).add(delete);

            delete = buildDelete(
                    buildKey(
                            partitionHash.getBytes(),
                            objectHash.getBytes(),
                            subjectHash.getBytes(),
                            predicateHash.getBytes()
                    )
            );
            deletes.get(ospTable).add(delete);

            delete = buildDelete(
                    buildKey(
                            subjectHash.getBytes(),
                            predicateHash.getBytes(),
                            objectHash.getBytes(),
                            partitionHash.getBytes()
                    )
            );
            deletes.get(spopTable).add(delete);

            delete = buildDelete(
                    buildKey(
                            predicateHash.getBytes(),
                            objectHash.getBytes(),
                            subjectHash.getBytes(),
                            partitionHash.getBytes()
                    )
            );
            deletes.get(pospTable).add(delete);

            delete = buildDelete(
                    buildKey(
                            objectHash.getBytes(),
                            subjectHash.getBytes(),
                            predicateHash.getBytes(),
                            partitionHash.getBytes()
                    )
            );
            deletes.get(osppTable).add(delete);
        }

        for(Map.Entry<HTableInterface, List<Delete>> e : deletes.entrySet())
        {
            e.getKey().delete(e.getValue());
        }
    }

    /**
     *
     * Triple with literal object
     *
     * @param partition
     * @param subject
     * @param predicate
     * @param object The n-triple form (e.g. "test"@en-us or "7"^^<fully_qualified_int> -- This is used for the hash
     * @param objectLiteral -- this is stored in Q_OBJECT
     * @param objectLiteralDatatype
     * @param objectLiteralLang
     * @param epoch
     * @param externalSource
     * @param ingestId
     * @throws IOException
     */
    protected void add(String partition, String subject, String predicate, String object, String objectLiteral, String objectLiteralDatatype, String objectLiteralLang, Long epoch, String externalSource, String ingestId) throws IOException
    {
        HashBytes partitionHash;

        if(partitionCache.containsKey(partition))
        {
            partitionHash = partitionCache.get(partition);
        }
        else
        {
            partitionHash = Hash.hashString(partition);
            partitionCache.put(partition, partitionHash);
        }

        HashBytes subjectHash = Hash.hashString(subject);
        HashBytes predicateHash = Hash.hashString(predicate);
        HashBytes objectHash = Hash.hashString(object);

        // Add to dictionary
        dictionary.setResource(subjectHash, subject);
        dictionary.setPredicate(predicateHash, predicate);

        Literal lit = new Literal();
        lit.value = objectLiteral;
        lit.type = objectLiteralDatatype;
        lit.lang = objectLiteralLang;

        dictionary.setLiteral(objectHash, lit);

        dictionary.setPredicateLiteral(predicateHash, objectLiteral);

        Put put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        spoTable.put(put);

        put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        posTable.put(put);

        put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        ospTable.put(put);

        // Write post fix tables
        put = buildPut(
                buildKey(
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        spopTable.put(put);

        put = buildPut(
                buildKey(
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        pospTable.put(put);

        put = buildPut(
                buildKey(
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        osppTable.put(put);
    }

    /**
     *
     * @param partition
     * @param subject
     * @param predicate
     * @param object -- is a Resource http://someUri
     * @param epoch
     * @param externalSource
     * @param ingestId
     * @throws IOException
     */
    protected void add(String partition, String subject, String predicate, String object, Long epoch, String externalSource, String ingestId) throws IOException
    {
        HashBytes partitionHash;

        if(partitionCache.containsKey(partition))
        {
            partitionHash = partitionCache.get(partition);
        }
        else
        {
            partitionHash = Hash.hashString(partition);
            partitionCache.put(partition, partitionHash);
        }

        HashBytes subjectHash = Hash.hashString(subject);
        HashBytes predicateHash = Hash.hashString(predicate);
        HashBytes objectHash = Hash.hashString(object);

        // Add to dictionary
        dictionary.setResource(subjectHash, subject);
        dictionary.setPredicate(predicateHash, predicate);
        dictionary.setResource(objectHash, object);

        Put put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        spoTable.put(put);

        put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        posTable.put(put);

        put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        ospTable.put(put);

        // Write post fix tables
        put = buildPut(
                buildKey(
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        spopTable.put(put);

        put = buildPut(
                buildKey(
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        pospTable.put(put);

        put = buildPut(
                buildKey(
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(Charsets.UTF_8),
                ingestId.getBytes(Charsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        osppTable.put(put);
    }

    private byte[] buildKey(byte[] zero, byte[] first, byte[] second, byte[] third)
    {
        ByteBuffer buff = ByteBuffer.allocate(Schema.TRIPLE_KEY_LENGTH);
        buff.put(zero);
        buff.put(first);
        buff.put(second);
        buff.put(third);
        return buff.array();
    }

    private Delete buildDelete(byte[] key)
    {
        return new Delete(key);
    }

    private Put buildPut(byte[] key, long epoch, byte[] externalSource, byte[] ingest)
    {
        Put put = new Put(key);
        put.add(Schema.F_INFO, Schema.Q_EPOCH, Bytes.toBytes(epoch));
        put.add(Schema.F_INFO, Schema.Q_EXTERNAL_SOURCE, externalSource);
        put.add(Schema.F_INFO, Schema.Q_INGEST_ID, ingest);
        return put;
    }

    private void addResources(Put put, String subject, String predicate, String object, String partition)
    {
        if(resourceStrategy.contains("s"))
            put.add(Schema.F_RESOURCE, Schema.Q_SUBJECT, subject.getBytes(Charsets.UTF_8));

        if(resourceStrategy.contains("p"))
            put.add(Schema.F_RESOURCE, Schema.Q_PREDICATE, predicate.getBytes(Charsets.UTF_8));

        if(resourceStrategy.contains("o"))
            put.add(Schema.F_RESOURCE, Schema.Q_OBJECT, object.getBytes(Charsets.UTF_8));

        put.add(Schema.F_RESOURCE, Schema.Q_PARTITION, partition.getBytes(Charsets.UTF_8));
    }

    private void addResources(Put put, String subject, String predicate, String object, String objectLiteral, String objectDatatype, String objectLang, String partition)
    {
        if(resourceStrategy.contains("s"))
            put.add(Schema.F_RESOURCE, Schema.Q_SUBJECT, subject.getBytes(Charsets.UTF_8));

        if(resourceStrategy.contains("p"))
            put.add(Schema.F_RESOURCE, Schema.Q_PREDICATE, predicate.getBytes(Charsets.UTF_8));

        if(resourceStrategy.contains("o"))
        {
            put.add(Schema.F_RESOURCE, Schema.Q_OBJECT, object.getBytes(Charsets.UTF_8));
            if (objectLiteral != null)
                put.add(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL, objectLiteral.getBytes(Charsets.UTF_8));
            if (objectDatatype != null)
                put.add(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL_DATATYPE, objectDatatype.getBytes(Charsets.UTF_8));
            if (objectLang != null)
                put.add(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL_LANG, objectLang.getBytes(Charsets.UTF_8));
        }

        put.add(Schema.F_RESOURCE, Schema.Q_PARTITION, partition.getBytes(Charsets.UTF_8));
    }

    public void flush() throws IOException
    {
        spoTable.flushCommits();
        posTable.flushCommits();
        ospTable.flushCommits();
        spopTable.flushCommits();
        pospTable.flushCommits();
        osppTable.flushCommits();
        dictionary.flush();
    }

    @Override
    public void close() throws IOException
    {
        log.info("Closing AbstractWriter");
        flush();
        spoTable.close();
        posTable.close();
        ospTable.close();
        spopTable.close();
        pospTable.close();
        osppTable.close();
        dictionary.close();
    }
}
