package com.daedafusion.knowledge.partition.framework.providers.daos;

import com.daedafusion.hibernate.dao.GenericDAO;
import com.daedafusion.knowledge.partition.Partition;

import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 10/3/14.
 */
public interface PartitionDAO extends GenericDAO<Partition, Long>
{
    Partition get(String uuid);
    List<Partition> getReadable(Set<String> capabilities);
    List<Partition> getWritable(Set<String> capabilities);
    List<Partition> getAdminable(Set<String> capabilities);

//    List<Partition> getReadable(Set<String> capabilities, Set<String> tags);
//    List<Partition> getWritable(Set<String> capabilities, Set<String> tags);
//    List<Partition> getAdminable(Set<String> capabilities, Set<String> tags);
}
