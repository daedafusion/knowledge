package com.daedafusion.knowledge.trinity.triples.query.strategy;

import com.daedafusion.hbase.PostfixFilter;
import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.util.Hash;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.triples.query.QueryContext;
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
public class SubjectPostfixQueryStrategy extends AbstractQueryStrategy
{
    private static final Logger log = Logger.getLogger(SubjectPostfixQueryStrategy.class);

    protected HTableInterface spoTable;

    public SubjectPostfixQueryStrategy()
    {

    }

    @Override
    public void init(QueryContext queryContext) throws IOException
    {
        this.context = queryContext;
        spoTable = HBasePool.getInstance().getTable(Schema.SPO_P);
    }

    @Override
    public boolean canFind(Node subject, Node predicate, Node object)
    {
        // Making a guess that it is more performant to favor object query strategy
        return subject.isConcrete() && !object.isConcrete();
    }

    @Override
    public ExtendedIterator<Triple> find(Node subject, Node predicate, Node object) throws IOException
    {
        if(log.isDebugEnabled())
        {
            log.debug(String.format("Subject Query Strategy :: %s %s %s", subject.toString(), predicate.toString(), object.toString()));
        }

        Map<String, ResultScanner> scanners = new HashMap<>();
        Map<String, String> startKeys = new HashMap<>();

        String partitions = StringUtils.join(context.getQuery().getPartitions(), "|");

        //long partitionHash = Hash.hashString(partition);
        long subjectHash = Hash.hashNode(subject);
        long predicateHash = 0;
        long objectHash = 0;

        if (predicate.isConcrete())
        {
            predicateHash = Hash.hashNode(predicate);
        }

        if (object.isConcrete())
        {
            if(object.isLiteral())
                objectHash = Hash.hashString((String) object.getLiteralValue());
            else
                objectHash = Hash.hashNode(object);
        }

        byte[] startKey = buildKey(null, subjectHash, predicateHash, objectHash);
        String startKey64 = Base64.encodeBase64String(startKey);

        if (context.getCursorMap().containsKey(partitions) &&
                context.getCursorMap().get(partitions).containsKey(startKey64))
        {
            startKey = Base64.decodeBase64(context.getCursorMap().get(partitions).get(startKey64));
        }

        byte[] endKey = buildEndKey(null, subjectHash, predicateHash, objectHash);

        Scan scan = new Scan(startKey, endKey);
        scan.setCaching(1000); // make configurable

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        for(String p : context.getQuery().getPartitions())
        {
            filterList.addFilter(new PostfixFilter(
                    CompareFilter.CompareOp.EQUAL, Bytes.toBytes(Hash.hashString(p)), 3 * Bytes.SIZEOF_LONG));
        }
        scan.setFilter(filterList);

        if (context.hasTimeConstraints())
        {
            addTimeConstraints(scan);
        }

        try
        {
            scanners.put(partitions, spoTable.getScanner(scan));
        }
        catch (IOException e)
        {
            log.warn("Query exception. Reconnecting to HBase", e);
            try
            {
                spoTable.close();
                spoTable = HBasePool.getInstance().getTable(Schema.SPO);
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
            public Long getPartitionHash(byte[] bytes)
            {
                return Bytes.toLong(bytes, 3*Bytes.SIZEOF_LONG, Bytes.SIZEOF_LONG);
            }

            @Override
            public Long getSubjectHash(byte[] bytes)
            {
                return Bytes.toLong(bytes, 0, Bytes.SIZEOF_LONG);
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
        log.debug("Closing SubjectQueryStrategy");
        spoTable.close();
    }
}
