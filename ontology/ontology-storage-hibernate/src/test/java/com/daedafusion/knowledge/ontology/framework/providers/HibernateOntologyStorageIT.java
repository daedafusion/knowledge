package com.daedafusion.knowledge.ontology.framework.providers;

import com.daedafusion.knowledge.ontology.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.ontology.DomainAssignment;
import com.daedafusion.knowledge.ontology.OntologySet;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Created by mphilpot on 3/30/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HibernateOntologyStorageIT
{
    private static final Logger log = Logger.getLogger(HibernateOntologyStorageIT.class);

    private final HibernateOntologyStorageProvider storage;

    public HibernateOntologyStorageIT()
    {
        this.storage = new HibernateOntologyStorageProvider();
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0ontMetaNotFound()
    {
        storage.getOntologyMeta("abc");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0ontNotFound()
    {
        storage.getOntologyMeta("abc");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0daNotFound()
    {
        storage.getDomainAssignment("abc");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0ontSetNotFound()
    {
        storage.getOntologySet("abc");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0removeOntFound()
    {
        storage.removeOntology("abc");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0removeOntSetNotFound()
    {
        storage.removeOntologySet("abc");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0removeDANotFound()
    {
        storage.removeDomainAssignment("abc");
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0updateOntSetNotFound()
    {
        OntologySet set = new OntologySet();
        storage.updateOntologySet(set);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0updateDaNotFound()
    {
        DomainAssignment da = new DomainAssignment();
        storage.updateDomainAssignment(da);
    }
}
