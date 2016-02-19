package com.daedafusion.knowledge.ontology.framework.providers.daos;

import com.daedafusion.hibernate.dao.GenericDAO;
import com.daedafusion.knowledge.ontology.DomainAssignment;

/**
 * Created by mphilpot on 7/15/14.
 */
public interface DomainAssignmentDAO extends GenericDAO<DomainAssignment, Long>
{
    DomainAssignment get(String domain);
}
