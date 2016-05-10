package com.daedafusion.knowledge.query.services.util;

import com.daedafusion.sparql.Head;
import com.daedafusion.sparql.Results;
import com.daedafusion.sparql.SparqlResults;
import com.daedafusion.knowledge.query.framework.QueryResult;
import com.daedafusion.knowledge.trinity.TripleMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Statement;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by mphilpot on 9/5/14.
 */
public class SparqlResultsRenderer
{
    private static final Logger log = Logger.getLogger(SparqlResultsRenderer.class);

    public static SparqlResults renderResultSet(QueryResult queryResult) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        SparqlResults results = new SparqlResults();
        results.setResults(new Results());
        results.setHead(new Head());
        results.getHead().setVars(new ArrayList<>());
//        results.getHead().getVars().add("s");
//        results.getHead().getVars().add("p");
//        results.getHead().getVars().add("o");
//        results.getHead().getVars().add("?");
        results.getHead().setLinks(new HashMap<>());
        results.getHead().getLinks().put("cursor", mapper.writeValueAsString(queryResult.getCursor()));

        Set<String> vars = new HashSet<>();

        for(ResultSet set : queryResult.getResultSets())
        {
            vars.addAll(set.getResultVars());

            for(;set.hasNext();)
            {
                Map<String, Map<String, String>> binding = new HashMap<>();

                QuerySolution sol = set.nextSolution();

                for(String v : set.getResultVars())
                {
                    binding.put(v, new HashMap<>());

                    if(sol.get(v).isLiteral())
                    {
                        binding.get(v).put("type", "literal");
                        binding.get(v).put("value", sol.get(v).asLiteral().getLexicalForm());

                        if(sol.get(v).asLiteral().getDatatypeURI() != null &&
                                !sol.get(v).asLiteral().getDatatypeURI().equals(""))
                        {
                            binding.get(v).put("datatype", sol.get(v).asLiteral().getDatatypeURI());
                        }
                        if(sol.get(v).asLiteral().getLanguage() != null &&
                                !sol.get(v).asLiteral().getLanguage().equals(""))
                        {
                            binding.get(v).put("xml:lang", sol.get(v).asLiteral().getLanguage());
                        }
                    }
                    else if(sol.get(v).isResource())
                    {
                        binding.get(v).put("type", "uri");
                        binding.get(v).put("value", sol.get(v).asResource().getURI());
                    }
                }

                results.getResults().getBindings().add(binding);
            }
        }

        results.getHead().getVars().addAll(vars);

        return results;
    }

    public static SparqlResults renderModel(QueryResult queryResult) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        SparqlResults results = new SparqlResults();
        results.setResults(new Results());
        results.setHead(new Head());
        results.getHead().setVars(new ArrayList<>());
        results.getHead().getVars().add("s");
        results.getHead().getVars().add("p");
        results.getHead().getVars().add("o");
        results.getHead().getVars().add("?");
        results.getHead().setLinks(new HashMap<>());
        results.getHead().getLinks().put("cursor", mapper.writeValueAsString(queryResult.getCursor()));

        for(Statement statement : queryResult.getModel().listStatements().toSet())
        {
            Triple t = statement.asTriple();

            Map<String, TripleMeta> metaData = new HashMap<>();
            if(!queryResult.getMetaData().containsKey(t.hashCode()))
            {
                log.warn("Missing triple metadata object");
            }
            else
            {
                for(TripleMeta tm : queryResult.getMetaData().get(t.hashCode()))
                {
                    metaData.put(tm.getPartition(), tm);
                }
            }

            Map<String, Map<String, String>> binding = new HashMap<>();
            binding.put("s", new HashMap<>());
            binding.put("p", new HashMap<>());
            binding.put("o", new HashMap<>());
            binding.put("?", new HashMap<>());

            if(statement.getSubject().isAnon())
            {
                binding.get("s").put("type", "bnode");
                binding.get("s").put("value", statement.getSubject().toString());
            }
            else
            {
                binding.get("s").put("type", "uri");
                binding.get("s").put("value", statement.getSubject().getURI());
            }

            binding.get("p").put("type", "uri");
            binding.get("p").put("value", statement.getPredicate().getURI());

            if(statement.getObject().isAnon())
            {
                binding.get("o").put("type", "bnode");
                binding.get("o").put("value", statement.getObject().toString());
            }
            else if(statement.getObject().isLiteral())
            {
                binding.get("o").put("type", "literal");
                binding.get("o").put("value", statement.getObject().asLiteral().getLexicalForm());

                if(statement.getObject().asLiteral().getDatatypeURI() != null &&
                        !statement.getObject().asLiteral().getDatatypeURI().equals(""))
                {
                    binding.get("o").put("datatype", statement.getObject().asLiteral().getDatatypeURI());
                }
                if(statement.getObject().asLiteral().getLanguage() != null &&
                        !statement.getObject().asLiteral().getLanguage().equals(""))
                {
                    binding.get("o").put("xml:lang", statement.getObject().asLiteral().getLanguage());
                }
            }
            else
            {
                binding.get("o").put("type", "uri");
                binding.get("o").put("value", statement.getObject().asResource().getURI());
            }

            binding.get("?").put("type", "application/json");
            binding.get("?").put("value", mapper.writeValueAsString(metaData));

            results.getResults().getBindings().add(binding);
        }

        return results;
    }
}
