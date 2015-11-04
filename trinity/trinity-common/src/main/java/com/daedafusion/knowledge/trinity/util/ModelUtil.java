package com.daedafusion.knowledge.trinity.util;

import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 8/26/14.
 */
public class ModelUtil
{
    private static final Logger log = Logger.getLogger(ModelUtil.class);

    public static Statement getDataStatement(String subject, String predicate, String object)
    {
        return ResourceFactory.createStatement(
            ResourceFactory.createResource(subject),
                ResourceFactory.createProperty(predicate),
                ResourceFactory.createPlainLiteral(object)
        );
    }

    public static Statement getObjectStatement(String subject, String predicate, String object)
    {
        return ResourceFactory.createStatement(
                ResourceFactory.createResource(subject),
                ResourceFactory.createProperty(predicate),
                ResourceFactory.createResource(object)
        );
    }

    public static Statement getTypeStatement(String subject, String object)
    {
        return ResourceFactory.createStatement(
                ResourceFactory.createResource(subject),
                ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                ResourceFactory.createResource(object)
        );
    }
}
