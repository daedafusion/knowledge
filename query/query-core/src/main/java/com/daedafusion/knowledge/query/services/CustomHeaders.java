package com.daedafusion.knowledge.query.services;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/18/14.
 */
public class CustomHeaders
{
    private static final Logger log = Logger.getLogger(CustomHeaders.class);

    // Content Types
    public static final String SPARQL_RESULTS = "application/sparql-results+json";
    public static final String SPARQL_QUERY = "application/sparql-query";
    public static final String KNOWLEDGE_QUERY = "application/knowledge-query";
}
