package com.daedafusion.knowledge.query.services.exceptions;

import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;

/**
 * Created by mphilpot on 7/14/14.
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
}
