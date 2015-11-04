package com.daedafusion.knowledge.trinity.util;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by mphilpot on 7/9/14.
 */
public class MemoryOnlyEhcache<K, V> implements Cache<K, V>
{
    private static final Logger log = Logger.getLogger(MemoryOnlyEhcache.class);

    private Ehcache cache;

    public MemoryOnlyEhcache(String size, String namePrefix)
    {
        CacheConfiguration config = new CacheConfiguration();
        config.setMaxBytesLocalHeap(size);
        config.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        config.setMemoryStoreEvictionPolicy("LRU");
        config.eternal(true);
        config.setName(String.format("%s-%s", namePrefix, UUID.randomUUID().toString()));

        cache = new net.sf.ehcache.Cache(config);
        CacheManager.getInstance().addCache(cache);
    }

    @Override
    public void put(K key, V value)
    {
        cache.put(new Element(key, value));
    }

    @Override
    public V get(K key)
    {
        Element e = cache.get(key);

        if(e == null)
        {
            return null;
        }

        return (V) e.getObjectValue();
    }

    @Override
    public boolean contains(K key)
    {
        return cache.get(key) != null;
    }

    @Override
    public void close() throws IOException
    {
        if(cache != null)
        {
            cache.dispose();
        }
    }
}
