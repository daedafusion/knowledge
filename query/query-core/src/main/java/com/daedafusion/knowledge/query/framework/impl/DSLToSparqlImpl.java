package com.daedafusion.knowledge.query.framework.impl;

import com.daedafusion.knowledge.query.framework.DSLToSparql;
import com.daedafusion.knowledge.query.framework.providers.DSLToSparqlProvider;
import com.daedafusion.sf.AbstractService;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 9/5/14.
 */
public class DSLToSparqlImpl extends AbstractService<DSLToSparqlProvider> implements DSLToSparql
{
    private static final Logger log = Logger.getLogger(DSLToSparqlImpl.class);

    @Override
    public String convert(String domain, String dslQuery)
    {
        return getSingleProvider().convert(domain, dslQuery);
    }

    @Override
    public Class getProviderInterface()
    {
        return DSLToSparqlProvider.class;
    }
}
