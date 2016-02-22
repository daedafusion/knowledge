package com.daedafusion.knowledge.ontology.framework.exceptions;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 2/19/16.
 */
public class StorageException extends RuntimeException
{
    private static final Logger log = Logger.getLogger(StorageException.class);

    public StorageException(Exception e)
    {
        super(e);
    }

    public StorageException(String message)
    {
        super(message);
    }

    public StorageException(String message, Exception cause)
    {
        super(message, cause);
    }
}
