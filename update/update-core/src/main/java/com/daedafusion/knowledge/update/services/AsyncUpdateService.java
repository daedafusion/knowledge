package com.daedafusion.knowledge.update.services;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.knowledge.update.services.exceptions.ServiceException;
import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import com.daedafusion.knowledge.update.framework.AsyncUpdate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mphilpot on 8/18/14.
 */
@Path("update/async")
public class AsyncUpdateService
{
    private static final Logger log = Logger.getLogger(AsyncUpdateService.class);

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void ntripleUpdate(InputStream ntriples,
                              @HeaderParam("x-update-epoch") Long epoch,
                              @HeaderParam("x-update-partition") String partition,
                              @HeaderParam("x-update-source") String externalSource,
                              @HeaderParam("x-update-ingestid") String ingestId,
                              @QueryParam("reified") @DefaultValue("false") Boolean isReified)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        AsyncUpdate async = framework.getService(AsyncUpdate.class);

        try
        {
            int bufferSize = Configuration.getInstance().getInteger("updateBufferSize", 10000);
            StringBuffer buffer = new StringBuffer();
            int lines = 0;
            LineIterator iter = IOUtils.lineIterator(ntriples, "UTF-8");

            while(iter.hasNext())
            {
                lines++;
                buffer.append(iter.nextLine());

                if(lines % bufferSize == 0)
                {
                    if (isReified)
                        async.update("reified", buffer.toString(), epoch, partition, externalSource, ingestId);
                    else
                        async.update("default", buffer.toString(), epoch, partition, externalSource, ingestId);

                    buffer = new StringBuffer();
                }
            }

            if(buffer.length() != 0)
            {
                if (isReified)
                    async.update("reified", buffer.toString(), epoch, partition, externalSource, ingestId);
                else
                    async.update("default", buffer.toString(), epoch, partition, externalSource, ingestId);
            }

            log.info(String.format("Wrote %d lines", lines));
        }
        catch(IOException e)
        {
            log.error("", e);
            throw new ServiceException("Error reading update");
        }
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
