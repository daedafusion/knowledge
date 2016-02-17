package com.daedafusion.knowledge.trinity.triples.query.strategy;

import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.triples.query.QueryContext;
import com.daedafusion.knowledge.trinity.util.HashBytes;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mphilpot on 7/8/14.
 */
public abstract class AbstractQueryStrategy implements QueryStrategy
{
    private static final Logger log = Logger.getLogger(AbstractQueryStrategy.class);

    protected QueryContext context;

    protected byte[] buildKey(HashBytes partition, HashBytes first, HashBytes second, HashBytes third)
    {
        ByteBuffer buff = ByteBuffer.allocate(Schema.TRIPLE_KEY_LENGTH);

        if(partition != null)
            buff.put(partition.getBytes());

        buff.put(first.getBytes());
        buff.put(second.getBytes());
        buff.put(third.getBytes());

        if(partition == null)
            buff.put(Bytes.toBytes(0L));

        return buff.array();
    }

    protected byte[] buildEndKey(HashBytes partition, HashBytes first, HashBytes second, HashBytes third)
    {
        HashBytes endFirst = new HashBytes();
        HashBytes endSecond = new HashBytes();
        HashBytes endThird = new HashBytes();

        if(!second.isEmpty() && !third.isEmpty())
        {
            endFirst.setBytes(first);
            endSecond.setBytes(second);
            endThird.setBytes(third.plusOne());
        }
        else if(!second.isEmpty())
        {
            endFirst.setBytes(first);
            endSecond.setBytes(second.plusOne());
        }
        else
        {
            endFirst.setBytes(first.plusOne());
        }

        return buildKey(partition, endFirst, endSecond, endThird);
    }

    protected void addTimeConstraints(Scan scan)
    {
        List<Filter> filters = new ArrayList<>();

        if(context.getQuery().getAfter() != null)
        {
            filters.add(new SingleColumnValueFilter(Schema.F_INFO, Schema.Q_EPOCH, CompareFilter.CompareOp.GREATER_OR_EQUAL, Bytes.toBytes(context.getQuery().getAfter())));
        }

        if(context.getQuery().getBefore() != null)
        {
            filters.add(new SingleColumnValueFilter(Schema.F_INFO, Schema.Q_EPOCH, CompareFilter.CompareOp.LESS_OR_EQUAL, Bytes.toBytes(context.getQuery().getBefore())));
        }

        if(!filters.isEmpty())
        {
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, filters);
            scan.setFilter(filterList);
        }
    }

    public abstract void init(QueryContext context) throws IOException;
}
