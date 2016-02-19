package com.daedafusion.knowledge.trinity.triples.query;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.knowledge.trinity.Query;
import com.daedafusion.knowledge.trinity.TripleMeta;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by mphilpot on 8/25/14.
 */
public class MultiQueryEngine implements Closeable
{
    private static final Logger log = Logger.getLogger(MultiQueryEngine.class);

    private final QueryEnginePool pool;
    private final ListeningExecutorService service;

    private MultiQueryEngine()
    {
        pool = QueryEnginePool.getInstance();
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Configuration.getInstance().getInteger("multiQueryEngine.threads", 10)));
    }

    public static MultiQueryEngine getInstance()
    {
        MultiQueryEngine mqe = new MultiQueryEngine();
        mqe.init();
        return mqe;
    }

    private void init()
    {

    }

    public class Result
    {
        private Model                          model;
        private Map<String, Map<String, String>> cursorMap;
        private Map<Integer, List<TripleMeta>> metaData;
        private List<ResultSet> resultSet;

        public Result()
        {
            metaData = new HashMap<>();
            cursorMap = new HashMap<>();
            resultSet = new ArrayList<>();
        }

        public Model getModel()
        {
            return model;
        }

        public Map<String, Map<String, String>> getCursorMap()
        {
            return cursorMap;
        }

        public Map<Integer, List<TripleMeta>> getMetaData()
        {
            return metaData;
        }

        public List<ResultSet> getResultSet()
        {
            return resultSet;
        }
    }

    private class QueryCallable implements Callable<Result>
    {
        private final Query q;

        public QueryCallable(Query q)
        {
            this.q = q;
        }


        @Override
        public Result call() throws Exception
        {
            QueryEngine engine = pool.getPool().borrowObject();
            try
            {
                Model model = engine.graphQuery(q);

                Result r = new Result();
                r.model = model;
                r.cursorMap.putAll(engine.getContext().getCursorMap());
                r.metaData.putAll(engine.getContext().getMetadata());

                return r;
            }
            finally
            {
                if (engine != null)
                {
                    pool.getPool().returnObject(engine);
                }
            }
        }
    }

    private class SelectCallable implements Callable<Result>
    {
        private final Query q;

        public SelectCallable(Query q)
        {
            this.q = q;
        }


        @Override
        public Result call() throws Exception
        {
            QueryEngine engine = pool.getPool().borrowObject();
            try
            {
                ResultSet rs = engine.execSelect(q);

                Result r = new Result();
                r.resultSet = Collections.singletonList(rs);
                r.cursorMap.putAll(engine.getContext().getCursorMap());
                r.metaData.putAll(engine.getContext().getMetadata());

                return r;
            }
            finally
            {
                if (engine != null)
                {
                    pool.getPool().returnObject(engine);
                }
            }
        }
    }

    public Result multiQuery(List<Query> queries) throws ExecutionException, InterruptedException
    {
        List<ListenableFuture<Result>> futures = new ArrayList<>();

        for (Query q : queries)
        {
            futures.add(service.submit(new QueryCallable(q)));
        }

        ListenableFuture<List<Result>> fanIn = Futures.allAsList(futures);

        List<Result> list = fanIn.get();

        Result result = new Result();
        result.model = ModelFactory.createDefaultModel();

        for (Result r : list)
        {
            result.model.add(r.model);
            result.cursorMap.putAll(r.cursorMap);

            for(Map.Entry<Integer, List<TripleMeta>> entry : r.getMetaData().entrySet())
            {
                if(result.metaData.containsKey(entry.getKey()))
                {
                    result.metaData.get(entry.getKey()).addAll(entry.getValue());
                }
                else
                {
                    result.metaData.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return result;
    }

    public Result multiSelect(List<Query> queries) throws ExecutionException, InterruptedException
    {
        List<ListenableFuture<Result>> futures = new ArrayList<>();

        for (Query q : queries)
        {
            futures.add(service.submit(new SelectCallable(q)));
        }

        ListenableFuture<List<Result>> fanIn = Futures.allAsList(futures);

        List<Result> list = fanIn.get();

        Result result = new Result();
        result.model = ModelFactory.createDefaultModel();

        for (Result r : list)
        {
            result.resultSet.addAll(r.resultSet);

            result.cursorMap.putAll(r.cursorMap);

            for(Map.Entry<Integer, List<TripleMeta>> entry : r.getMetaData().entrySet())
            {
                if(result.metaData.containsKey(entry.getKey()))
                {
                    result.metaData.get(entry.getKey()).addAll(entry.getValue());
                }
                else
                {
                    result.metaData.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return result;
    }

    public Result multiQuery(Query... queries) throws ExecutionException, InterruptedException
    {
        List<Query> list = new ArrayList<>();
        Collections.addAll(list, queries);
        return multiQuery(list);
    }

    @Override
    public void close() throws IOException
    {
        pool.getPool().close();
        service.shutdown();
    }
}
