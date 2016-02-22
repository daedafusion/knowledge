package com.daedafusion.knowledge.ontology.framework;

import com.daedafusion.knowledge.ontology.OntologyDescription;
import com.daedafusion.knowledge.ontology.model.OntologyModel;

/**
 * Created by mphilpot on 2/19/16.
 */
public interface OntologyCompiler
{
    OntologyDescription describeOntologyClosure(OntologyModel model);
}
