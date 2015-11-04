package com.daedafusion.knowledge.trinity.dictionary;

import com.daedafusion.sparql.Literal;
import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.util.Hash;
import com.daedafusion.knowledge.trinity.util.Cache;
import com.daedafusion.knowledge.trinity.conf.Schema;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mphilpot on 7/9/14.
 */
public class Dictionary implements Closeable
{
    private static final Logger log = Logger.getLogger(Dictionary.class);

    protected HTableInterface resDict;
    protected HTableInterface pDict;
    protected HTableInterface plDict;

    protected Cache<Long, String> resDictCache;
    protected Cache<Long, String> pDictCache;
    protected Cache<Long, Literal> lDictCache;

    public Dictionary()
    {

    }

    public void init() throws IOException
    {
        resDict = HBasePool.getInstance().getTable(Schema.RESDICT);
        pDict = HBasePool.getInstance().getTable(Schema.PDICT);
        plDict = HBasePool.getInstance().getTable(Schema.PLDICT);

        resDict.setAutoFlushTo(false);
        pDict.setAutoFlushTo(false);
        plDict.setAutoFlushTo(false);

        initializeCaches();
    }

    public void setAutoFlush(boolean autoFlush)
    {
        resDict.setAutoFlushTo(autoFlush);
        pDict.setAutoFlushTo(autoFlush);
        plDict.setAutoFlushTo(autoFlush);
    }

    protected void initializeCaches()
    {
        // Use common dictionary cache for all instances in jvm (hopefully improve performance)
        resDictCache = GlobalDictionaryCache.getInstance().getResourceCache();
        pDictCache = GlobalDictionaryCache.getInstance().getPredicateCache();
        lDictCache = GlobalDictionaryCache.getInstance().getLiteralCache();
    }

    public void setPredicateLiteral(Long predicateHash, String literal)
    {
        // TODO may need configurable logic to limit size of key by truncating literal
        Put put = new Put(Bytes.add(Bytes.toBytes(predicateHash), Bytes.toBytes(literal)));
        put.add(Schema.F_DICTIONARY, Schema.Q_DVALUE, Bytes.toBytes(literal));
        try
        {
            plDict.put(put);
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    public Set<String> getPredicateLiterals(String predicate, String literalPrefix)
    {
        return getPredicateLiterals(Hash.hashString(predicate), literalPrefix);
    }

    /**
     * TODO This scan is one large hack.  I should be able to do it with stopRow but I can't figure it out with strings
     *
     * TODO -- This should probably be partition scoped, but that will probably make the performance even worse?
     *
     * @param predicateHash
     * @param literalPrefix
     * @return
     */
    public Set<String> getPredicateLiterals(Long predicateHash, String literalPrefix)
    {
        Set<String> result = new HashSet<>();

        Scan scan = new Scan();
        scan.setStartRow(Bytes.add(Bytes.toBytes(predicateHash), Bytes.toBytes(literalPrefix)));
        //scan.setStopRow(Bytes.add(Bytes.toBytes(predicateHash), Bytes.toBytes(literalPrefix+" ")));
        scan.setCaching(100); // TODO make configurable

        int length = Bytes.toBytes(predicateHash).length;

        try(ResultScanner scanner = plDict.getScanner(scan))
        {
            for (Result row : scanner)
            {
                String value = Bytes.toString(row.getValue(Schema.F_DICTIONARY, Schema.Q_DVALUE));

                if(Bytes.compareTo(row.getRow(), 0, length, Bytes.toBytes(predicateHash), 0, length) != 0 ||
                        (value != null && !value.startsWith(literalPrefix)))
                {
                    break;
                }

                result.add(value);

                if(result.size() > 100)
                {
                    break;
                }
            }
        }
        catch (IOException e)
        {
            log.error("", e);
        }

        return result;
    }

    public boolean isCached(Long hash)
    {
        return resDictCache.contains(hash) || pDictCache.contains(hash) || lDictCache.contains(hash);
    }

    public void cacheResource(Long hash, String resource)
    {
        resDictCache.put(hash, resource);
    }

    public void cachePredicate(Long hash, String predicate)
    {
        pDictCache.put(hash, predicate);
    }

    public void cacheLiteral(Long hash, Literal literal)
    {
        lDictCache.put(hash, literal);
    }

    public String getResource(Long hash)
    {
        String resource = resDictCache.get(hash);

        if(resource == null)
        {
            try
            {
                Result result = resDict.get(new Get(Bytes.toBytes(hash)));
                resource = Bytes.toString(result.getValue(Schema.F_DICTIONARY, Schema.Q_DVALUE));
                resDictCache.put(hash, resource);
            }
            catch (IOException e)
            {
                log.error("", e);
            }
        }

        return resource;
    }

    public Literal getLiteral(Long hash)
    {
        Literal literal = lDictCache.get(hash);

        if(literal == null)
        {
            try
            {
                literal = new Literal();
                Result result = resDict.get(new Get(Bytes.toBytes(hash)));
                literal.value = Bytes.toString(result.getValue(Schema.F_DICTIONARY, Schema.Q_DVALUE));

                if(result.containsColumn(Schema.F_DICTIONARY, Schema.Q_DTYPE))
                    literal.type = Bytes.toString(result.getValue(Schema.F_DICTIONARY, Schema.Q_DTYPE));
                else if(result.containsColumn(Schema.F_DICTIONARY, Schema.Q_DLANG))
                    literal.lang = Bytes.toString(result.getValue(Schema.F_DICTIONARY, Schema.Q_DLANG));

                lDictCache.put(hash, literal);
            }
            catch (IOException e)
            {
                log.error("", e);
            }
        }

        return literal;
    }

    public String getPredicate(Long hash)
    {
        String predicate = pDictCache.get(hash);

        if(predicate == null)
        {
            try
            {
                Result result = pDict.get(new Get(Bytes.toBytes(hash)));
                predicate = Bytes.toString(result.getValue(Schema.F_DICTIONARY, Schema.Q_DVALUE));
                pDictCache.put(hash, predicate);
            }
            catch (IOException e)
            {
                log.error("", e);
            }
        }

        return predicate;
    }

    public void setResource(Long hash, String resource)
    {
        if(resDictCache.get(hash) != null)
        {
            return;
        }

        try
        {
            Put put = new Put(Bytes.toBytes(hash));
            put.add(Schema.F_DICTIONARY, Schema.Q_DVALUE, Bytes.toBytes(resource));
            resDict.put(put);
            resDictCache.put(hash, resource);
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    public void setLiteral(Long hash, Literal literal)
    {
        if(lDictCache.get(hash) != null)
        {
            return;
        }

        try
        {
            Put put = new Put(Bytes.toBytes(hash));
            put.add(Schema.F_DICTIONARY, Schema.Q_DVALUE, Bytes.toBytes(literal.value));

            if(literal.type != null)
                put.add(Schema.F_DICTIONARY, Schema.Q_DTYPE, Bytes.toBytes(literal.type));
            else if(literal.lang != null)
                put.add(Schema.F_DICTIONARY, Schema.Q_DLANG, Bytes.toBytes(literal.lang));

            resDict.put(put);
            lDictCache.put(hash, literal);
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    public void setPredicate(Long hash, String predicate)
    {
        if(pDictCache.get(hash) != null)
        {
            return;
        }

        try
        {
            Put put = new Put(Bytes.toBytes(hash));
            put.add(Schema.F_DICTIONARY, Schema.Q_DVALUE, Bytes.toBytes(predicate));
            pDict.put(put);
            pDictCache.put(hash, predicate);
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    public void flush() throws IOException
    {
        resDict.flushCommits();
        pDict.flushCommits();
        plDict.flushCommits();
    }

    @Override
    public void close() throws IOException
    {
        log.info("Closing Dictionary");
        flush();
        resDict.close();
        pDict.close();
        plDict.close();
    }

    public boolean isCachedLiteral(long objectHash)
    {
        return lDictCache.contains(objectHash);
    }
}
