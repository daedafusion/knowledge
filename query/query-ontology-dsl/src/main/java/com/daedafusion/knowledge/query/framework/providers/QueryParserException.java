package com.daedafusion.knowledge.query.framework.providers;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/18/14.
 */
public class QueryParserException extends RuntimeException
{
    private static final Logger log = Logger.getLogger(QueryParserException.class);

    public QueryParserException(String message)
    {
        super(message);
    }

    public QueryParserException(Exception e)
    {
        super(e);
    }
}
