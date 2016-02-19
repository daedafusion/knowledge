package com.daedafusion.knowledge.query.services;

import com.daedafusion.sparql.SparqlResults;
import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import com.daedafusion.knowledge.query.framework.QueryManager;
import com.daedafusion.knowledge.query.framework.QueryResult;
import com.daedafusion.knowledge.query.services.exceptions.ServiceException;
import com.daedafusion.knowledge.query.services.util.SparqlResultsRenderer;
import com.daedafusion.knowledge.trinity.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Created by mphilpot on 7/18/14.
 */
@Path("describe")
public class DescribeService
{
    private static final Logger log = Logger.getLogger(DescribeService.class);

    // TODO add limits
    private static final String DESCRIBE_QUERY = "construct where { <%s> ?p ?o . }";
    private static final String REFERENCED_QUERY = "construct where { ?s ?p <%s> . }";
    private static final String INCOMING_PREDICATES = "select distinct ?p ?s where { ?s ?p <%s> . }";
    private static final String OUTGOING_PREDICATES = "select distinct ?p ?o where { <%s> ?p ?o . }";
    private static final String INCOMING_PREDICATE_TRACE = "construct where { ?s <%s> <%s> . }";
    private static final String OUTGOING_PREDICATE_TRACE = "construct where { <%s> <%s> ?o . }";

    private static final String DISTINCT_PREDICATE_VALUES = "select distinct ?o where { ?s <%s> ?o . }";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_RESULTS)
    public SparqlResults describe(@QueryParam("partition") List<String> partitions,
                                  List<String> subjectUriList)
    {

        List<Query> queries = new ArrayList<>();

        for(String uri : subjectUriList)
        {
            Query q = new Query();
            q.setLimit(null);
            q.setPartitions(new HashSet<>(partitions));
            q.setQuery(String.format(DESCRIBE_QUERY, uri));
            queries.add(q);
        }

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        QueryResult result = queryManager.query(queries);

        SparqlResults sr;
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
    @Path("referenced")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_RESULTS)
    public SparqlResults referenced(@QueryParam("partition") List<String> partitions,
                                    List<String> subjectUriList)
    {
        List<Query> queries = new ArrayList<>();

        for(String uri : subjectUriList)
        {
            Query q = new Query();
            q.setLimit(null);
            q.setPartitions(new HashSet<>(partitions));
            q.setQuery(String.format(REFERENCED_QUERY, uri));
            queries.add(q);
        }

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        QueryResult result = queryManager.query(queries);

        SparqlResults sr;
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
    @Path("predicates/incoming")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_RESULTS)
    public SparqlResults predicatesIncoming(@QueryParam("partition") List<String> partitions,
                                            List<String> subjectUriList)
    {
        List<Query> queries = new ArrayList<>();

        for(String uri : subjectUriList)
        {
            Query q = new Query();
            q.setLimit(null);
            q.setPartitions(new HashSet<>(partitions));
            q.setQuery(String.format(INCOMING_PREDICATES, uri));
            queries.add(q);
        }

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        QueryResult result = queryManager.select(queries);

        SparqlResults sr;
        try
        {
            sr = SparqlResultsRenderer.renderResultSet(result);
        }
        catch (JsonProcessingException e)
        {
            log.error("", e);
            throw new ServiceException();
        }

        return sr;
    }

    @POST
    @Path("predicates/outgoing")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_RESULTS)
    public SparqlResults predicatesOutgoing(@QueryParam("partition") List<String> partitions,
                                            List<String> subjectUriList)
    {
        List<Query> queries = new ArrayList<>();

        for(String uri : subjectUriList)
        {
            Query q = new Query();
            q.setLimit(null);
            q.setPartitions(new HashSet<>(partitions));
            q.setQuery(String.format(OUTGOING_PREDICATES, uri));
            queries.add(q);
        }

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        QueryResult result = queryManager.select(queries);

        SparqlResults sr;
        try
        {
            sr = SparqlResultsRenderer.renderResultSet(result);
        }
        catch (JsonProcessingException e)
        {
            log.error("", e);
            throw new ServiceException();
        }

        return sr;
    }

    @POST
    @Path("predicates/trace/incoming")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_RESULTS)
    public SparqlResults traceIncoming(@QueryParam("partition") List<String> partitions,
                                       Map<String, String> subjectPredicatePairs)
    {
        List<Query> queries = new ArrayList<>();

        for(Map.Entry<String, String> entry : subjectPredicatePairs.entrySet())
        {
            Query q = new Query();
            q.setLimit(null);
            q.setPartitions(new HashSet<>(partitions));
            q.setQuery(String.format(INCOMING_PREDICATE_TRACE, entry.getValue(), entry.getKey()));
            queries.add(q);
        }

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        QueryResult result = queryManager.query(queries);

        SparqlResults sr;
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
    @Path("predicates/trace/outgoing")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CustomHeaders.SPARQL_RESULTS)
    public SparqlResults traceOutgoing(@QueryParam("partition") List<String> partitions,
                                       Map<String, String> subjectPredicatePairs)
    {
        List<Query> queries = new ArrayList<>();

        for(Map.Entry<String, String> entry : subjectPredicatePairs.entrySet())
        {
            Query q = new Query();
            q.setLimit(null);
            q.setPartitions(new HashSet<>(partitions));
            q.setQuery(String.format(OUTGOING_PREDICATE_TRACE, entry.getKey(), entry.getValue()));
            queries.add(q);
        }

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        QueryResult result = queryManager.query(queries);

        SparqlResults sr;
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
    @Path("predicate/values")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> distinctPredicateValues(@QueryParam("partition") List<String> partitions,
                                                String predicateUri)
    {
        List<Query> queries = new ArrayList<>();

        Query q = new Query();
        q.setLimit(null);
        q.setPartitions(new HashSet<>(partitions));
        q.setQuery(String.format(DISTINCT_PREDICATE_VALUES, predicateUri));
        queries.add(q);

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        QueryManager queryManager = framework.getService(QueryManager.class);

        QueryResult result = queryManager.select(queries);

        Set<String> values = new HashSet<>();

        for(ResultSet rs : result.getResultSets())
        {
            while (rs.hasNext())
            {
                QuerySolution sol = rs.next();
                values.add(sol.getLiteral("o").getLexicalForm());
            }
        }

        return values;
    }
}
