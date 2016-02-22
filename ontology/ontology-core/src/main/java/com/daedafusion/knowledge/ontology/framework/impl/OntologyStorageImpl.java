package com.daedafusion.knowledge.ontology.framework.impl;

import com.daedafusion.sf.AbstractService;
import com.daedafusion.knowledge.ontology.DomainAssignment;
import com.daedafusion.knowledge.ontology.OntologyMeta;
import com.daedafusion.knowledge.ontology.OntologySet;
import com.daedafusion.knowledge.ontology.framework.OntologyStorage;
import com.daedafusion.knowledge.ontology.framework.providers.OntologyStorageProvider;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by mphilpot on 7/14/14.
 */
public class OntologyStorageImpl extends AbstractService<OntologyStorageProvider> implements OntologyStorage
{
    private static final Logger log = Logger.getLogger(OntologyStorageImpl.class);


    @Override
    public Class getProviderInterface()
    {
        return OntologyStorageProvider.class;
    }

    @Override
    public OntologyMeta getOntologyMeta(String uuid)
    {
        return getSingleProvider().getOntologyMeta(uuid);
    }

    @Override
    public String getOntology(String uuid)
    {
        return getSingleProvider().getOntology(uuid);
    }

    @Override
    public DomainAssignment getDomainAssignment(String domain)
    {
        return getSingleProvider().getDomainAssignment(domain);
    }

    @Override
    public List<OntologyMeta> getOntologyMeta()
    {
        return getSingleProvider().getOntologyMeta();
    }

    @Override
    public List<DomainAssignment> getDomainAssignments()
    {
        return getSingleProvider().getDomainAssignments();
    }

    @Override
    public void removeDomainAssignment(String domain)
    {
        getSingleProvider().removeDomainAssignment(domain);
    }

    @Override
    public DomainAssignment saveDomainAssignment(DomainAssignment assignment)
    {
        return getSingleProvider().saveDomainAssignment(assignment);
    }

    @Override
    public List<OntologySet> getOntologySets()
    {
        return getSingleProvider().getOntologySets();
    }

    @Override
    public void removeOntology(String uuid)
    {
        getSingleProvider().removeOntology(uuid);
    }

    @Override
    public void removeOntologySet(String uuid)
    {
        getSingleProvider().removeOntologySet(uuid);
    }

    @Override
    public OntologySet saveOntologySet(OntologySet set)
    {
        return getSingleProvider().saveOntologySet(set);
    }

    @Override
    public OntologyMeta saveOntology(String rdfXml)
    {
        return getSingleProvider().saveOntology(rdfXml);
    }

    @Override
    public OntologySet getOntologySet(String uuid)
    {
        return getSingleProvider().getOntologySet(uuid);
    }

    @Override
    public void updateOntologySet(OntologySet oSet)
    {
        getSingleProvider().updateOntologySet(oSet);
    }

    @Override
    public void updateDomainAssignment(DomainAssignment assignment)
    {
        getSingleProvider().updateDomainAssignment(assignment);
    }
}
