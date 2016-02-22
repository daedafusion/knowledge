package com.daedafusion.knowledge.ontology.framework.providers.daos.impl;

import com.daedafusion.hibernate.dao.AbstractDAO;
import com.daedafusion.knowledge.ontology.OntologySet;
import com.daedafusion.knowledge.ontology.framework.providers.daos.OntologySetDAO;
import org.apache.log4j.Logger;
import org.hibernate.Query;

/**
 * Created by mphilpot on 7/15/14.
 */
public class OntologySetDAOHibernate extends AbstractDAO<OntologySet, Long> implements OntologySetDAO
{
    private static final Logger log = Logger.getLogger(OntologySetDAOHibernate.class);

    @Override
    public OntologySet get(String uuid)
    {
        Query q = getSession().createQuery("from OntologySet s where s.uuid = :uuid");
        q.setString("uuid", uuid);
        return (OntologySet) q.uniqueResult();
    }
}
