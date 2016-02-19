package com.daedafusion.knowledge.ontology.framework.providers.daos;

import com.daedafusion.hibernate.dao.GenericDAO;
import com.daedafusion.knowledge.ontology.OntologySet;

/**
 * Created by mphilpot on 7/15/14.
 */
public interface OntologySetDAO extends GenericDAO<OntologySet, Long>
{
    OntologySet get(String uuid);
}
