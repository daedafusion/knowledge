package com.daedafusion.knowledge.ontology.framework.providers.daos.impl;

import com.daedafusion.hibernate.dao.AbstractDAO;
import com.daedafusion.knowledge.ontology.OntologyFile;
import com.daedafusion.knowledge.ontology.framework.providers.daos.OntologyFileDAO;
import org.apache.log4j.Logger;
import org.hibernate.Query;

/**
 * Created by mphilpot on 7/15/14.
 */
public class OntologyFileDAOHibernate extends AbstractDAO<OntologyFile, Long> implements OntologyFileDAO
{
    private static final Logger log = Logger.getLogger(OntologyFileDAOHibernate.class);

    @Override
    public OntologyFile get(String uuid)
    {
        Query q = getSession().createQuery("from OntologyFile f where f.uuid = :uuid");
        q.setString("uuid", uuid);
        return (OntologyFile) q.uniqueResult();
    }
}
