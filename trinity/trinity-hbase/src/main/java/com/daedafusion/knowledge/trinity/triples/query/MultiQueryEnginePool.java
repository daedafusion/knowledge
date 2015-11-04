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
public class MultiQueryEnginePool
{
    private static MultiQueryEnginePool ourInstance = new MultiQueryEnginePool();

    public static MultiQueryEnginePool getInstance()
    {
        return ourInstance;
    }

    private static class MultiQueryEngineObjectFactory extends BasePooledObjectFactory<MultiQueryEngine>
    {

        @Override
        public MultiQueryEngine create() throws Exception
        {
            return MultiQueryEngine.getInstance();
        }

        @Override
        public PooledObject<MultiQueryEngine> wrap(MultiQueryEngine queryEngine)
        {
            return new DefaultPooledObject<MultiQueryEngine>(queryEngine);
        }

        @Override
        public void destroyObject(PooledObject<MultiQueryEngine> p) throws Exception
        {
            p.getObject().close();
        }
    }

    private class MultiQueryEngineObjectPool extends GenericObjectPool<MultiQueryEngine>
    {
        public MultiQueryEngineObjectPool(PooledObjectFactory<MultiQueryEngine> factory)
        {
            super(factory);
            setLifo(false);
        }
    }

    private ObjectPool<MultiQueryEngine> pool;

    private MultiQueryEnginePool()
    {
        pool = new MultiQueryEngineObjectPool(new MultiQueryEngineObjectFactory());
    }

    public ObjectPool<MultiQueryEngine> getPool()
    {
        return pool;
    }
}
