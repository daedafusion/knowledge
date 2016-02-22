package com.daedafusion.knowledge.partition.framework.providers;

import com.daedafusion.hibernate.Transaction;
import com.daedafusion.hibernate.TransactionFactory;
import com.daedafusion.hibernate.listener.HibernateSessionFilter;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.knowledge.partition.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.partition.framework.exceptions.StorageException;
import com.daedafusion.knowledge.partition.Partition;
import com.daedafusion.knowledge.partition.framework.providers.daos.DAOFactory;
import com.daedafusion.knowledge.partition.framework.providers.daos.PartitionDAO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import java.util.*;

import static com.daedafusion.knowledge.partition.framework.PartitionStorage.Action.*;

/**
 * Created by mphilpot on 10/3/14.
 */
public class HibernatePartitionStorageProvider extends AbstractProvider implements PartitionStorageProvider
{
    private static final Logger log = Logger.getLogger(HibernatePartitionStorageProvider.class);

    public HibernatePartitionStorageProvider()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                getServiceRegistry().addExternalResource(HibernateSessionFilter.class.getName(), new HibernateSessionFilter());
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
    public Partition createPartition(Partition partition)
    {
        PartitionDAO dao = DAOFactory.instance().getPartitionDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            partition = dao.makePersistent(partition);

            tm.commit();

            return partition;
        }
        catch(HibernateException e)
        {
            throw new StorageException(e);
        }
        finally
        {
            if(tm.isActive())
                tm.rollback();
        }
    }

    @Override
    public void deletePartition(String uuid)
    {
        PartitionDAO dao = DAOFactory.instance().getPartitionDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            Partition p = dao.get(uuid);

            if(p == null)
            {
                throw new ObjectNotFoundException();
            }

            dao.makeTransient(p);

            tm.commit();
        }
        catch(HibernateException e)
        {
            throw new StorageException(e);
        }
        finally
        {
            if(tm.isActive())
                tm.rollback();
        }
    }

    @Override
    public Partition getPartition(String uuid)
    {
        PartitionDAO dao = DAOFactory.instance().getPartitionDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            Partition p = dao.get(uuid);

            if(p == null)
            {
                throw new ObjectNotFoundException();
            }

            p = tm.materialize(p);

            tm.commit();

            return p;
        }
        catch(HibernateException e)
        {
            throw new StorageException(e);
        }
        finally
        {
            if(tm.isActive())
                tm.rollback();
        }
    }

    @Override
    public void updatePartition(Partition partition)
    {
        PartitionDAO dao = DAOFactory.instance().getPartitionDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        tm.begin();

        try
        {
            Partition p = dao.get(partition.getUuid());

            if(p == null)
            {
                throw new ObjectNotFoundException();
            }

            p.setName(partition.getName());
            p.setAdmin(partition.getAdmin());
            p.setHash(partition.getHash());
            p.setRead(partition.getRead());
            p.setWrite(partition.getWrite());

            tm.commit();
        }
        catch(HibernateException e)
        {
            throw new StorageException(e);
        }
        finally
        {
            if(tm.isActive())
                tm.rollback();
        }
    }

    @Override
    public List<Partition> getAllPartitions()
    {
        PartitionDAO dao = DAOFactory.instance().getPartitionDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            Collection<Partition> list = dao.findAll();

            for (Partition p : list)
            {
                tm.materialize(p);
            }

            tm.commit();

            return new ArrayList<Partition>(list);
        }
        catch(HibernateException e)
        {
            throw new StorageException(e);
        }
        finally
        {
            if(tm.isActive())
                tm.rollback();
        }
    }

    @Override
    public List<Partition> getPartitions(Action action, Set<String> capabilities)
    {
        PartitionDAO dao = DAOFactory.instance().getPartitionDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            List<Partition> list;

            switch (action)
            {
                case Admin:
                    list = dao.getAdminable(capabilities);
                    break;
                case Read:
                    list = dao.getReadable(capabilities);
                    break;
                case Write:
                    list = dao.getWritable(capabilities);
                    break;
                default:
                    throw new RuntimeException("Invalid Action enum value");
            }

            for (Partition p : list)
            {
                tm.materialize(p);
            }

            tm.commit();

            return list;
        }
        catch(HibernateException e)
        {
            throw new StorageException(e);
        }
        finally
        {
            if(tm.isActive())
                tm.rollback();
        }
    }

    @Override
    public List<Partition> getPartitions(Action action, Set<String> capabilities, Set<String> tags)
    {
        PartitionDAO dao = DAOFactory.instance().getPartitionDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            List<Partition> list;

            switch (action)
            {
                case Admin:
                    list = dao.getAdminable(capabilities);
                    break;
                case Read:
                    list = dao.getReadable(capabilities);
                    break;
                case Write:
                    list = dao.getWritable(capabilities);
                    break;
                default:
                    throw new RuntimeException("Invalid Action enum value");
            }

            Iterator<Partition> iter = list.iterator();

            while (iter.hasNext())
            {
                Partition p = iter.next();
                tm.materialize(p);

                if (!tags.isEmpty() && !CollectionUtils.containsAny(tags, p.getTags()))
                {
                    iter.remove();
                }
            }

            tm.commit();

            return list;
        }
        catch(HibernateException e)
        {
            throw new StorageException(e);
        }
        finally
        {
            if(tm.isActive())
                tm.rollback();
        }
    }

    @Override
    public List<Partition> getPartitions(Action action, Set<String> capabilities, Set<String> tags, Set<String> systemTags)
    {
        PartitionDAO dao = DAOFactory.instance().getPartitionDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            List<Partition> list;

            switch (action)
            {
                case Admin:
                    list = dao.getAdminable(capabilities);  // todo add tags to sql query
                    break;
                case Read:
                    list = dao.getReadable(capabilities);
                    break;
                case Write:
                    list = dao.getWritable(capabilities);
                    break;
                default:
                    throw new RuntimeException("Invalid Action enum value");
            }

            Iterator<Partition> iter = list.iterator();

            while (iter.hasNext())
            {
                Partition p = iter.next();
                tm.materialize(p);

                if (!tags.isEmpty() && !CollectionUtils.containsAny(tags, p.getTags()))
                {
                    iter.remove();
                } else if (!systemTags.isEmpty() && !CollectionUtils.containsAny(systemTags, p.getSystemTags()))
                {
                    iter.remove();
                }
            }

            tm.commit();

            return list;
        }
        catch(HibernateException e)
        {
            throw new StorageException(e);
        }
        finally
        {
            if(tm.isActive())
                tm.rollback();
        }
    }
}
