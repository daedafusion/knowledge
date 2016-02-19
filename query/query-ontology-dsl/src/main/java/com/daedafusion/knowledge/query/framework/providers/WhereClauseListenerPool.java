package com.daedafusion.knowledge.query.framework.providers;

import com.daedafusion.cache.Cache;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 1/23/15.
 */
public class WhereClauseListenerPool extends GenericKeyedObjectPool<String, WhereClauseListener>
{
    private static final Logger log = Logger.getLogger(WhereClauseListenerPool.class);

    public WhereClauseListenerPool(KeyedPooledObjectFactory<String, WhereClauseListener> factory)
    {
        super(factory);
    }

    public static class WhereClauseListenerKeyedObjectFactory extends BaseKeyedPooledObjectFactory<String, WhereClauseListener>
    {
        private final Cache<String, Model> ontologyCache;

        public WhereClauseListenerKeyedObjectFactory(Cache<String, Model> ontologyCache)
        {
            this.ontologyCache = ontologyCache;
        }

        @Override
        public WhereClauseListener create(String domain) throws Exception
        {
            WhereClauseListener wcl = new WhereClauseListener(ontologyCache);
            wcl.init(domain);
            return wcl;
        }

        @Override
        public PooledObject<WhereClauseListener> wrap(WhereClauseListener whereClauseListener)
        {
            return new DefaultPooledObject<>(whereClauseListener);
        }
    }
}
