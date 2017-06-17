package com.daedafusion.knowledge.query.framework.providers;

import com.daedafusion.knowledge.ontology.exceptions.NotFoundException;
import com.daedafusion.knowledge.ontology.exceptions.ServiceErrorException;
import com.daedafusion.knowledge.ontology.exceptions.UnauthorizedException;
import com.daedafusion.service.ServiceIdentity;
import com.daedafusion.cache.Cache;
import com.daedafusion.knowledge.query.dsl.QueryParser;
import com.daedafusion.sparql.Literal;
import com.daedafusion.knowledge.ontology.OntologyClient;
import com.daedafusion.knowledge.ontology.model.OntologyModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by mphilpot on 7/17/14.
 */
public class WhereClauseListener extends com.daedafusion.knowledge.query.dsl.QueryBaseListener
{
    private static final Logger log = Logger.getLogger(WhereClauseListener.class);

    private final Cache<String, Model> ontologyCache;

    private static final String PREFIX_ANNOTATION = "http://www.daedafusion.com/editor_annotation#namespacePrefix";

    private String rootOntElement;
    private Set<String> candidateCurrentClasses;

    private List<Set<String>> clauseSets;
    private Set<String>       currentClauses;

    private static String[] var = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
    };

    private int i = 0;

    private String pendingPredicate;
    private String nextVariable;

    //          tag     uri
    private Map<String, String> classUriMap;
    //          uri     tag
    private Map<String, String> classUriMapReverse;

    private Map<String, String> predicateUriMap;

    //          tag         tag     uri
    private Map<String, Map<String, String>> classPredicateUriMap;
    //          tag         tag     tags
    private Map<String, Map<String, Set<String>>> classPredicateRangeMap;

    public WhereClauseListener(Cache<String, Model> ontologyCache)
    {
        this.ontologyCache = ontologyCache;
        currentClauses = new LinkedHashSet<>();
        clauseSets = new ArrayList<>();
        candidateCurrentClasses = new HashSet<>();
    }

    public void reset()
    {
        i = 0;
        clauseSets = new ArrayList<>();
        currentClauses = new LinkedHashSet<>();
        candidateCurrentClasses = new HashSet<>();
        pendingPredicate = null;
        nextVariable = null;
        rootOntElement = null;
    }

    public void init(String domain)
    {
        if (!ontologyCache.contains(domain))
        {
            populateOntologyCache(domain, true);
        }

        Model model = ontologyCache.get(domain);

        classUriMap = new HashMap<>();
        classUriMapReverse = new HashMap<>();
        predicateUriMap = new HashMap<>();
        classPredicateUriMap = new HashMap<>();
        classPredicateRangeMap = new HashMap<>();

        OntologyModel ontModel = new OntologyModel(model);

        for(String clazz : ontModel.getClasses())
        {
            String tag = clazz.substring(clazz.lastIndexOf("#")+1);

            if(classUriMap.containsKey(tag))
            {
                // Reassign existing
                String existingFQN = classUriMap.get(tag);
                String prefix;
                Set<Literal> prefixSet;

                prefixSet = ontModel.getAnnotationProperties(existingFQN).get(PREFIX_ANNOTATION);

                if(prefixSet == null)
                {
                    log.warn(String.format("%s missing %s annotation", existingFQN, PREFIX_ANNOTATION));
                    prefixSet = new HashSet<>();
                }

                if(prefixSet.isEmpty())
                {
                    prefix = "_";
                }
                else
                {
                    // TODO this is probably wrong need to test
                    prefix = prefixSet.iterator().next().value;
                }

                classUriMap.remove(tag);
                classUriMap.put(String.format("%s%s", prefix, tag), existingFQN);

                classUriMapReverse.remove(existingFQN);
                classUriMapReverse.put(existingFQN, String.format("%s%s", prefix, tag));

                Map existingMap = classPredicateUriMap.remove(tag);
                classPredicateUriMap.put(String.format("%s%s", prefix, tag), existingMap);

                existingMap = classPredicateRangeMap.remove(tag);
                classPredicateRangeMap.put(String.format("%s%s", prefix, tag), existingMap);

                prefixSet = ontModel.getAnnotationProperties(clazz).get(PREFIX_ANNOTATION);

                if(prefixSet == null)
                {
                    log.warn(String.format("%s missing %s annotation", clazz, PREFIX_ANNOTATION));
                    prefixSet = new HashSet<>();
                }

                if(prefixSet.isEmpty())
                {
                    prefix = "__";
                }
                else
                {
                    prefix = prefixSet.iterator().next().value;
                }

//                classUriMap.put(String.format("%s%s", prefix, tag), clazz);
                tag = String.format("%s%s", prefix, tag);
            }

            classUriMap.put(tag, clazz);
            classUriMapReverse.put(clazz, tag);
            classPredicateUriMap.put(tag, new HashMap<String, String>());
            classPredicateRangeMap.put(tag, new HashMap<String, Set<String>>());

            for(String dp : ontModel.getDataProperties(clazz))
            {
                String dpTag = dp.substring(dp.lastIndexOf("#")+1);
                classPredicateUriMap.get(tag).put(dpTag, dp);
            }
            for(String op : ontModel.getObjectProperties(clazz))
            {
                String opTag = op.substring(op.lastIndexOf("#")+1);
                classPredicateUriMap.get(tag).put(opTag, op);
                classPredicateRangeMap.get(tag).put(opTag, new HashSet<String>());

                for(String r : ontModel.getRangeOfProperty(clazz, op))
                {
                    // Range needs to start out with uris... then once the collisions have sorted out (tag = prefix+tag)
                    // Then resolve to tag
//                    classPredicateRangeMap.get(tag).get(opTag).add(r.substring(r.lastIndexOf("#")+1));
                    classPredicateRangeMap.get(tag).get(opTag).add(r);
                }
            }
        }

        // Now resolve range uris to tags
        for(Map.Entry<String, Map<String, Set<String>>> _e : classPredicateRangeMap.entrySet())
        {
            for(Map.Entry<String, Set<String>> e : _e.getValue().entrySet())
            {
                Set<String> resolved = new HashSet<>();

                for(String r : e.getValue())
                {
                    resolved.add(classUriMapReverse.get(r));
                }

                e.setValue(resolved);
            }
        }


        for(String uri : ontModel.getDataProperties())
        {
            String tag = uri.substring(uri.lastIndexOf("#")+1);
            predicateUriMap.put(tag, uri);
        }

        for(String uri : ontModel.getObjectProperties())
        {
            String tag = uri.substring(uri.lastIndexOf("#")+1);
            predicateUriMap.put(tag, uri);
        }
    }

    public List<Set<String>> getClauseSets()
    {
        return clauseSets;
    }

    @Override
    public void exitRootClass(@NotNull QueryParser.RootClassContext ctx)
    {
        super.exitRootClass(ctx);
        if (rootOntElement == null)
        {
            rootOntElement = ctx.getText();
            candidateCurrentClasses.clear();
            candidateCurrentClasses.add(rootOntElement);
        }
        else
        {
            if(!rootOntElement.equals(ctx.getText()))
            {
                throw new QueryParserException(String.format("Query can not have different root elements :: %s and %s",
                        lookupClass(rootOntElement), lookupClass(ctx.getText())));
            }
        }

        currentClauses.add(String.format("?%s rdf:type <%s> . ", "root", lookupClass(ctx.getText())));
        nextVariable = "root";
    }

    @Override
    public void exitFollowOP(@NotNull QueryParser.FollowOPContext ctx)
    {
        super.exitFollowOP(ctx);

        String predicate = ctx.ontElem().getText();

        if(pendingPredicate == null)
        {
            pendingPredicate = predicate;
        }
        else
        {
            String resolvedClass = resolveClass(candidateCurrentClasses, pendingPredicate);
            currentClauses.add(String.format("?%s <%s> ?%s . ", nextVariable, lookupPredicate(resolvedClass, pendingPredicate), var[i]));

            candidateCurrentClasses = getCandidateClassSet(resolvedClass, pendingPredicate);

            pendingPredicate = predicate;
            nextVariable = var[i];
            i++;
        }
    }



    @Override
    public void exitReverseOP(@NotNull QueryParser.ReverseOPContext ctx)
    {
        super.exitReverseOP(ctx);

        String predicate = ctx.ontElem().getText();

        if(pendingPredicate == null)
        {
            pendingPredicate = predicate;
        }
        else
        {
            currentClauses.add(String.format("?%s <%s> ?%s . ", nextVariable, lookupReversePredicate(pendingPredicate), var[i]));
            pendingPredicate = predicate;
            nextVariable = var[i];
            i++;
        }
    }

    @Override
    public void exitLiteral(@NotNull com.daedafusion.knowledge.query.dsl.QueryParser.LiteralContext ctx)
    {
        super.exitLiteral(ctx);

        String value = ctx.getText().startsWith("\"") ? ctx.getText() : String.format("\"%s\"", ctx.getText());

        if(value.equals("*") || value.equals("\"*\""))
        {
            value = "?wild";
        }

        if(pendingPredicate == null)
        {
            currentClauses.add(String.format("?%s ?pred %s . ", nextVariable, value));
        }
        else
        {
            String resolvedClass = resolveClass(candidateCurrentClasses, pendingPredicate);
            currentClauses.add(String.format("?%s <%s> %s . ", nextVariable, lookupPredicate(resolvedClass, pendingPredicate), value));
            pendingPredicate = null;
        }

        nextVariable = null;
    }

    @Override
    public void enterOrExp(@NotNull QueryParser.OrExpContext ctx)
    {
        super.enterOrExp(ctx);
        clauseSets.add(currentClauses);
        currentClauses = new LinkedHashSet<>();
    }

    @Override
    public void exitExpQuery(@NotNull QueryParser.ExpQueryContext ctx)
    {
        super.exitExpQuery(ctx);
        clauseSets.add(currentClauses);
        currentClauses = new LinkedHashSet<>();
    }

    private String lookupClass(String text)
    {
        return classUriMap.get(text);
    }

    private String resolveClass(Set<String> candidateCurrentClasses, String pendingPredicate)
    {
        for(String c : candidateCurrentClasses)
        {
            if(classPredicateUriMap.get(c).containsKey(pendingPredicate))
            {
                return c;
            }
        }

        throw new RuntimeException("Could not find predicate");
    }

    private String lookupPredicate(String clazz, String pred)
    {
        return classPredicateUriMap.get(clazz).get(pred);
    }

    private Set<String> getCandidateClassSet(String clazz, String pendingPredicate)
    {
        return classPredicateRangeMap.get(clazz).get(pendingPredicate);
    }

    private String lookupReversePredicate(String predicate)
    {
        // TODO need to test
        return predicateUriMap.get(predicate);
    }

    private void populateOntologyCache(String domain, boolean retry)
    {
        try(OntologyClient client = new OntologyClient())
        {
            client.setAuthToken(ServiceIdentity.getInstance().getToken());

            String ont = client.getDomainOntologyRDF(domain, Collections.EMPTY_LIST);

            Model model = ModelFactory.createDefaultModel();

            model.read(new ByteArrayInputStream(ont.getBytes()), null, "RDF/XML");

            ontologyCache.put(domain, model);
        }
        catch (URISyntaxException | NotFoundException | IOException e)
        {
            log.error("", e);
        }
        catch (UnauthorizedException | ServiceErrorException e)
        {
            log.info("", e);
            if(retry)
            {
                ServiceIdentity.getInstance().reAuthenticate();
                populateOntologyCache(domain, false);
            }
            else
            {
                log.error("Failed to reauthenticate");
            }
        }
    }
}
