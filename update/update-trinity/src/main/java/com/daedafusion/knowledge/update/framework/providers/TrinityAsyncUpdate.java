package com.daedafusion.knowledge.update.framework.providers;

import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.knowledge.trinity.triples.update.ModelWriter;
import com.daedafusion.knowledge.trinity.triples.update.ModelWriterPool;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by mphilpot on 8/18/14.
 */
public class TrinityAsyncUpdate extends AbstractProvider implements AsyncUpdateProvider
{
    private static final Logger log = Logger.getLogger(TrinityAsyncUpdate.class);

    private final ObjectPool<ModelWriter> pool;

    private final AtomicLong counter;

    public TrinityAsyncUpdate()
    {
        pool = new ModelWriterPool(new ModelWriterPool.ModelWriterObjectFactory(true, false));
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
            log.info(String.format("Writing %d triples async (Total :: %d)", model.size(), counter.get()));

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
    public void update(String nTripleLine, Long epoch, String partition, String externalSource, String ingestId)
    {
        // Use Jena for validation... could do this manually and be more efficient
        Model model = ModelFactory.createDefaultModel();
        ByteArrayInputStream bais = new ByteArrayInputStream(nTripleLine.getBytes());
        model.read(bais, null, "N-TRIPLE");

        update(model, epoch, partition, externalSource, ingestId);
    }

    @Override
    public void truncate(String partition)
    {
        throw new RuntimeException("Not Yet Implemented");
    }

    @Override
    public String getNamedGraph()
    {
        return "default";
    }
}
