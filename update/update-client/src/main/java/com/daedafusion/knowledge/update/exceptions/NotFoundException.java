package com.daedafusion.knowledge.update.exceptions;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/24/14.
 */
public class NotFoundException extends RuntimeException
{
    private static final Logger log = Logger.getLogger(NotFoundException.class);

    public NotFoundException(String message)
    {
        super(message);
    }

    public NotFoundException(String message, Throwable e)
    {
        super(message, e);
    }
}
