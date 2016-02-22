package com.daedafusion.knowledge.ontology.framework.providers.daos.impl;

import com.daedafusion.hibernate.dao.AbstractDAO;
import com.daedafusion.knowledge.ontology.OntologyMeta;
import com.daedafusion.knowledge.ontology.framework.providers.daos.OntologyMetaDAO;
import org.apache.log4j.Logger;
import org.hibernate.Query;

import java.util.List;

/**
 * Created by mphilpot on 7/15/14.
 */
public class OntologyMetaDAOHibernate extends AbstractDAO<OntologyMeta, Long> implements OntologyMetaDAO
{
    private static final Logger log = Logger.getLogger(OntologyMetaDAOHibernate.class);

    @Override
    public OntologyMeta get(String uuid)
    {
        Query q = getSession().createQuery("from OntologyMeta m where m.uuid = :uuid");
        q.setString("uuid", uuid);
        return (OntologyMeta) q.uniqueResult();
    }

    @Override
    public List<OntologyMeta> getForDomain(String domain)
    {
        Query q = getSession().createQuery("from OntologyMeta m where m.domain = :domain or m.domain is null");
        q.setString("domain", domain);
        return q.list();
    }
}
