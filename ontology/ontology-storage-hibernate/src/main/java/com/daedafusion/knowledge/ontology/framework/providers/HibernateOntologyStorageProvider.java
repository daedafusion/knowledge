package com.daedafusion.knowledge.ontology.framework.providers;

import com.daedafusion.hibernate.Transaction;
import com.daedafusion.hibernate.TransactionFactory;
import com.daedafusion.hibernate.listener.HibernateSessionFilter;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.knowledge.ontology.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.ontology.framework.exceptions.StorageException;
import com.daedafusion.knowledge.ontology.DomainAssignment;
import com.daedafusion.knowledge.ontology.OntologyFile;
import com.daedafusion.knowledge.ontology.OntologyMeta;
import com.daedafusion.knowledge.ontology.OntologySet;
import com.daedafusion.knowledge.ontology.framework.providers.daos.*;
import com.daedafusion.knowledge.ontology.model.OntologyModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 7/15/14.
 */
public class HibernateOntologyStorageProvider extends AbstractProvider implements OntologyStorageProvider
{
    private static final Logger log = Logger.getLogger(HibernateOntologyStorageProvider.class);

    public HibernateOntologyStorageProvider()
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
    public OntologyMeta getOntologyMeta(String uuid)
    {
        OntologyMetaDAO dao = DAOFactory.instance().getOntologyMetaDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            OntologyMeta om = dao.get(uuid);

            if(om == null)
            {
                throw new ObjectNotFoundException();
            }

            om = tm.materialize(om);

            tm.commit();

            return om;
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
    public String getOntology(String uuid)
    {
        OntologyFileDAO dao = DAOFactory.instance().getOntologyFileDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            OntologyFile of = dao.get(uuid);

            if(of == null)
            {
                throw new ObjectNotFoundException();
            }

            of = tm.materialize(of);

            tm.commit();

            return of.getFile();
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
    public DomainAssignment getDomainAssignment(String domain)
    {
        DomainAssignmentDAO dao = DAOFactory.instance().getDomainAssignmentDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            DomainAssignment assignment = dao.get(domain);

            if(assignment == null)
            {
                throw new ObjectNotFoundException();
            }

            assignment = tm.materialize(assignment);

            tm.commit();

            return assignment;
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
    public List<OntologyMeta> getOntologyMeta()
    {
        OntologyMetaDAO dao = DAOFactory.instance().getOntologyMetaDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            Collection<OntologyMeta> list = dao.findAll();

            for (OntologyMeta om : list)
            {
                om = tm.materialize(om);
            }

            tm.commit();

            return new ArrayList<OntologyMeta>(list);
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
    public List<DomainAssignment> getDomainAssignments()
    {
        DomainAssignmentDAO dao = DAOFactory.instance().getDomainAssignmentDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            Collection<DomainAssignment> list = dao.findAll();

            for (DomainAssignment da : list)
            {
                da = tm.materialize(da);
            }

            tm.commit();

            return new ArrayList<DomainAssignment>(list);
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
    public void removeDomainAssignment(String domain)
    {
        DomainAssignmentDAO dao = DAOFactory.instance().getDomainAssignmentDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            DomainAssignment da = dao.get(domain);

            if(da == null)
            {
                throw new ObjectNotFoundException();
            }

            dao.makeTransient(da);

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
    public DomainAssignment saveDomainAssignment(DomainAssignment assignment)
    {
        DomainAssignmentDAO dao = DAOFactory.instance().getDomainAssignmentDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            assignment = dao.makePersistent(assignment);

            tm.commit();

            return assignment;
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
    public List<OntologySet> getOntologySets()
    {
        OntologySetDAO dao = DAOFactory.instance().getOntologySetDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            Collection<OntologySet> list = dao.findAll();

            for (OntologySet os : list)
            {
                os = tm.materialize(os);
            }

            tm.commit();

            return new ArrayList<OntologySet>(list);
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
    public OntologyMeta saveOntology(String rdfXml)
    {
        OntologyFileDAO fdao = DAOFactory.instance().getOntologyFileDAO();
        OntologyMetaDAO mdao = DAOFactory.instance().getOntologyMetaDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            OntologyModel ontModel = new OntologyModel();
            ontModel.load(new ByteArrayInputStream(rdfXml.getBytes()), "RDF/XML");

            OntologyMeta om = new OntologyMeta();
            Set<String> set = ontModel.getObjectsOfType("http://www.w3.org/2002/07/owl#Ontology");
            String uri = set.isEmpty() ? null : set.iterator().next();
            om.setUrl(uri);
            om.setLabel(uri == null ? null : StringUtils.join(ontModel.getLabels(uri), "\n"));
            om.setComments(uri == null ? null : StringUtils.join(ontModel.getComments(uri), "\n"));

            om = mdao.makePersistent(om);

            OntologyFile of = new OntologyFile();
            of.setUuid(om.getUuid());
            of.setFile(rdfXml);

            fdao.makePersistent(of);

            tm.commit();

            return om;
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
    public OntologySet getOntologySet(String uuid)
    {
        OntologySetDAO dao = DAOFactory.instance().getOntologySetDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            OntologySet os = dao.get(uuid);

            if (os == null)
            {
                throw new ObjectNotFoundException();
            }

            os = tm.materialize(os);

            tm.commit();

            return os;
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
    public void updateOntologySet(OntologySet oSet)
    {
        OntologySetDAO dao = DAOFactory.instance().getOntologySetDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            OntologySet os = dao.get(oSet.getUuid());

            if (os == null)
            {
                throw new ObjectNotFoundException();
            }

            os.setName(oSet.getName());
            os.setOntologies(oSet.getOntologies());

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
    public void updateDomainAssignment(DomainAssignment assignment)
    {
        DomainAssignmentDAO dao = DAOFactory.instance().getDomainAssignmentDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            DomainAssignment da = dao.get(assignment.getUuid());

            if(da == null)
            {
                throw new ObjectNotFoundException();
            }

            da.setOntologySets(assignment.getOntologySets());

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
    public void removeOntology(String uuid)
    {
        OntologyFileDAO fdao = DAOFactory.instance().getOntologyFileDAO();
        OntologyMetaDAO mdao = DAOFactory.instance().getOntologyMetaDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            OntologyFile of = fdao.get(uuid);
            OntologyMeta om = mdao.get(uuid);

            if(of == null || om == null)
            {
                throw new ObjectNotFoundException();
            }

            fdao.makeTransient(of);
            mdao.makeTransient(om);

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
    public void removeOntologySet(String uuid)
    {
        OntologySetDAO dao = DAOFactory.instance().getOntologySetDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            OntologySet os = dao.get(uuid);

            if (os == null)
            {
                throw new ObjectNotFoundException();
            }

            dao.makeTransient(os);

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
    public OntologySet saveOntologySet(OntologySet set)
    {
        OntologySetDAO dao = DAOFactory.instance().getOntologySetDAO();

        Transaction tm = TransactionFactory.getInstance().get();

        try
        {
            tm.begin();

            set = dao.makePersistent(set);

            tm.commit();

            return set;
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
