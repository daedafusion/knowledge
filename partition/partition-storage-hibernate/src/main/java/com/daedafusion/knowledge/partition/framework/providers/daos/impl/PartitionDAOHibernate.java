package com.daedafusion.knowledge.partition.framework.providers.daos.impl;

import com.daedafusion.hibernate.dao.AbstractDAO;
import com.daedafusion.knowledge.partition.Partition;
import com.daedafusion.knowledge.partition.framework.providers.daos.PartitionDAO;
import org.apache.log4j.Logger;
import org.hibernate.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 10/3/14.
 */
public class PartitionDAOHibernate extends AbstractDAO<Partition, Long> implements PartitionDAO
{
    private static final Logger log = Logger.getLogger(PartitionDAOHibernate.class);

    @Override
    public Partition get(String uuid)
    {
        Query q = getSession().createQuery("from Partition p where p.uuid = :uuid");
        q.setString("uuid", uuid);
        return (Partition) q.uniqueResult();
    }

    @Override
    public List<Partition> getReadable(Set<String> capabilities)
    {
//        List<Partition> result = new ArrayList<>();
//
//        for(String cap : capabilities)
//        {
//            Query q = getSession().createQuery("from Partition p where :cap member of p.read").setString("cap", cap);
//            result.addAll(q.list());
//        }
//
//        return result;
        Query q = getSession().createQuery("select p from Partition p left join p.read c where c in ( :caps )");
        q.setParameterList("caps", capabilities);

        List<Partition> query = q.list();

        // Need to dedup
        Set<String> uuids = new HashSet<>();
        List<Partition> result = new ArrayList<>();

        for(Partition p : query)
        {
            if(!uuids.contains(p.getUuid()))
            {
                result.add(p);
                uuids.add(p.getUuid());
            }
        }

        return result;
    }

    @Override
    public List<Partition> getWritable(Set<String> capabilities)
    {
        Query q = getSession().createQuery("select p from Partition p left join p.write c where c in ( :caps )");
        q.setParameterList("caps", capabilities);

        List<Partition> query = q.list();

        // Need to dedup
        Set<String> uuids = new HashSet<>();
        List<Partition> result = new ArrayList<>();

        for(Partition p : query)
        {
            if(!uuids.contains(p.getUuid()))
            {
                result.add(p);
                uuids.add(p.getUuid());
            }
        }

        return result;
    }

    @Override
    public List<Partition> getAdminable(Set<String> capabilities)
    {
        Query q = getSession().createQuery("select p from Partition p left join p.admin c where c in ( :caps )");
        q.setParameterList("caps", capabilities);

        List<Partition> query = q.list();

        // Need to dedup
        Set<String> uuids = new HashSet<>();
        List<Partition> result = new ArrayList<>();

        for(Partition p : query)
        {
            if(!uuids.contains(p.getUuid()))
            {
                result.add(p);
                uuids.add(p.getUuid());
            }
        }

        return result;
    }

//    @Override
//    public List<Partition> getReadable(Set<String> capabilities, Set<String> tags)
//    {
//        List<Partition> result = new ArrayList<>();
//
//        for(String cap : capabilities)
//        {
//            Query q = getSession().createQuery("from Partition p where :cap member of p.read").setString("cap", cap);
//            result.addAll(q.list());
//        }
//
//        return result;
//    }
//
//    @Override
//    public List<Partition> getWritable(Set<String> capabilities, Set<String> tags)
//    {
//        return null;
//    }
//
//    @Override
//    public List<Partition> getAdminable(Set<String> capabilities, Set<String> tags)
//    {
//        return null;
//    }
}
