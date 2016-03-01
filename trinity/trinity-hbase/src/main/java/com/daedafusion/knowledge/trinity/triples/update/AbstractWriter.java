package com.daedafusion.knowledge.trinity.triples.update;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.dictionary.Dictionary;
import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.util.Hash;
import com.daedafusion.knowledge.trinity.util.HashBytes;
import com.daedafusion.sparql.Literal;
import org.apache.commons.codec.Charsets;
import org.apache.commons.collections.Buffer;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    protected Map<TableName, Table> tables;

    protected Map<TableName, BufferedMutator> mutators;

    protected Dictionary dictionary;

    protected boolean buffered;

    /**
     * All resources go to dictionary
     *
     * spo =&lt; write all resources inline
     * so =&lt; write s &amp; o inline
     * s =&lt; write s inline
     * o =&lt; write o inline
     */
    protected Set<String> resourceStrategy;

    protected Map<String, HashBytes> partitionCache;

    public void init(boolean buffered) throws IOException
    {
        this.buffered = buffered;
        tables = new HashMap<>();
        mutators = new HashMap<>();

        tables.put(Schema.SPO, HBasePool.getInstance().getTable(Schema.SPO));
        tables.put(Schema.POS, HBasePool.getInstance().getTable(Schema.POS));
        tables.put(Schema.OSP, HBasePool.getInstance().getTable(Schema.OSP));
        tables.put(Schema.SPO_P, HBasePool.getInstance().getTable(Schema.SPO_P));
        tables.put(Schema.POS_P, HBasePool.getInstance().getTable(Schema.POS_P));
        tables.put(Schema.OSP_P, HBasePool.getInstance().getTable(Schema.OSP_P));

        mutators.put(Schema.SPO, HBasePool.getInstance().getBufferedMutator(Schema.SPO));
        mutators.put(Schema.POS, HBasePool.getInstance().getBufferedMutator(Schema.POS));
        mutators.put(Schema.OSP, HBasePool.getInstance().getBufferedMutator(Schema.OSP));
        mutators.put(Schema.SPO_P, HBasePool.getInstance().getBufferedMutator(Schema.SPO_P));
        mutators.put(Schema.POS_P, HBasePool.getInstance().getBufferedMutator(Schema.POS_P));
        mutators.put(Schema.OSP_P, HBasePool.getInstance().getBufferedMutator(Schema.OSP_P));

        dictionary = new Dictionary();
        dictionary.init(buffered);

        resourceStrategy = new HashSet<>();

        for (char c : Configuration.getInstance().getString("resource.strategy", "so").toCharArray())
        {
            resourceStrategy.add(String.valueOf(c));
        }

        partitionCache = new HashMap<>();
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

        Map<TableName, List<Delete>> deletes = new HashMap<>();
        deletes.put(Schema.SPO, new ArrayList<>());
        deletes.put(Schema.POS, new ArrayList<>());
        deletes.put(Schema.OSP, new ArrayList<>());
        deletes.put(Schema.SPO_P, new ArrayList<>());
        deletes.put(Schema.POS_P, new ArrayList<>());
        deletes.put(Schema.OSP_P, new ArrayList<>());

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
            deletes.get(Schema.SPO).add(delete);

            delete = buildDelete(
                    buildKey(
                            partitionHash.getBytes(),
                            predicateHash.getBytes(),
                            objectHash.getBytes(),
                            subjectHash.getBytes()
                    )
            );
            deletes.get(Schema.POS).add(delete);

            delete = buildDelete(
                    buildKey(
                            partitionHash.getBytes(),
                            objectHash.getBytes(),
                            subjectHash.getBytes(),
                            predicateHash.getBytes()
                    )
            );
            deletes.get(Schema.OSP).add(delete);

            delete = buildDelete(
                    buildKey(
                            subjectHash.getBytes(),
                            predicateHash.getBytes(),
                            objectHash.getBytes(),
                            partitionHash.getBytes()
                    )
            );
            deletes.get(Schema.SPO_P).add(delete);

            delete = buildDelete(
                    buildKey(
                            predicateHash.getBytes(),
                            objectHash.getBytes(),
                            subjectHash.getBytes(),
                            partitionHash.getBytes()
                    )
            );
            deletes.get(Schema.POS_P).add(delete);

            delete = buildDelete(
                    buildKey(
                            objectHash.getBytes(),
                            subjectHash.getBytes(),
                            predicateHash.getBytes(),
                            partitionHash.getBytes()
                    )
            );
            deletes.get(Schema.OSP_P).add(delete);
        }

        for(Map.Entry<TableName, List<Delete>> e : deletes.entrySet())
        {
            if(buffered)
            {
                mutators.get(e.getKey()).mutate(e.getValue());
            }
            else
            {
                tables.get(e.getKey()).delete(e.getValue());
            }
        }
    }

    private void applyPut(TableName name, Put put) throws IOException
    {
        if(buffered)
        {
            mutators.get(name).mutate(put);
        }
        else
        {
            tables.get(name).put(put);
        }
    }

    /**
     *
     * Triple with literal object
     *
     * @param partition
     * @param subject
     * @param predicate
     * @param object The n-triple form (e.g. "test"@en-us or "7"^^&gt;fully_qualified_int&lt; -- This is used for the hash
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
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        applyPut(Schema.SPO, put);

        put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        applyPut(Schema.POS, put);

        put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        applyPut(Schema.OSP, put);

        // Write post fix tables
        put = buildPut(
                buildKey(
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        applyPut(Schema.SPO_P, put);

        put = buildPut(
                buildKey(
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        applyPut(Schema.POS_P, put);

        put = buildPut(
                buildKey(
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, objectLiteral, objectLiteralDatatype, objectLiteralLang, partition);
        applyPut(Schema.OSP_P, put);
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
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        applyPut(Schema.SPO, put);

        put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        applyPut(Schema.POS, put);

        put = buildPut(
                buildKey(
                        partitionHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        applyPut(Schema.OSP, put);

        // Write post fix tables
        put = buildPut(
                buildKey(
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        applyPut(Schema.SPO_P, put);

        put = buildPut(
                buildKey(
                        predicateHash.getBytes(),
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        applyPut(Schema.POS_P, put);

        put = buildPut(
                buildKey(
                        objectHash.getBytes(),
                        subjectHash.getBytes(),
                        predicateHash.getBytes(),
                        partitionHash.getBytes()
                ),
                epoch,
                externalSource.getBytes(StandardCharsets.UTF_8),
                ingestId.getBytes(StandardCharsets.UTF_8)
        );
        addResources(put, subject, predicate, object, partition);
        applyPut(Schema.OSP_P, put);
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
        put.addColumn(Schema.F_INFO, Schema.Q_EPOCH, Bytes.toBytes(epoch));
        put.addColumn(Schema.F_INFO, Schema.Q_EXTERNAL_SOURCE, externalSource);
        put.addColumn(Schema.F_INFO, Schema.Q_INGEST_ID, ingest);
        return put;
    }

    private void addResources(Put put, String subject, String predicate, String object, String partition)
    {
        if(resourceStrategy.contains("s"))
            put.addColumn(Schema.F_RESOURCE, Schema.Q_SUBJECT, subject.getBytes(StandardCharsets.UTF_8));

        if(resourceStrategy.contains("p"))
            put.addColumn(Schema.F_RESOURCE, Schema.Q_PREDICATE, predicate.getBytes(StandardCharsets.UTF_8));

        if(resourceStrategy.contains("o"))
            put.addColumn(Schema.F_RESOURCE, Schema.Q_OBJECT, object.getBytes(StandardCharsets.UTF_8));

        put.addColumn(Schema.F_RESOURCE, Schema.Q_PARTITION, partition.getBytes(StandardCharsets.UTF_8));
    }

    private void addResources(Put put, String subject, String predicate, String object, String objectLiteral, String objectDatatype, String objectLang, String partition)
    {
        if(resourceStrategy.contains("s"))
            put.addColumn(Schema.F_RESOURCE, Schema.Q_SUBJECT, subject.getBytes(StandardCharsets.UTF_8));

        if(resourceStrategy.contains("p"))
            put.addColumn(Schema.F_RESOURCE, Schema.Q_PREDICATE, predicate.getBytes(StandardCharsets.UTF_8));

        if(resourceStrategy.contains("o"))
        {
            put.addColumn(Schema.F_RESOURCE, Schema.Q_OBJECT, object.getBytes(StandardCharsets.UTF_8));
            if (objectLiteral != null)
                put.addColumn(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL, objectLiteral.getBytes(StandardCharsets.UTF_8));
            if (objectDatatype != null)
                put.addColumn(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL_DATATYPE, objectDatatype.getBytes(StandardCharsets.UTF_8));
            if (objectLang != null)
                put.addColumn(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL_LANG, objectLang.getBytes(StandardCharsets.UTF_8));
        }

        put.addColumn(Schema.F_RESOURCE, Schema.Q_PARTITION, partition.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws IOException
    {
        log.info("Closing AbstractWriter");

        for(Map.Entry<TableName, Table> entry : tables.entrySet())
        {
            entry.getValue().close();
        }

        for(Map.Entry<TableName, BufferedMutator> entry : mutators.entrySet())
        {
            entry.getValue().close();
        }

        dictionary.close();
    }
}
