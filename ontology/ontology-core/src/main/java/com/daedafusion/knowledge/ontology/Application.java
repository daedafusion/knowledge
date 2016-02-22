package com.daedafusion.knowledge.ontology;

import com.daedafusion.knowledge.ontology.services.OntologyAdminService;
import com.daedafusion.knowledge.ontology.services.OntologyService;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by mphilpot on 7/14/14.
 */
public class Application extends ResourceConfig
{
    private static final Logger log = Logger.getLogger(Application.class);

    public Application()
    {
        super(
                OntologyService.class,
                OntologyAdminService.class,
                JacksonJsonProvider.class
        );
    }
}
