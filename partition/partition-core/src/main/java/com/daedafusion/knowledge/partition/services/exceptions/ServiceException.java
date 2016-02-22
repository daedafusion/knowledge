package com.daedafusion.knowledge.partition.services.exceptions;

import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;

/**
 * Created by mphilpot on 2/19/16.
 */
public class ServiceException extends WebApplicationException
{
    private static final Logger log = Logger.getLogger(ServiceException.class);

    public ServiceException()
    {
        super(500);
    }

    public ServiceException(String message)
    {
        super(message, 500);
    }

    public ServiceException(Exception cause)
    {
        super(cause, 500);
    }

    public ServiceException(String message, Exception cause)
    {
        super(message, cause, 500);
    }
}
