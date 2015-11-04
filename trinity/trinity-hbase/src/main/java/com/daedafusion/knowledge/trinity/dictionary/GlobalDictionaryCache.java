package com.daedafusion.knowledge.trinity.dictionary;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.sparql.Literal;
import com.daedafusion.knowledge.trinity.util.Cache;
import com.daedafusion.knowledge.trinity.util.MemoryOnlyEhcache;

/**
 * Created by mphilpot on 7/10/14.
 */
public class GlobalDictionaryCache
{
    private static GlobalDictionaryCache ourInstance = new GlobalDictionaryCache();

    public static GlobalDictionaryCache getInstance()
    {
        return ourInstance;
    }

    private Cache<Long, String> resDictCache;
    private Cache<Long, String> pDictCache;
    private Cache<Long, Literal> lDictCache;

    private GlobalDictionaryCache()
    {
        resDictCache = new MemoryOnlyEhcache<>(Configuration.getInstance().getString("dictionary.resdict.cache.size", "200m"), "resDictCache");
        pDictCache = new MemoryOnlyEhcache<>(Configuration.getInstance().getString("dictionary.pdict.cache.size", "10m"), "pDictCache");
        lDictCache = new MemoryOnlyEhcache<>(Configuration.getInstance().getString("dictionary.ldict.cache.size", "200m"), "lDictCache");
    }

    public Cache<Long, String> getResourceCache()
    {
        return resDictCache;
    }

    public Cache<Long, String> getPredicateCache()
    {
        return pDictCache;
    }

    public Cache<Long, Literal> getLiteralCache() { return lDictCache; }
}
