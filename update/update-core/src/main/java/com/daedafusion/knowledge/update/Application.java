package com.daedafusion.knowledge.update;

import com.daedafusion.knowledge.update.services.AsyncUpdateService;
import com.daedafusion.knowledge.update.services.SyncUpdateService;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by mphilpot on 8/18/14.
 */
public class Application extends ResourceConfig
{
    private static final Logger log = Logger.getLogger(Application.class);

    public Application()
    {
        super(
                AsyncUpdateService.class,
                SyncUpdateService.class,
                JacksonJsonProvider.class
        );
    }
}
