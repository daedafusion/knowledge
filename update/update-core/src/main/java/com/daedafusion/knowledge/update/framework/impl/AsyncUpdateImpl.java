package com.daedafusion.knowledge.update.framework.impl;

import com.daedafusion.sf.AbstractService;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.knowledge.update.framework.AsyncUpdate;
import com.daedafusion.knowledge.update.framework.providers.AsyncUpdateProvider;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mphilpot on 8/18/14.
 */
public class AsyncUpdateImpl extends AbstractService<AsyncUpdateProvider> implements AsyncUpdate
{
    private static final Logger log = Logger.getLogger(AsyncUpdateImpl.class);

    private final Map<String, AsyncUpdateProvider> map;

    public AsyncUpdateImpl()
    {
        map = new HashMap<>();

        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                for(AsyncUpdateProvider aup : getProviders())
                {
                    map.put(aup.getNamedGraph(), aup);
                }
            }

            @Override
            public void start()
            {

            }

            @Override
            public void stop()
            {

            }

            @Override
            public void teardown()
            {

            }
        });
    }

    @Override
    public void update(String namedGraph, Model model, Long epoch, String partition, String externalSource, String ingestId)
    {
        if(!map.containsKey(namedGraph))
            throw new RuntimeException(String.format("Unsupported named graph (%s)", namedGraph));

        map.get(namedGraph).update(model, epoch, partition, externalSource, ingestId);
    }

    @Override
    public void update(String namedGraph, String nTripleLine, Long epoch, String partition, String externalSource, String ingestId)
    {
        if(!map.containsKey(namedGraph))
            throw new RuntimeException(String.format("Unsupported named graph (%s)", namedGraph));

        map.get(namedGraph).update(nTripleLine, epoch, partition, externalSource, ingestId);
    }

    @Override
    public void truncate(String namedGraph, String partition)
    {
        if(!map.containsKey(namedGraph))
            throw new RuntimeException(String.format("Unsupported named graph (%s)", namedGraph));

        map.get(namedGraph).truncate(partition);
    }

    @Override
    public Class getProviderInterface()
    {
        return AsyncUpdateProvider.class;
    }
}
