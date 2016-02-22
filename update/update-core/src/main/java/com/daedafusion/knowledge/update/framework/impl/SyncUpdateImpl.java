package com.daedafusion.knowledge.update.framework.impl;

import com.daedafusion.sf.AbstractService;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.knowledge.update.framework.SyncUpdate;
import com.daedafusion.knowledge.update.framework.providers.SyncUpdateProvider;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mphilpot on 8/18/14.
 */
public class SyncUpdateImpl extends AbstractService<SyncUpdateProvider> implements SyncUpdate
{
    private static final Logger log = Logger.getLogger(SyncUpdateImpl.class);

    private final Map<String, SyncUpdateProvider> map;

    public SyncUpdateImpl()
    {
        map = new HashMap<>();

        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                for(SyncUpdateProvider sup : getProviders())
                {
                    map.put(sup.getNamedGraph(), sup);
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
    public void delete(String namedGraph, Model model, String partition)
    {
        if(!map.containsKey(namedGraph))
            throw new RuntimeException(String.format("Unsupported named graph (%s)", namedGraph));

        map.get(namedGraph).delete(model, partition);
    }

    @Override
    public void delete(String namedGraph, String subject, String partition)
    {
        if(!map.containsKey(namedGraph))
            throw new RuntimeException(String.format("Unsupported named graph (%s)", namedGraph));

        map.get(namedGraph).delete(subject, partition);
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
        return SyncUpdateProvider.class;
    }
}
