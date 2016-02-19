package com.daedafusion.knowledge.query.framework.impl;

import com.daedafusion.sf.AbstractService;
import com.daedafusion.knowledge.query.framework.QueryManager;
import com.daedafusion.knowledge.query.framework.QueryResult;
import com.daedafusion.knowledge.query.framework.providers.QueryManagerProvider;
import com.daedafusion.knowledge.trinity.Query;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Created by mphilpot on 9/5/14.
 */
public class QueryManagerImpl extends AbstractService<QueryManagerProvider> implements QueryManager
{
    private static final Logger log = Logger.getLogger(QueryManagerImpl.class);

    @Override
    public QueryResult query(Query query)
    {
        return getSingleProvider().query(query);
    }

    @Override
    public QueryResult query(List<Query> queryList)
    {
        return getSingleProvider().query(queryList);
    }

    @Override
    public QueryResult select(Query query)
    {
        return getSingleProvider().select(query);
    }

    @Override
    public QueryResult select(List<Query> queryList)
    {
        return getSingleProvider().select(queryList);
    }

    @Override
    public List<String> literalPrefixLookup(List<String> predicates, String literalPrefix) throws IOException
    {
        return getSingleProvider().literalPrefixLookup(predicates, literalPrefix);
    }

    @Override
    public Class getProviderInterface()
    {
        return QueryManagerProvider.class;
    }
}
