package com.daedafusion.knowledge.partition.framework.providers.daos.impl;

import com.daedafusion.knowledge.partition.framework.providers.daos.DAOFactory;
import com.daedafusion.knowledge.partition.framework.providers.daos.PartitionDAO;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 10/3/14.
 */
public class HibernateDAOFactory extends DAOFactory
{
    private static final Logger log = Logger.getLogger(HibernateDAOFactory.class);

    @Override
    public PartitionDAO getPartitionDAO()
    {
        return (PartitionDAO) instantiateDAO(PartitionDAOHibernate.class);
    }
}
