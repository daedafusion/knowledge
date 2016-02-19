package com.daedafusion.knowledge.query.framework.providers;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.knowledge.query.framework.QueryResult;
import com.daedafusion.knowledge.trinity.Query;
import com.daedafusion.knowledge.trinity.QueryException;
import com.daedafusion.knowledge.trinity.dictionary.Dictionary;
import com.daedafusion.knowledge.trinity.dictionary.DictionaryPool;
import com.daedafusion.knowledge.trinity.triples.query.MultiQueryEngine;
import com.daedafusion.knowledge.trinity.triples.query.MultiQueryEnginePool;
import com.daedafusion.knowledge.trinity.triples.query.QueryEngine;
import com.daedafusion.knowledge.trinity.triples.query.QueryEnginePool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.pool2.ObjectPool;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by mphilpot on 9/5/14.
 */
public class TrinityQueryManager extends AbstractProvider implements QueryManagerProvider
{
    private static final Logger log = Logger.getLogger(TrinityQueryManager.class);

    private final ObjectMapper                 mapper;
    private final ObjectPool<QueryEngine>      qePool;
    private final ObjectPool<MultiQueryEngine> mqePool;
    private final ObjectPool<Dictionary>       dictPool;

    private final ListeningExecutorService service;

    public TrinityQueryManager()
    {
        mapper = new ObjectMapper();
        qePool = QueryEnginePool.getInstance().getPool();
        mqePool = MultiQueryEnginePool.getInstance().getPool();
        dictPool = DictionaryPool.getInstance().getPool();

        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Configuration.getInstance().getInteger("trinityQueryManager.literalPrefixLookup.threads", 20)));

        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {

            }

            @Override
            public void start()
            {

            }

            @Override
            public void stop()
            {

            }

            @Override
            public void teardown()
            {
                qePool.close();
                mqePool.close();
                dictPool.close();
                service.shutdown();
            }
        });
    }

    @Override
    public QueryResult query(Query query)
    {
        QueryEngine qe = null;
        try
        {
            qe = qePool.borrowObject();

            QueryResult result = new QueryResult();

            Model model = qe.graphQuery(query);

            result.setCursor(mapper.writeValueAsString(qe.getContext().getCursorMap()));
            result.getMetaData().putAll(qe.getContext().getMetadata());
            result.setModel(model);

            return result;
        }
        catch (Exception e)
        {
            log.error("", e);
            throw new QueryException(e);
        }
        finally
        {
            if (qe != null)
            {
                try
                {
                    qePool.returnObject(qe);
                }
                catch (Exception e)
                {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public QueryResult query(List<Query> queryList)
    {
        MultiQueryEngine mqe = null;
        try
        {
            mqe = mqePool.borrowObject();

            QueryResult result = new QueryResult();

            MultiQueryEngine.Result r = mqe.multiQuery(queryList);

            result.setCursor(mapper.writeValueAsString(r.getCursorMap()));
            result.getMetaData().putAll(r.getMetaData());
            result.setModel(r.getModel());

            return result;
        }
        catch (Exception e)
        {
            log.error("", e);
            throw new QueryException(e);
        }
        finally
        {
            if (mqe != null)
            {
                try
                {
                    mqePool.returnObject(mqe);
                }
                catch (Exception e)
                {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public QueryResult select(Query query)
    {
        QueryEngine qe = null;
        try
        {
            qe = qePool.borrowObject();

            QueryResult result = new QueryResult();

            ResultSet rs = qe.execSelect(query);

            result.setCursor(mapper.writeValueAsString(qe.getContext().getCursorMap()));
            result.getMetaData().putAll(qe.getContext().getMetadata());
            result.setResultSets(Collections.singletonList(rs));

            return result;
        }
        catch (Exception e)
        {
            log.error("", e);
            throw new QueryException(e);
        }
        finally
        {
            if (qe != null)
            {
                try
                {
                    qePool.returnObject(qe);
                }
                catch (Exception e)
                {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public QueryResult select(List<Query> queryList)
    {
        MultiQueryEngine mqe = null;
        try
        {
            mqe = mqePool.borrowObject();

            QueryResult result = new QueryResult();

            MultiQueryEngine.Result r = mqe.multiSelect(queryList);

            result.setCursor(mapper.writeValueAsString(r.getCursorMap()));
            result.getMetaData().putAll(r.getMetaData());
            result.setResultSets(r.getResultSet());

            return result;
        }
        catch (Exception e)
        {
            log.error("", e);
            throw new QueryException(e);
        }
        finally
        {
            if (mqe != null)
            {
                try
                {
                    mqePool.returnObject(mqe);
                }
                catch (Exception e)
                {
                    log.error("", e);
                }
            }
        }
    }

    private class LiteralPrefixCallable implements Callable<Set<String>>
    {
        private final String predicate;
        private final String prefix;

        public LiteralPrefixCallable(String predicate, String prefix)
        {
            this.predicate = predicate;
            this.prefix = prefix;
        }

        @Override
        public Set<String> call() throws Exception
        {
            Dictionary dict = null;
            try
            {
                dict = dictPool.borrowObject();
                return dict.getPredicateLiterals(predicate, prefix);
            }
            finally
            {
                if(dict != null)
                {
                    dictPool.returnObject(dict);
                }
            }
        }
    }

    @Override
    public List<String> literalPrefixLookup(List<String> predicates, String literalPrefix) throws IOException
    {
        List<ListenableFuture<Set<String>>> futures = new ArrayList<>();

        for(String p : predicates)
        {
            futures.add(service.submit(new LiteralPrefixCallable(p, literalPrefix)));
        }

        ListenableFuture<List<Set<String>>> fanIn = Futures.allAsList(futures);

        Set<String> result = new HashSet<>();

        try
        {
            List<Set<String>> list = fanIn.get();

            list.forEach(result::addAll);

            return new ArrayList<>(result);
        }
        catch (Exception e)
        {
            log.error("", e);
            return new ArrayList<>();
        }

    }
}
