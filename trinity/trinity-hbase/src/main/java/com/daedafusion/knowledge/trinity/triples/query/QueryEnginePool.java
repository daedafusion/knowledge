package com.daedafusion.knowledge.trinity.triples.query;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Created by mphilpot on 7/10/14.
 */
public class QueryEnginePool
{
    private static QueryEnginePool ourInstance = new QueryEnginePool();

    public static QueryEnginePool getInstance()
    {
        return ourInstance;
    }

    private static class QueryEngineObjectFactory extends BasePooledObjectFactory<QueryEngine>
    {

        @Override
        public QueryEngine create() throws Exception
        {
            return QueryEngine.getInstance();
        }

        @Override
        public PooledObject<QueryEngine> wrap(QueryEngine queryEngine)
        {
            return new DefaultPooledObject<QueryEngine>(queryEngine);
        }

        @Override
        public void destroyObject(PooledObject<QueryEngine> p) throws Exception
        {
            p.getObject().close();
        }
    }

    private class QueryEngineObjectPool extends GenericObjectPool<QueryEngine>
    {
        public QueryEngineObjectPool(PooledObjectFactory<QueryEngine> factory)
        {
            super(factory);
            setLifo(false);
        }
    }

    private ObjectPool<QueryEngine> pool;

    private QueryEnginePool()
    {
        pool = new QueryEngineObjectPool(new QueryEngineObjectFactory());
    }

    public ObjectPool<QueryEngine> getPool()
    {
        return pool;
    }
}
