package com.daedafusion.knowledge.query.framework.providers;

import com.df.argos.commons.cache.Cache;
import com.df.argos.commons.cache.CacheManager;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.df.argos.knowledge.query.core.QueryConverter;
import com.df.argos.knowledge.query.core.QueryParserException;
import com.df.argos.knowledge.query.core.WhereClauseListener;
import com.df.argos.knowledge.query.core.WhereClauseListenerPool;
import com.df.argos.knowledge.query.framework.providers.ConverterProvider;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 9/5/14.
 */
public class OntDSLConverter extends AbstractProvider implements ConverterProvider
{
    private static final Logger log = Logger.getLogger(DefaultConverter.class);

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
