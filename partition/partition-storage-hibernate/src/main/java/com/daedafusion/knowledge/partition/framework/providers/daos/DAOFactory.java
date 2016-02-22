package com.daedafusion.knowledge.partition.framework.providers.daos;

import com.daedafusion.hibernate.dao.AbstractDAO;
import com.daedafusion.knowledge.partition.framework.providers.daos.impl.HibernateDAOFactory;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 10/3/14.
 */
public abstract class DAOFactory
{
    private static final Logger log = Logger.getLogger(DAOFactory.class);

    public static final Class HIBERNATE = HibernateDAOFactory.class;

    public static DAOFactory instance()
    {
        try
        {
            return (DAOFactory) HIBERNATE.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException("Error creating DAOFactory impl", e);
        }
    }

    protected AbstractDAO instantiateDAO(Class daoClass)
    {
        try
        {
            return (AbstractDAO)daoClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException("Can not instantiate DAO", e);
        }
    }

    public abstract PartitionDAO getPartitionDAO();
}
