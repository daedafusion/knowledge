package com.daedafusion.knowledge.query.framework;

import com.daedafusion.knowledge.trinity.Query;

import java.io.IOException;
import java.util.List;

/**
 * Created by mphilpot on 9/4/14.
 */
public interface QueryManager
{
    QueryResult query(Query query);
    QueryResult query(List<Query> queryList);

    QueryResult select(Query query);
    QueryResult select(List<Query> queryList);

    List<String> literalPrefixLookup(List<String> predicates, String literalPrefix) throws IOException;
}
