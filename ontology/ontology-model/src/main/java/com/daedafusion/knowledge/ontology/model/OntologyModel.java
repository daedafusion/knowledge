package com.daedafusion.knowledge.ontology.model;

import com.daedafusion.sparql.Literal;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mphilpot on 7/14/14.
 */
public class OntologyModel
{
    private static final Logger log = Logger.getLogger(OntologyModel.class);

    private Model               model;
    private Map<String, String> prefixes;

    public OntologyModel()
    {
        model = ModelFactory.createDefaultModel();
//        model = ModelFactory.createRDFSModel(ModelFactory.createDefaultModel());

        prefixes = new HashMap<>();

        prefixes.put("rdf", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        prefixes.put("rdfs", "<http://www.w3.org/2000/01/rdf-schema#>");
        prefixes.put("xsd", "<http://www.w3.org/2001/XMLSchema#>");
        prefixes.put("owl", "<http://www.w3.org/2002/07/owl#>");
    }

    public OntologyModel(Model model)
    {
        this();
        this.model = model;
    }

    public Model getModel()
    {
        return model;
    }

    public void load(InputStream in, String type)
    {
        model.read(in, null, type);
    }

    public Set<String> getClasses()
    {
        Set<String> classes = new HashSet<>();
        String sparql = String.format("%s construct where { ?s rdf:type owl:Class }", buildPrefixes());

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            for (Statement s : results.listStatements().toSet())
            {
                if(!s.getSubject().isAnon())
                {
                    classes.add(s.getSubject().toString());
                }
            }
        }

        return classes;
    }

    public Set<String> getParentClasses(String uri)
    {
        Set<String> parents = new HashSet<>();

        String sparql = String.format("%s construct where { <%s> rdfs:subClassOf ?o }", buildPrefixes(), uri);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            parents.addAll(results.listStatements().toSet()
                    .stream()
                    .filter(s -> !s.getObject().isAnon())
                    .map(s -> s.getObject().toString())
                    .collect(Collectors.toList()));

            Set<String> parentClosure = new HashSet<>();

            parents.stream()
                    .filter(parent -> !parent.equals("http://www.w3.org/2000/01/rdf-schema#Resource") && !parent.equals(uri))
                    .forEach(parent -> parentClosure.addAll(getParentClasses(parent)));

            parents.addAll(parentClosure);
        }

        return parents;

    }

    public Set<String> getDataProperties(String uri)
    {
        Set<String> dps = new HashSet<>();

        String sparql = String.format("%s select ?s where { " +
                "{ ?s rdf:type owl:DatatypeProperty . ?s rdfs:domain <%s> } UNION { ?s rdf:type rdf:Property . ?s rdfs:domain <%s> } }",
                buildPrefixes(),
                uri, uri);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qe.execSelect();

            for(;results.hasNext();)
            {
                QuerySolution sol = results.nextSolution();

                Resource resource = sol.getResource("s");

                dps.add(resource.toString());
            }

            Set<String> parents = getParentClasses(uri);

            parents.stream()
                    .filter(parent -> !parent.equals("http://www.w3.org/2000/01/rdf-schema#Resource") && !parent.equals(uri))
                    .forEach(parent -> dps.addAll(getDataProperties(parent)));
        }

        return dps;
    }

    public Set<String> getDataProperties()
    {
        Set<String> dps = new HashSet<>();

        String sparql = String.format("%s select ?s where { " +
                        "{ ?s rdf:type owl:DatatypeProperty } UNION { ?s rdf:type rdf:Property } }",
                buildPrefixes());

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qe.execSelect();

            for(;results.hasNext();)
            {
                QuerySolution sol = results.nextSolution();

                Resource resource = sol.getResource("s");

                dps.add(resource.toString());
            }
        }

        return dps;
    }

    public Set<String> getObjectProperties(String uri)
    {
        Set<String> ops = new HashSet<>();

        String sparql = String.format("%s construct where { ?s rdf:type owl:ObjectProperty . ?s rdfs:domain <%s> }",
                buildPrefixes(), uri);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            ops.addAll(results.listStatements().toSet()
                    .stream()
                    .map(s -> s.getSubject().toString())
                    .collect(Collectors.toList()));

            Set<String> parentClosure = new HashSet<>();

            for(String parent : getParentClasses(uri))
            {
                if(!parent.equals("http://www.w3.org/2000/01/rdf-schema#Resource") && !parent.equals(uri))
                    parentClosure.addAll(getObjectProperties(parent));
            }

            ops.addAll(parentClosure);
        }

        return ops;
    }

    public Set<String> getObjectProperties()
    {
        Set<String> ops = new HashSet<>();

        String sparql = String.format("%s construct where { ?s rdf:type owl:ObjectProperty }",
                buildPrefixes());

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            ops.addAll(results.listStatements().toSet()
                    .stream()
                    .map(s -> s.getSubject().toString())
                    .collect(Collectors.toList()));
        }

        return ops;
    }

    public Set<String> getObjectsOfType(String rdfType)
    {
        Set<String> objects = new HashSet<>();
        String sparql = String.format("%s construct where { ?s rdf:type <%s> }", buildPrefixes(), rdfType);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            objects.addAll(results.listStatements().toSet()
                    .stream()
                    .map(s -> s.getSubject().toString())
                    .collect(Collectors.toList()));
        }

        return objects;
    }

    public String getTypeOfObject(String uri)
    {
        Set<String> rdfTypes = new HashSet<>();
        String sparql = String.format("%s construct where { <%s> rdf:type ?o }", buildPrefixes(), uri);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            rdfTypes.addAll(results.listStatements().toSet()
                    .stream()
                    .map(s -> s.getObject().toString())
                    .collect(Collectors.toList()));
        }

        if(rdfTypes.isEmpty())
        {
            return null;
        }

        if(rdfTypes.size() > 1)
        {
            log.warn(String.format("Found more than one rdfType for %s :: %s", uri, rdfTypes));
        }

        return rdfTypes.iterator().next();
    }

    public Set<String> getObjectsWithAnnotationProperty(String prop)
    {
        Set<String> objects = new HashSet<>();
        String sparql = String.format("%s construct where { ?s <%s> ?o }", buildPrefixes(), prop);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            objects.addAll(results.listStatements().toSet()
                    .stream()
                    .map(s -> s.getSubject().toString())
                    .collect(Collectors.toList()));
        }

        return objects;
    }

    public Set<String> getObjectsWithAnnotationPropertyValue(String prop, String value)
    {
        //validateLiteral(value);

        Set<String> objects = new HashSet<>();
        String sparql = String.format("%s construct where { ?s <%s> \"%s\" }", buildPrefixes(), prop, value);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            objects.addAll(results.listStatements().toSet()
                    .stream()
                    .map(s -> s.getSubject().toString())
                    .collect(Collectors.toList()));
        }

        return objects;
    }

    public Set<Literal> getLabels(String uri)
    {
        Set<Literal> labels = new HashSet<>();
        String sparql = String.format("%s construct where { <%s> rdfs:label ?o }", buildPrefixes(), uri);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            labels.addAll(results.listStatements().toSet()
                    .stream()
                    .map(s -> getLiteral(s.getObject()))
                    .collect(Collectors.toList()));
        }

        return labels;
    }

    public Set<Literal> getComments(String uri)
    {
        Set<Literal> comments = new HashSet<>();
        String sparql = String.format("%s construct where { <%s> rdfs:comment ?o }", buildPrefixes(), uri);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            comments.addAll(results.listStatements().toSet()
                    .stream()
                    .map(s -> getLiteral(s.getObject()))
                    .collect(Collectors.toList()));
        }

        return comments;
    }

    public Set<String> getDomainOfProperty(String propUri)
    {
        Set<String> domains = new HashSet<>();
        String sparql = String.format("%s construct where { <%s> rdfs:domain ?o }", buildPrefixes(), propUri);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            for (Statement s : results.listStatements().toSet())
            {
                if(s.getObject().isAnon())
                {
                    String select = String.format("%s select ?member where { <%s> rdfs:domain ?o . ?o owl:unionOf ?list . ?list rdf:rest*/rdf:first ?member }",
                                                buildPrefixes(), propUri);

                    Query query2 = QueryFactory.create(select);
                    try(QueryExecution qe2 = QueryExecutionFactory.create(query2, model))
                    {
                        ResultSet results2 = qe2.execSelect();

                        for(;results2.hasNext();)
                        {
                            QuerySolution sol = results2.nextSolution();

                            Resource resource = sol.getResource("s");

                            domains.add(resource.toString());
                        }
                    }
                }
                else
                {
                    domains.add(s.getObject().toString());
                }
            }
        }

        return domains;
    }

    public Set<String> getRangeOfProperty(String classUri, String propUri)
    {
        Set<String> range = new HashSet<>();

        String rdfType = getTypeOfObject(classUri);

        boolean isObjectProperty = rdfType.equals("http://www.w3.org/2002/07/owl#ObjectProperty");

        String select = String.format("%s select ?orsc ?orp ?orv where { <%s> rdfs:subClassOf ?orsc . ?orsc rdf:type owl:Restriction . ?orsc ?orp ?orv }",
                buildPrefixes(), classUri);

        //  orsc        orp     orv
        Map<String, Map<String, RDFNode>> tmp = new HashMap<>();

        Query query = QueryFactory.create(select);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qe.execSelect();

            for(;results.hasNext();)
            {
                QuerySolution sol = results.nextSolution();

                String orsc = sol.getResource("orsc").toString();
                String orp = sol.getResource("orp").toString();
                RDFNode orv = sol.get("orv");

                if(!tmp.containsKey(orsc))
                {
                    tmp.put(orsc, new HashMap<>());
                }

                tmp.get(orsc).put(orp, orv);
            }
        }

        // Contains overrides
        for(Map.Entry<String, Map<String, RDFNode>> entry : tmp.entrySet())
        {
            String orsc = entry.getKey();
            Map<String, RDFNode> t = entry.getValue();

            if(t.containsKey("http://www.w3.org/2002/07/owl#onProperty") &&
                    t.get("http://www.w3.org/2002/07/owl#onProperty").toString().equals(propUri) &&
                    (t.containsKey("http://www.w3.org/2002/07/owl#someValuesFrom") ||
                     t.containsKey("http://www.w3.org/2002/07/owl#allValuesFrom") ||
                     t.containsKey("http://www.w3.org/2002/07/owl#hasValue")) )
            {
                String[] restrictionTypes = {
                        "http://www.w3.org/2002/07/owl#someValuesFrom",
                        "http://www.w3.org/2002/07/owl#allValuesFrom",
                        "http://www.w3.org/2002/07/owl#hasValue"
                };

                for(String rt : restrictionTypes)
                {
                    select = String.format("%s select ?member where { <%s> rdfs:subClassOf ?o . ?o <%s> ?o2 . ?o2 owl:oneOf ?list . ?list rdf:rest*/rdf:first ?member }",
                            buildPrefixes(), propUri, rt);

                    query = QueryFactory.create(select);
                    try(QueryExecution qe = QueryExecutionFactory.create(query, model))
                    {
                        ResultSet results = qe.execSelect();

                        for(;results.hasNext();)
                        {
                            QuerySolution sol = results.nextSolution();

                            String member = sol.getResource("member").toString();

                            range.add(member);
                        }
                    }
                }
            }
        }

        // TODO This is wrong but I need the uber owl file to debug
        String construct = String.format("%s construct where { <%s> rdfs:range ?o }", buildPrefixes(), propUri);

        query = QueryFactory.create(construct);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            for (Statement s : results.listStatements().toSet())
            {
                if(s.getObject().isAnon())
                {
                    if (isObjectProperty)
                    {
                        select = String.format("%s select ?member where { <%s> rdfs:range ?o . ?o owl:unionOf ?list . ?list rdf:rest*/rdf:first ?member }",
                                buildPrefixes(), propUri);

                        query = QueryFactory.create(select);
                        try (QueryExecution qe2 = QueryExecutionFactory.create(query, model))
                        {
                            ResultSet results2 = qe2.execSelect();

                            for (; results2.hasNext(); )
                            {
                                QuerySolution sol = results2.nextSolution();

                                String member = getResource(sol.get("member"));

                                range.add(member);
                            }
                        }
                    }
                    else
                    {
                        select = String.format("%s select ?member where { <%s> rdfs:range ?o . ?o owl:oneOf ?list . ?list rdf:rest*/rdf:first ?member }",
                                buildPrefixes(), propUri);

                        query = QueryFactory.create(select);
                        try(QueryExecution qe2 = QueryExecutionFactory.create(query, model))
                        {
                            ResultSet results2 = qe2.execSelect();

                            for(;results2.hasNext();)
                            {
                                QuerySolution sol = results2.nextSolution();

                                String member = getResource(sol.get("member"));

                                range.add(member);
                            }
                        }
                    }
                }
                else
                {
                    range.add(s.getObject().asResource().toString());
                }
            }
        }

        return range;
    }

    /**
     *
     * @param classUri
     * @param propUri
     * @return [0] = min, [1] = max, [2] = exact, null if not specified
     */
    public Integer[] getCardinality(String classUri, String propUri)
    {
        Integer[] cardinality = new Integer[3];

        String select = String.format("%s select ?orsc ?orp ?orv where { <%s> rdfs:subClassOf ?orsc . ?orsc rdf:type owl:Restriction . ?orsc ?orp ?orv }",
                buildPrefixes(), classUri);

        //  orsc        orp     orv
        Map<String, Map<String, RDFNode>> tmp = new HashMap<>();

        Query query = QueryFactory.create(select);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qe.execSelect();

            for(;results.hasNext();)
            {
                QuerySolution sol = results.nextSolution();

                String orsc = sol.getResource("orsc").toString();
                String orp = sol.getResource("orp").toString();
                RDFNode orv = sol.get("orv");

                if(!tmp.containsKey(orsc))
                {
                    tmp.put(orsc, new HashMap<>());
                }

                tmp.get(orsc).put(orp, orv);
            }
        }

        for(Map.Entry<String, Map<String, RDFNode>> entry : tmp.entrySet())
        {
            String orsc = entry.getKey();
            Map<String, RDFNode> t = entry.getValue();

            if(t.containsKey("http://www.w3.org/2002/07/owl#onProperty") &&
                    t.get("http://www.w3.org/2002/07/owl#onProperty").toString().equals(propUri))
            {
                if(t.containsKey("http://www.w3.org/2002/07/owl#minCardinality"))
                {
                    cardinality[0] = t.get("http://www.w3.org/2002/07/owl#minCardinality").asLiteral().getInt();
                }

                if(t.containsKey("http://www.w3.org/2002/07/owl#maxCardinality"))
                {
                    cardinality[1] = t.get("http://www.w3.org/2002/07/owl#maxCardinality").asLiteral().getInt();
                }

                if(t.containsKey("http://www.w3.org/2002/07/owl#cardinality"))
                {
                    cardinality[2] = t.get("http://www.w3.org/2002/07/owl#cardinality").asLiteral().getInt();
                }
            }
        }

        return cardinality;
    }

    public Map<String, Set<Literal>> getAnnotationProperties(String uri)
    {
        Map<String, Set<Literal>> properties = new HashMap<>();

        String sparql = String.format("%s construct where { <%s> ?p ?o }", buildPrefixes(), uri);

        Query query = QueryFactory.create(sparql);
        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            Model results = qe.execConstruct();

            for (Statement s : results.listStatements().toSet())
            {
                String predicate = s.getPredicate().toString();

                if(!properties.containsKey(predicate))
                {
                    properties.put(predicate, new HashSet<Literal>());
                }

                properties.get(predicate).add(getLiteral(s.getObject()));
            }
        }

        return properties;
    }

//    private void validateLiteral(String value)
//    {
//        String regex = "\\\".+?\\\"(@[a-z\\-]+$|\\^\\^.+)?";
//
//        if(!value.matches(regex))
//        {
//            throw new IllegalArgumentException("Literal does not conform to spec");
//        }
//    }

    private String buildPrefixes()
    {
        StringBuilder builder = new StringBuilder();
        for(String key : prefixes.keySet())
        {
            builder.append(String.format("prefix %s: %s \n", key, prefixes.get(key)));
        }

        return builder.toString();
    }

    public String getResource(RDFNode node)
    {
        String object = "";

        if (node.isResource())
        {
            object = node.asResource().getURI();
        } else
        {
            log.warn("Literal node processed as resource");
        }

        return object;
    }

    public Literal getLiteral(RDFNode node)
    {
        Literal lit = new Literal();

        if(node.isLiteral())
        {
            lit.value = node.asLiteral().getString();

            if(node.asLiteral().getDatatypeURI() != null && !node.asLiteral().getDatatypeURI().equals(""))
            {
                lit.type = node.asLiteral().getDatatypeURI();
            }
            else if(node.asLiteral().getLanguage() != null && !node.asLiteral().getLanguage().equals(""))
            {
                lit.lang = node.asLiteral().getLanguage();
            }
        }
        else if(node.isResource())
        {
            lit.value = node.asResource().getURI();
        }
        else
        {
            // Anonymous nodes
            lit.value = node.toString();
        }

        return lit;
    }

    public Map<String, String> getPrefixes()
    {
        return prefixes;
    }
}
