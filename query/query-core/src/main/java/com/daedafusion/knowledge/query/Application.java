package com.daedafusion.knowledge.query;

//import com.daedafusion.knowledge.query.services.DescribeService;
//import com.daedafusion.knowledge.query.services.EditorService;
import com.daedafusion.knowledge.query.services.QueryService;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by mphilpot on 7/17/14.
 */
public class Application extends ResourceConfig
{
    private static final Logger log = Logger.getLogger(Application.class);

    public Application()
    {
        super(
                QueryService.class,
//                DescribeService.class,
//                EditorService.class,
                JacksonJsonProvider.class
        );
    }

}
