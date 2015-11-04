package com.daedafusion.knowledge.trinity.dictionary;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Created by mphilpot on 7/10/14.
 */
public class DictionaryPool
{
    private static DictionaryPool ourInstance = new DictionaryPool();

    public static DictionaryPool getInstance()
    {
        return ourInstance;
    }

    private static class DictionaryObjectFactory extends BasePooledObjectFactory<Dictionary>
    {

        @Override
        public Dictionary create() throws Exception
        {
            Dictionary dict = new Dictionary();
            dict.init();

            return dict;
        }

        @Override
        public PooledObject<Dictionary> wrap(Dictionary dictionary)
        {
            return new DefaultPooledObject<Dictionary>(dictionary);
        }

        @Override
        public void destroyObject(PooledObject<Dictionary> p) throws Exception
        {
            p.getObject().close();
        }
    }

    private class DictionaryObjectPool extends GenericObjectPool<Dictionary>
    {
        public DictionaryObjectPool(PooledObjectFactory<Dictionary> factory)
        {
            super(factory);
            setLifo(false);
        }
    }

    private ObjectPool<Dictionary> pool;

    private DictionaryPool()
    {
        pool = new DictionaryObjectPool(new DictionaryObjectFactory());
    }

    public ObjectPool<Dictionary> getPool()
    {
        return pool;
    }
}
