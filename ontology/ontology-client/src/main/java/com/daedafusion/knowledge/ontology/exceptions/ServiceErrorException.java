package com.daedafusion.knowledge.ontology.exceptions;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/24/14.
 */
public class ServiceErrorException extends RuntimeException
{
    private static final Logger log = Logger.getLogger(ServiceErrorException.class);

    public ServiceErrorException(String message)
    {
        super(message);
    }

    public ServiceErrorException(String message, Throwable e)
    {
        super(message, e);
    }
}
