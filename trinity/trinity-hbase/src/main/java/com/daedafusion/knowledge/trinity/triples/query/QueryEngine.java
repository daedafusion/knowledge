package com.daedafusion.knowledge.trinity.triples.query;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * Created by mphilpot on 7/8/14.
 */
public class QueryEngine implements Closeable
{
    private static final Logger log = Logger.getLogger(QueryEngine.class);
    private final ObjectMapper mapper;

    public static final String REIFIED_NAMED_GRAPH = "urn:reified";

    private QueryContext context;
    private QueryReifiedContext rContext;

    private QueryContext currentContext = null;

    private Dataset      dataset;
    private Dataset     rDataset;

    public static QueryEngine getInstance() throws IOException
    {
        QueryEngine engine = new QueryEngine();
        engine.init();
        return engine;
    }

    protected QueryEngine()
    {
        this.mapper = new ObjectMapper();
    }

    protected void init() throws IOException
    {
        JenaAdapter adapter = new JenaAdapter();

        context = new QueryContext();
        context.init();

        adapter.init(context);

        Model model = ModelFactory.createModelForGraph(adapter);

        dataset = DatasetFactory.create(model);

        JenaAdapter rAdapter = new JenaAdapter();

        rContext = new QueryReifiedContext();
        rContext.init();

        rAdapter.init(rContext);

        Model rModel = ModelFactory.createModelForGraph(rAdapter);

        rDataset = DatasetFactory.create(rModel);
    }

    public Model graphQuery(com.daedafusion.knowledge.trinity.Query q) throws IOException
    {
        Query arqQuery = QueryFactory.create(q.getQuery());

        Dataset currentDataset;

        // Determine dataset and context
        if(arqQuery.getNamedGraphURIs().contains(REIFIED_NAMED_GRAPH))
        {
            currentDataset = rDataset;
            currentContext = rContext;
        }
        else
        {
            currentDataset = dataset;
            currentContext = context;
        }

        currentContext.clearContext();

        currentContext.setQuery(q);

        if(q.getCursor() != null)
        {
            Map<String, Map<String, String>> cursorMap = mapper.readValue(q.getCursor(), new TypeReference<Map<String, Map<String, String>>>()
            {
            });
            currentContext.setCursorMap(cursorMap);
        }

        if(q.getLimit() != null)
        {
            arqQuery.setLimit(q.getLimit());
        }
        if(q.getOffset() != null)
        {
            arqQuery.setOffset(q.getOffset());
        }

        QueryExecution qe = QueryExecutionFactory.create(arqQuery, currentDataset);

        return qe.execConstruct();
    }

    public ResultSet execSelect(com.daedafusion.knowledge.trinity.Query q) throws IOException
    {
        Query arqQuery = QueryFactory.create(q.getQuery());

        Dataset currentDataset;

        // Determine dataset and context
        if(arqQuery.getNamedGraphURIs().contains(REIFIED_NAMED_GRAPH))
        {
            currentDataset = rDataset;
            currentContext = rContext;
        }
        else
        {
            currentDataset = dataset;
            currentContext = context;
        }

        currentContext.clearContext();

        currentContext.setQuery(q);

        if(q.getCursor() != null)
        {
            Map<String, Map<String, String>> cursorMap = mapper.readValue(q.getCursor(), new TypeReference<Map<String, Map<String, String>>>()
            {
            });
            currentContext.setCursorMap(cursorMap);
        }

        if(q.getLimit() != null)
        {
            arqQuery.setLimit(q.getLimit());
        }
        if(q.getOffset() != null)
        {
            arqQuery.setOffset(q.getOffset());
        }

        QueryExecution qe = QueryExecutionFactory.create(arqQuery, currentDataset);

        return qe.execSelect();
    }

    public QueryContext getContext()
    {
        return currentContext;
    }

    @Override
    public void close() throws IOException
    {
        dataset.close();
        rDataset.close();
        context.close();
        rContext.close();
    }
}
