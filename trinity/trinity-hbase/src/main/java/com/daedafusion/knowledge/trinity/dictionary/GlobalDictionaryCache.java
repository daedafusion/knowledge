package com.daedafusion.knowledge.trinity.dictionary;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.knowledge.trinity.util.HashBytes;
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

    private Cache<HashBytes, String> resDictCache;
    private Cache<HashBytes, String> pDictCache;
    private Cache<HashBytes, Literal> lDictCache;

    private GlobalDictionaryCache()
    {
        resDictCache = new MemoryOnlyEhcache<>(Configuration.getInstance().getString("dictionary.resdict.cache.size", "20m"), "resDictCache");
        pDictCache = new MemoryOnlyEhcache<>(Configuration.getInstance().getString("dictionary.pdict.cache.size", "10m"), "pDictCache");
        lDictCache = new MemoryOnlyEhcache<>(Configuration.getInstance().getString("dictionary.ldict.cache.size", "20m"), "lDictCache");
    }

    public Cache<HashBytes, String> getResourceCache()
    {
        return resDictCache;
    }

    public Cache<HashBytes, String> getPredicateCache()
    {
        return pDictCache;
    }

    public Cache<HashBytes, Literal> getLiteralCache() { return lDictCache; }
}
