package com.daedafusion.knowledge.update.services;

import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import com.daedafusion.knowledge.update.framework.AsyncUpdate;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;

/**
 * Created by mphilpot on 8/18/14.
 */
@Path("update/async")
public class AsyncUpdateService
{
    private static final Logger log = Logger.getLogger(AsyncUpdateService.class);

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void ntripleUpdate(String ntriples,
                              @HeaderParam("x-update-epoch") Long epoch,
                              @HeaderParam("x-update-partition") String partition,
                              @HeaderParam("x-update-source") String externalSource,
                              @HeaderParam("x-update-ingestid") String ingestId,
                              @QueryParam("reified") @DefaultValue("false") Boolean isReified)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        AsyncUpdate async = framework.getService(AsyncUpdate.class);

        Model model = ModelFactory.createDefaultModel();

        ByteArrayInputStream bais = new ByteArrayInputStream(ntriples.getBytes());

        model.read(bais, null, "N-TRIPLE");

        if(isReified)
            async.update("reified", model, epoch, partition, externalSource, ingestId);
        else
            async.update("default", model, epoch, partition, externalSource, ingestId);
    }

    @POST
    @Path("truncate/{uuid}")
    public void truncate(@PathParam("uuid") String partition,
                         @QueryParam("reified") @DefaultValue("false") Boolean isReified)
    {
        // TODO Add Auth check
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        AsyncUpdate async = framework.getService(AsyncUpdate.class);

        if(isReified)
            async.truncate("reified", partition);
        else
            async.truncate("default", partition);
    }
}
