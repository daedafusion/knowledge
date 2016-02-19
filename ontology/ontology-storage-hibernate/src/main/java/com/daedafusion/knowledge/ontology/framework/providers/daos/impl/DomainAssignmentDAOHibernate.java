package com.daedafusion.knowledge.ontology.framework.providers.daos.impl;

import com.daedafusion.hibernate.dao.AbstractDAO;
import com.daedafusion.knowledge.ontology.DomainAssignment;
import com.daedafusion.knowledge.ontology.framework.providers.daos.DomainAssignmentDAO;
import org.apache.log4j.Logger;
import org.hibernate.Query;

/**
 * Created by mphilpot on 7/15/14.
 */
public class DomainAssignmentDAOHibernate extends AbstractDAO<DomainAssignment, Long> implements DomainAssignmentDAO
{
    private static final Logger log = Logger.getLogger(DomainAssignmentDAOHibernate.class);

    @Override
    public DomainAssignment get(String domain)
    {
        Query q = getSession().createQuery("from DomainAssignment da where da.domain = :domain");
        q.setString("domain", domain);
        return (DomainAssignment) q.uniqueResult();
    }
}
