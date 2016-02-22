package com.daedafusion.knowledge.ontology.framework.providers.daos;

import com.daedafusion.hibernate.dao.GenericDAO;
import com.daedafusion.knowledge.ontology.OntologyMeta;

import java.util.List;

/**
 * Created by mphilpot on 7/15/14.
 */
public interface OntologyMetaDAO extends GenericDAO<OntologyMeta, Long>
{
    OntologyMeta get(String uuid);
    List<OntologyMeta> getForDomain(String domain);
}
