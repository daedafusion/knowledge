package com.daedafusion.knowledge.trinity.triples.query.strategy;

import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.util.Hash;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.triples.query.QueryContext;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mphilpot on 7/8/14.
 */
public class PredicateQueryStrategy extends AbstractQueryStrategy
{
    private static final Logger log = Logger.getLogger(PredicateQueryStrategy.class);

    protected HTableInterface posTable;

    @Override
    public void init(QueryContext queryContext) throws IOException
    {
        this.context = queryContext;
        posTable = HBasePool.getInstance().getTable(Schema.POS);
    }

    @Override
    public boolean canFind(Node subject, Node predicate, Node object)
    {
        return predicate.isConcrete();
    }

    @Override
    public ExtendedIterator<Triple> find(Node subject, Node predicate, Node object) throws IOException
    {
        if(log.isDebugEnabled())
        {
            log.debug(String.format("Predicate Query Strategy :: %s %s %s", subject.toString(), predicate.toString(), object.toString()));
        }

        Map<String, ResultScanner> scanners = new HashMap<>();
        Map<String, String> startKeys = new HashMap<>();

        for(String partition : context.getQuery().getPartitions())
        {
            long partitionHash = Hash.hashString(partition);
            long predicateHash = Hash.hashNode(predicate);
            long objectHash = 0;
            long subjectHash = 0;


            if (object.isConcrete())
            {
                if(object.isLiteral())
                    objectHash = Hash.hashString((String) object.getLiteralValue());
                else
                    objectHash = Hash.hashNode(object);
            }

            if (subject.isConcrete())
            {
                subjectHash = Hash.hashNode(subject);
            }

            byte[] startKey = buildKey(partitionHash, predicateHash, objectHash, subjectHash);
            String startKey64 = Base64.encodeBase64String(startKey);

            if (context.getCursorMap().containsKey(partition) &&
                    context.getCursorMap().get(partition).containsKey(startKey64))
            {
                // Check if we have exhausted this partition/stage previously
                if(context.getCursorMap().get(partition).get(startKey64).equals(""))
                {
                    // We don't need to scan
                    startKeys.put(partition, startKey64);
                    continue;
                }

                startKey = Base64.decodeBase64(context.getCursorMap().get(partition).get(startKey64));
            }

            byte[] endKey = buildEndKey(partitionHash, predicateHash, objectHash, subjectHash);

            Scan scan = new Scan(startKey, endKey);
            scan.setCaching(1000);

            if (context.hasTimeConstraints())
            {
                addTimeConstraints(scan);
            }

            try
            {
                scanners.put(partition, posTable.getScanner(scan));
            }
            catch (IOException e)
            {
                log.warn("Query exception. Reconnecting to HBase", e);
                try
                {
                    posTable.close();
                    posTable = HBasePool.getInstance().getTable(Schema.SPO);
                }
                catch (IOException e1)
                {
                    log.warn("Unable to reconnect to hbase", e1);
                }

                throw e;
            }

            startKeys.put(partition, startKey64);
        }

        return new ScanIterator(startKeys, scanners, new KeyParser()
        {
            @Override
            public Long getPartitionHash(byte[] bytes)
            {
                return Bytes.toLong(bytes, 0, Bytes.SIZEOF_LONG);
            }

            @Override
            public Long getSubjectHash(byte[] bytes)
            {
                return Bytes.toLong(bytes, 3*Bytes.SIZEOF_LONG, Bytes.SIZEOF_LONG);
            }

            @Override
            public Long getPredicateHash(byte[] bytes)
            {
                return Bytes.toLong(bytes, Bytes.SIZEOF_LONG, Bytes.SIZEOF_LONG);
            }

            @Override
            public Long getObjectHash(byte[] bytes)
            {
                return Bytes.toLong(bytes, 2*Bytes.SIZEOF_LONG, Bytes.SIZEOF_LONG);
            }
        }, context);
    }

    @Override
    public void close() throws IOException
    {
        posTable.close();
    }
}