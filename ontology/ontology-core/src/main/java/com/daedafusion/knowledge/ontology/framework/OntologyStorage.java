package com.daedafusion.knowledge.ontology.framework;

import com.daedafusion.knowledge.ontology.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.ontology.framework.exceptions.StorageException;
import com.daedafusion.knowledge.ontology.DomainAssignment;
import com.daedafusion.knowledge.ontology.OntologyMeta;
import com.daedafusion.knowledge.ontology.OntologySet;

import java.util.List;

/**
 * Created by mphilpot on 7/14/14.
 */
public interface OntologyStorage
{
    OntologyMeta getOntologyMeta(String uuid) throws ObjectNotFoundException, StorageException;

    String getOntology(String uuid) throws ObjectNotFoundException, StorageException;

    DomainAssignment getDomainAssignment(String domain) throws ObjectNotFoundException, StorageException;

    // Admin routines

    List<OntologyMeta> getOntologyMeta() throws StorageException;

    List<DomainAssignment> getDomainAssignments() throws StorageException;

    void removeDomainAssignment(String domain) throws ObjectNotFoundException, StorageException;

    DomainAssignment saveDomainAssignment(DomainAssignment assignment) throws StorageException;

    List<OntologySet> getOntologySets() throws StorageException;

    void removeOntology(String uuid) throws ObjectNotFoundException, StorageException;

    void removeOntologySet(String uuid) throws ObjectNotFoundException, StorageException;

    OntologySet saveOntologySet(OntologySet set) throws StorageException;

    OntologyMeta saveOntology(String rdfXml) throws StorageException;

    OntologySet getOntologySet(String uuid) throws ObjectNotFoundException, StorageException;

    void updateOntologySet(OntologySet oSet) throws ObjectNotFoundException, StorageException;

    void updateDomainAssignment(DomainAssignment assignment) throws ObjectNotFoundException, StorageException;
}
