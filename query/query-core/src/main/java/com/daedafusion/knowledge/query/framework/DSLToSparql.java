package com.daedafusion.knowledge.query.framework;

/**
 * Created by mphilpot on 9/5/14.
 */
public interface DSLToSparql
{
    /**
     *
     * @param domain
     * @param dslQuery
     * @return Sparql Query String
     */
    String convert(String domain, String dslQuery);
}
