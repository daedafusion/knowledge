package com.daedafusion.knowledge.trinity.util;

import com.daedafusion.knowledge.trinity.conf.HBaseConfig;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by mphilpot on 7/8/14.
 */
public class HBasePool implements Closeable
{
    private static final Logger    log         = Logger.getLogger(HBasePool.class);

    private static       HBasePool ourInstance = new HBasePool();

    public static HBasePool getInstance()
    {
        return ourInstance;
    }

    private Connection pool;

    private HBasePool()
    {
        try
        {
            pool = ConnectionFactory.createConnection(HBaseConfig.getConfiguration());
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    public Table getTable(TableName tn) throws IOException
    {
        return pool.getTable(tn);
    }

    public BufferedMutator getBufferedMutator(TableName tn) throws IOException
    {
        return pool.getBufferedMutator(tn);
    }

    // TODO method to net new connection?

    @Override
    public void close() throws IOException
    {
        pool.close();
    }
}
