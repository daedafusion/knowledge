package com.daedafusion.knowledge.ontology.framework.providers.daos.impl;

import com.daedafusion.knowledge.ontology.framework.providers.daos.*;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/15/14.
 */
public class HibernateDAOFactory extends DAOFactory
{
    private static final Logger log = Logger.getLogger(HibernateDAOFactory.class);


    @Override
    public OntologyMetaDAO getOntologyMetaDAO()
    {
        return (OntologyMetaDAO) instantiateDAO(OntologyMetaDAOHibernate.class);
    }

    @Override
    public OntologyFileDAO getOntologyFileDAO()
    {
        return (OntologyFileDAO) instantiateDAO(OntologyFileDAOHibernate.class);
    }

    @Override
    public OntologySetDAO getOntologySetDAO()
    {
        return (OntologySetDAO) instantiateDAO(OntologySetDAOHibernate.class);
    }

    @Override
    public DomainAssignmentDAO getDomainAssignmentDAO()
    {
        return (DomainAssignmentDAO) instantiateDAO(DomainAssignmentDAOHibernate.class);
    }
}
