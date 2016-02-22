package com.daedafusion.knowledge.ontology.framework.providers.daos;

import com.daedafusion.hibernate.dao.GenericDAO;
import com.daedafusion.knowledge.ontology.OntologyFile;

/**
 * Created by mphilpot on 7/15/14.
 */
public interface OntologyFileDAO extends GenericDAO<OntologyFile, Long>
{
    OntologyFile get(String uuid);
}
