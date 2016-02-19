package com.daedafusion.knowledge.query.services;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.sparql.SparqlResults;
import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import com.daedafusion.knowledge.query.framework.Converter;
import com.daedafusion.knowledge.query.framework.QueryManager;
import com.daedafusion.knowledge.query.framework.QueryResult;
import com.daedafusion.knowledge.query.services.exceptions.ServiceException;
import com.daedafusion.knowledge.query.services.util.SparqlResultsRenderer;
import com.daedafusion.knowledge.trinity.Query;
import com.daedafusion.knowledge.trinity.TripleMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

/**
 * Created by mphilpot on 7/18/14.
 */
@Path("query")
public class QueryService
{
    private static final Logger log = Logger.getLogger(QueryService.class);

    @POST
    @Path("{domain}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_RESULTS)
    public SparqlResults knowledgeQuery(@PathParam("domain") String domain,
                                        Query query)
    {
        if(query.getQueryType().equals(Query.KNOWLEDGE_QUERY))
        {
            query.setQuery(convertQuery(domain, query.getQuery()));
            query.setQueryType(Query.SPARQL_QUERY);
        }

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        QueryResult result;

        if(query.getPartitions().isEmpty())
        {
            log.warn("Query with no partitions");
            throw new BadRequestException("No partitions specified");
        }

        result = queryManager.query(query);

        SparqlResults sr = null;
        try
        {
            sr = SparqlResultsRenderer.renderModel(result);
        }
        catch (JsonProcessingException e)
        {
            log.error("", e);
            throw new ServiceException();
        }

        return sr;
    }

    @POST
    @Path("convert/{domain}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_QUERY)
    public String convertQuery(@PathParam("domain") String domain, String knowledgeQuery)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        Converter converter = framework.getService(Converter.class);

        return converter.convert(domain, knowledgeQuery);
    }

    @POST
    @Path("literal/prefix")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> literalPrefixLookup(Map<String, Object> parameters)
    {
        List<String> predicates = (List<String>) parameters.get("predicates");
        String literalPrefix = (String) parameters.get("literalPrefix");

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        try
        {
            return queryManager.literalPrefixLookup(predicates, literalPrefix);
        }
        catch (IOException e)
        {
            log.error("", e);
            throw new ServiceException();
        }
    }

    @POST
    @Path("spider")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_RESULTS)
    public SparqlResults spider(Query query, @QueryParam("depth") @DefaultValue("0") Integer depth)
    {
        if(!query.getQueryType().equals(Query.SPIDER_QUERY))
        {
            throw new BadRequestException("Invalid query type");
        }
        else if(depth > Configuration.getInstance().getInteger("spider.maxDepth", 2))
        {
            throw new BadRequestException(String.format("Depths above %d are not supported", Configuration.getInstance().getInteger("spider.maxDepth", 2)));
        }

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        Set<String> remaining = new HashSet<>();
        int currentDepth = 0;

        Model model = ModelFactory.createDefaultModel();
        Map<Integer, List<TripleMeta>> metadata = new HashMap<>();

        remaining.add(query.getQuery());

        String uuid = UUID.randomUUID().toString();

        do
        {
            log.info(String.format("Spider query %s :: currentDepth %d remaining %d", uuid, currentDepth, remaining.size()));

            Iterator<String> iter = remaining.iterator();

            List<Query> queries = new ArrayList<>();

            while(iter.hasNext())
            {
                String uri = iter.next();
                iter.remove();

                Query q = new Query(query);

                q.setQuery(String.format("construct where { <%s> ?p ?o . }", uri));

                queries.add(q);
            }

            QueryResult result = queryManager.query(queries);

            model.add(result.getModel());
            metadata.putAll(result.getMetaData());

            for(Statement s : result.getModel().listStatements().toSet())
            {
                if(s.getObject().isResource())
                {
                    remaining.add(s.getObject().asResource().getURI());
                }
            }

            currentDepth++;
        }
        while(currentDepth <= depth);

        QueryResult qr = new QueryResult();
        qr.setModel(model);
        qr.setMetaData(metadata);

        SparqlResults sr = null;
        try
        {
            sr = SparqlResultsRenderer.renderModel(qr);
        }
        catch (JsonProcessingException e)
        {
            log.error("", e);
            throw new ServiceException();
        }

        return sr;
    }
}
