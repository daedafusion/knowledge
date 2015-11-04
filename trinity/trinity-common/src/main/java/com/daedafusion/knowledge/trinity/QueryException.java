package com.daedafusion.knowledge.trinity;

import org.apache.log4j.Logger;


/**
 * Created by mphilpot on 7/8/14.
 */
public class QueryException extends RuntimeException
{
    private static final Logger log = Logger.getLogger(QueryException.class);

    public QueryException(Throwable cause)
    {
        super(cause);
    }
}
