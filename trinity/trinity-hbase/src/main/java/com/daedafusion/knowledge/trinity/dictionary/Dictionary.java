package com.daedafusion.knowledge.trinity.dictionary;

import com.daedafusion.knowledge.trinity.util.HashBytes;
import com.daedafusion.sparql.Literal;
import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.util.Hash;
import com.daedafusion.knowledge.trinity.util.Cache;
import com.daedafusion.knowledge.trinity.conf.Schema;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by mphilpot on 7/9/14.
 */
public class Dictionary implements Closeable
{
    private static final Logger log = Logger.getLogger(Dictionary.class);

    protected Map<TableName, Table> tables;
    protected Map<TableName, BufferedMutator> mutators;

//    protected Table resDict;
//    protected Table pDict;
//    protected Table plDict;

    protected boolean buffered;

    protected Cache<HashBytes, String> resDictCache;
    protected Cache<HashBytes, String> pDictCache;
    protected Cache<HashBytes, Literal> lDictCache;

    public Dictionary()
    {
        tables = new HashMap<>();
        mutators = new HashMap<>();
    }

    public void init(boolean buffered) throws IOException
    {
        this.buffered = buffered;

        tables.put(Schema.RESDICT, HBasePool.getInstance().getTable(Schema.RESDICT));
        tables.put(Schema.PDICT, HBasePool.getInstance().getTable(Schema.PDICT));
        tables.put(Schema.PLDICT, HBasePool.getInstance().getTable(Schema.PLDICT));

        mutators.put(Schema.RESDICT, HBasePool.getInstance().getBufferedMutator(Schema.RESDICT));
        mutators.put(Schema.PDICT, HBasePool.getInstance().getBufferedMutator(Schema.PDICT));
        mutators.put(Schema.PLDICT, HBasePool.getInstance().getBufferedMutator(Schema.PLDICT));

        initializeCaches();
    }

    protected void initializeCaches()
    {
        // Use common dictionary cache for all instances in jvm (hopefully improve performance)
        resDictCache = GlobalDictionaryCache.getInstance().getResourceCache();
        pDictCache = GlobalDictionaryCache.getInstance().getPredicateCache();
        lDictCache = GlobalDictionaryCache.getInstance().getLiteralCache();
    }

    private void applyPut(TableName name, Put put) throws IOException
    {
        if(buffered)
        {
            mutators.get(name).mutate(put);
        }
        else
        {
            tables.get(name).put(put);
        }
    }

    public void setPredicateLiteral(HashBytes predicateHash, String literal)
    {
        // TODO may need configurable logic to limit size of key by truncating literal
        Put put = new Put(Bytes.add(predicateHash.getBytes(), Bytes.toBytes(literal)));
        put.addColumn(Schema.F_DICTIONARY, Schema.Q_DVALUE, Bytes.toBytes(literal));
        try
        {
            applyPut(Schema.PLDICT, put);
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
    public Set<String> getPredicateLiterals(HashBytes predicateHash, String literalPrefix)
    {
        Set<String> result = new HashSet<>();

        Scan scan = new Scan();
        scan.setStartRow(Bytes.add(predicateHash.getBytes(), Bytes.toBytes(literalPrefix)));
        //scan.setStopRow(Bytes.add(Bytes.toBytes(predicateHash), Bytes.toBytes(literalPrefix+" ")));
        scan.setCaching(100); // TODO make configurable

        int length = predicateHash.getBytes().length;

        try(ResultScanner scanner = tables.get(Schema.PLDICT).getScanner(scan))
        {
            for (Result row : scanner)
            {
                String value = Bytes.toString(row.getValue(Schema.F_DICTIONARY, Schema.Q_DVALUE));

                if(Bytes.compareTo(row.getRow(), 0, length, predicateHash.getBytes(), 0, length) != 0 ||
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

    public boolean isCached(HashBytes hash)
    {
        return resDictCache.contains(hash) || pDictCache.contains(hash) || lDictCache.contains(hash);
    }

    public void cacheResource(HashBytes hash, String resource)
    {
        resDictCache.put(hash, resource);
    }

    public void cachePredicate(HashBytes hash, String predicate)
    {
        pDictCache.put(hash, predicate);
    }

    public void cacheLiteral(HashBytes hash, Literal literal)
    {
        lDictCache.put(hash, literal);
    }

    public String getResource(HashBytes hash)
    {
        String resource = resDictCache.get(hash);

        if(resource == null)
        {
            try
            {
                Result result = tables.get(Schema.RESDICT).get(new Get(hash.getBytes()));
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

    public Literal getLiteral(HashBytes hash)
    {
        Literal literal = lDictCache.get(hash);

        if(literal == null)
        {
            try
            {
                literal = new Literal();
                Result result = tables.get(Schema.RESDICT).get(new Get(hash.getBytes()));
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

    public String getPredicate(HashBytes hash)
    {
        String predicate = pDictCache.get(hash);

        if(predicate == null)
        {
            try
            {
                Result result = tables.get(Schema.PDICT).get(new Get(hash.getBytes()));
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

    public void setResource(HashBytes hash, String resource)
    {
        if(resDictCache.get(hash) != null)
        {
            return;
        }

        try
        {
            Put put = new Put(hash.getBytes());
            put.addColumn(Schema.F_DICTIONARY, Schema.Q_DVALUE, Bytes.toBytes(resource));
            applyPut(Schema.RESDICT, put);
            resDictCache.put(hash, resource);
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    public void setLiteral(HashBytes hash, Literal literal)
    {
        if(lDictCache.get(hash) != null)
        {
            return;
        }

        try
        {
            Put put = new Put(hash.getBytes());
            put.addColumn(Schema.F_DICTIONARY, Schema.Q_DVALUE, Bytes.toBytes(literal.value));

            if(literal.type != null)
                put.addColumn(Schema.F_DICTIONARY, Schema.Q_DTYPE, Bytes.toBytes(literal.type));
            else if(literal.lang != null)
                put.addColumn(Schema.F_DICTIONARY, Schema.Q_DLANG, Bytes.toBytes(literal.lang));

            applyPut(Schema.RESDICT, put);
            lDictCache.put(hash, literal);
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    public void setPredicate(HashBytes hash, String predicate)
    {
        if(pDictCache.get(hash) != null)
        {
            return;
        }

        try
        {
            Put put = new Put(hash.getBytes());
            put.addColumn(Schema.F_DICTIONARY, Schema.Q_DVALUE, Bytes.toBytes(predicate));
            applyPut(Schema.PDICT, put);
            pDictCache.put(hash, predicate);
        }
        catch (IOException e)
        {
            log.error("", e);
        }
    }

    @Override
    public void close() throws IOException
    {
        log.info("Closing Dictionary");

        for(Map.Entry<TableName, Table> entry : tables.entrySet())
        {
            entry.getValue().close();
        }
        for(Map.Entry<TableName, BufferedMutator> entry : mutators.entrySet())
        {
            entry.getValue().close();
        }
    }

    public boolean isCachedLiteral(HashBytes objectHash)
    {
        return lDictCache.contains(objectHash);
    }
}
