package com.daedafusion.knowledge.query.exceptions;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/24/14.
 */
public class UnauthorizedException extends RuntimeException
{
    private static final Logger log = Logger.getLogger(UnauthorizedException.class);

    public UnauthorizedException(String message)
    {
        super(message);
    }

    public UnauthorizedException(String message, Throwable e)
    {
        super(message, e);
    }
}
