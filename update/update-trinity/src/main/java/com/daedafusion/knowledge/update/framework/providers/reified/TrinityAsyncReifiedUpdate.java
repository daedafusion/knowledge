package com.daedafusion.knowledge.update.framework.providers.reified;

import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.knowledge.trinity.triples.update.ModelWriter;
import com.daedafusion.knowledge.trinity.triples.update.ModelWriterPool;
import com.daedafusion.knowledge.update.framework.providers.AsyncUpdateProvider;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.pool2.ObjectPool;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by mphilpot on 8/18/14.
 */
public class TrinityAsyncReifiedUpdate extends AbstractProvider implements AsyncUpdateProvider
{
    private static final Logger log = Logger.getLogger(TrinityAsyncReifiedUpdate.class);

    private final ObjectPool<ModelWriter> pool;

    private final AtomicLong counter;

    public TrinityAsyncReifiedUpdate()
    {
        pool = new ModelWriterPool(new ModelWriterPool.ModelWriterObjectFactory(false, true));
        counter = new AtomicLong(0L);

        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {

            }

            @Override
            public void start()
            {

            }

            @Override
            public void stop()
            {
                pool.close();
            }

            @Override
            public void teardown()
            {

            }
        });
    }

    @Override
    public void update(Model model, Long epoch, String partition, String externalSource, String ingestId)
    {
        ModelWriter writer = null;
        try
        {
            writer = pool.borrowObject();

            counter.getAndAdd(model.size());
            log.info(String.format("Writing %d reified triples async (Total :: %d)", model.size(), counter.get()));

            writer.write(model, partition, epoch, externalSource, ingestId);
        }
        catch (IOException e)
        {
            log.error("Invalidating model writer object", e);
            try
            {
                pool.invalidateObject(writer);
            }
            catch (Exception e1)
            {
                log.error("", e1);
            }
        }
        catch (Exception e)
        {
            log.error("", e);
            // TODO what should we do with the model?
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    pool.returnObject(writer);
                }
                catch (Exception e)
                {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public void truncate(String partition)
    {
        throw new RuntimeException("Not Yet Implemented");
    }

    @Override
    public String getNamedGraph()
    {
        return "reified";
    }
}
