package com.daedafusion.knowledge.partition.framework.impl;

import com.daedafusion.sf.AbstractService;
import com.daedafusion.knowledge.partition.Partition;
import com.daedafusion.knowledge.partition.framework.PartitionStorage;
import com.daedafusion.knowledge.partition.framework.providers.PartitionStorageProvider;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 10/3/14.
 */
public class PartitionStorageImpl extends AbstractService<PartitionStorageProvider> implements PartitionStorage
{
    private static final Logger log = Logger.getLogger(PartitionStorageImpl.class);

    @Override
    public Partition createPartition(Partition partition)
    {
        return getSingleProvider().createPartition(partition);
    }

    @Override
    public void deletePartition(String uuid)
    {
        getSingleProvider().deletePartition(uuid);
    }

    @Override
    public Partition getPartition(String uuid)
    {
        return getSingleProvider().getPartition(uuid);
    }

    @Override
    public void updatePartition(Partition partition)
    {
        getSingleProvider().updatePartition(partition);
    }

    @Override
    public List<Partition> getAllPartitions()
    {
        return getSingleProvider().getAllPartitions();
    }

    @Override
    public List<Partition> getPartitions(Action action, Set<String> capabilities)
    {
        return getSingleProvider().getPartitions(action, capabilities);
    }

    @Override
    public List<Partition> getPartitions(Action action, Set<String> capabilities, Set<String> tags)
    {
        return getSingleProvider().getPartitions(action, capabilities, tags);
    }

    @Override
    public List<Partition> getPartitions(Action action, Set<String> capabilities, Set<String> tags, Set<String> systemTags)
    {
        return getSingleProvider().getPartitions(action, capabilities, tags, systemTags);
    }

    @Override
    public Class getProviderInterface()
    {
        return PartitionStorageProvider.class;
    }
}
