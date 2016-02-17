package com.daedafusion.knowledge.trinity.triples.query.strategy;

import com.daedafusion.hbase.PostfixFilter;
import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.util.Hash;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.triples.query.QueryContext;
import com.daedafusion.knowledge.trinity.util.HashBytes;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mphilpot on 7/8/14.
 */
public class ObjectPostfixQueryStrategy extends AbstractQueryStrategy
{
    private static final Logger log = Logger.getLogger(ObjectPostfixQueryStrategy.class);

    protected HTableInterface ospTable;

    @Override
    public void init(QueryContext queryContext) throws IOException
    {
        this.context = queryContext;
        ospTable = HBasePool.getInstance().getTable(Schema.OSP_P);
    }

    @Override
    public boolean canFind(Node subject, Node predicate, Node object)
    {
        return (object.isConcrete() && subject.isConcrete() && predicate.isConcrete()) ||
                (object.isConcrete() && subject.isConcrete() ||
                        (object.isConcrete() && !predicate.isConcrete()));
    }

    @Override
    public ExtendedIterator<Triple> find(Node subject, Node predicate, Node object) throws IOException
    {
        if(log.isDebugEnabled())
        {
            log.debug(String.format("Object Query Strategy :: %s %s %s", subject.toString(), predicate.toString(), object.toString()));
        }

        Map<String, ResultScanner> scanners = new HashMap<>();
        Map<String, String> startKeys = new HashMap<>();

        String partitions = StringUtils.join(context.getQuery().getPartitions(), "|");

        //long partitionHash = Hash.hashString(partition);
        HashBytes objectHash;

        if(object.isLiteral())
            objectHash = Hash.hashString((String) object.getLiteralValue());
        else
            objectHash = Hash.hashNode(object);

        HashBytes subjectHash = new HashBytes();
        HashBytes predicateHash = new HashBytes();

        if (subject.isConcrete())
        {
            subjectHash = Hash.hashNode(subject);
        }

        if (predicate.isConcrete())
        {
            predicateHash = Hash.hashNode(predicate);
        }

        byte[] startKey = buildKey(null, objectHash, subjectHash, predicateHash);
        String startKey64 = Base64.encodeBase64String(startKey);

        if (context.getCursorMap().containsKey(partitions) &&
                context.getCursorMap().get(partitions).containsKey(startKey64))
        {
            startKey = Base64.decodeBase64(context.getCursorMap().get(partitions).get(startKey64));
        }

        byte[] endKey = buildEndKey(null, objectHash, subjectHash, predicateHash);

        Scan scan = new Scan(startKey, endKey);
        scan.setCaching(1000);

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        for(String p : context.getQuery().getPartitions())
        {
            filterList.addFilter(new PostfixFilter(
                    CompareFilter.CompareOp.EQUAL, Hash.hashString(p).getBytes(), 3 * HashBytes.SIZEOF_HASH));
        }
        scan.setFilter(filterList);

        if (context.hasTimeConstraints())
        {
            addTimeConstraints(scan);
        }

        try
        {
            scanners.put(partitions, ospTable.getScanner(scan));
        }
        catch (IOException e)
        {
            log.warn("Query exception. Reconnecting to HBase", e);
            try
            {
                ospTable.close();
                ospTable = HBasePool.getInstance().getTable(Schema.SPO);
            }
            catch (IOException e1)
            {
                log.warn("Unable to reconnect to hbase", e1);
            }

            throw e;
        }

        startKeys.put(partitions, startKey64);


        return new ScanIterator(startKeys, scanners, new KeyParser()
        {
            @Override
            public HashBytes getPartitionHash(byte[] bytes)
            {
                return new HashBytes(bytes, 3*HashBytes.SIZEOF_HASH, HashBytes.SIZEOF_HASH);
            }

            @Override
            public HashBytes getSubjectHash(byte[] bytes)
            {
                return new HashBytes(bytes, HashBytes.SIZEOF_HASH, HashBytes.SIZEOF_HASH);
            }

            @Override
            public HashBytes getPredicateHash(byte[] bytes)
            {
                return new HashBytes(bytes, 2*HashBytes.SIZEOF_HASH, HashBytes.SIZEOF_HASH);
            }

            @Override
            public HashBytes getObjectHash(byte[] bytes)
            {
                return new HashBytes(bytes, 0, HashBytes.SIZEOF_HASH);
            }
        }, context);

    }

    @Override
    public void close() throws IOException
    {
        ospTable.close();
    }
}
