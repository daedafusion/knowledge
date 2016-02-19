package com.daedafusion.knowledge.partition;

import com.daedafusion.knowledge.partition.services.PartitionService;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by mphilpot on 7/10/14.
 */
public class Application extends ResourceConfig
{
    private static final Logger log = Logger.getLogger(Application.class);

    public Application()
    {
        super(
                PartitionService.class,
                JacksonJsonProvider.class
        );
    }
}
