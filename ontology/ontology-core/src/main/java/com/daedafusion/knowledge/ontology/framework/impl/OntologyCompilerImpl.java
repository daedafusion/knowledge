package com.daedafusion.knowledge.ontology.framework.impl;

import com.daedafusion.knowledge.ontology.OntologyDescription;
import com.daedafusion.knowledge.ontology.framework.OntologyCompiler;
import com.daedafusion.knowledge.ontology.framework.providers.OntologyCompilerProvider;
import com.daedafusion.knowledge.ontology.model.OntologyModel;
import com.daedafusion.sf.AbstractService;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by mphilpot on 7/14/14.
 */
public class OntologyCompilerImpl extends AbstractService<OntologyCompilerProvider> implements OntologyCompiler
{
    private static final Logger log = Logger.getLogger(OntologyCompilerImpl.class);


    @Override
    public Class getProviderInterface()
    {
        return OntologyCompilerProvider.class;
    }


    @Override
    public OntologyDescription describeOntologyClosure(OntologyModel model)
    {
        return getSingleProvider().describeOntologyClosure(model);
    }
}
