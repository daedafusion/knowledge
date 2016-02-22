package com.daedafusion.knowledge.query.framework.providers;

import com.daedafusion.cache.Cache;
import com.daedafusion.cache.CacheManager;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 9/5/14.
 */
public class OntDSLConverter extends AbstractProvider implements DSLToSparqlProvider
{
    private static final Logger log = Logger.getLogger(OntDSLConverter.class);

    private final QueryConverter queryConverter;
    private WhereClauseListenerPool pool;

    public OntDSLConverter()
    {
        queryConverter = new QueryConverter();

        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                Cache<String, Model> ontologyCache = getServiceRegistry().getService(CacheManager.class).getCache("ontologies");

                pool = new WhereClauseListenerPool(new WhereClauseListenerPool.WhereClauseListenerKeyedObjectFactory(ontologyCache));
            }

            @Override
            public void start()
            {

            }

            @Override
            public void stop()
            {

            }

            @Override
            public void teardown()
            {
            }
        });
    }

    @Override
    public String convert(String domain, String knowledgeQuery)
    {
        WhereClauseListener wcl = null;

        try
        {
            wcl = pool.borrowObject(domain);
            wcl.reset();
            return queryConverter.getSparql(wcl, knowledgeQuery);
        }
        catch (Exception e)
        {
            log.error("", e);
            throw new QueryParserException(e);
        }
        finally
        {
            if(wcl != null)
                pool.returnObject(domain, wcl);
        }
    }


}
