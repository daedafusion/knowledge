package com.daedafusion.knowledge.trinity.triples.query;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.knowledge.trinity.Query;
import com.daedafusion.knowledge.trinity.TripleMeta;
import com.daedafusion.knowledge.trinity.triples.query.strategy.*;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Logger;
import com.daedafusion.knowledge.trinity.dictionary.Dictionary;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

/**
 * Created by mphilpot on 7/8/14.
 */
public class QueryContext implements Closeable
{
    private static final Logger log = Logger.getLogger(QueryContext.class);

    private Query                    query;
    protected Dictionary               dictionary;
    private Map<Integer, List<TripleMeta>> metadata;

    protected List<QueryStrategy> strategies;
    protected List<QueryStrategy> postfixStrategies;

    private enum Strategy {POSTFIX_ONLY, PREFIX_ONLY, ADAPTIVE}

    private Strategy strategy;

    /**
     * key1 => partition
     * key2 => b64 startkey for stage
     * value => b64 lastKeySeen
     */
    private Map<String, Map<String, String>> cursorMap;

    public QueryContext()
    {
        metadata = new HashMap<>();
        strategies = new ArrayList<>();
        postfixStrategies = new ArrayList<>();
        cursorMap = new HashMap<>();
    }

    public void init() throws IOException
    {
        dictionary = new Dictionary();
        dictionary.init(true);

        ObjectQueryStrategy oqs = new ObjectQueryStrategy();
        oqs.init(this);
        SubjectQueryStrategy sqs = new SubjectQueryStrategy();
        sqs.init(this);
        PredicateQueryStrategy pqs = new PredicateQueryStrategy();
        pqs.init(this);
        AnyQueryStrategy aqs = new AnyQueryStrategy();
        aqs.init(this);

        ObjectPostfixQueryStrategy opqs = new ObjectPostfixQueryStrategy();
        opqs.init(this);
        SubjectPostfixQueryStrategy spqs = new SubjectPostfixQueryStrategy();
        spqs.init(this);
        PredicatePostfixQueryStrategy ppqs = new PredicatePostfixQueryStrategy();
        ppqs.init(this);

        strategies.add(oqs);
        strategies.add(sqs);
        strategies.add(pqs);
        strategies.add(aqs);

        postfixStrategies.add(opqs);
        postfixStrategies.add(spqs);
        postfixStrategies.add(ppqs);

        /*
            postfix-only (1)
            prefix-only (2)
            adaptive? (3)
         */

        switch (Configuration.getInstance().getString("trinity.query.strategy", "postfix-only"))
        {
            case "postfix-only":
                strategy = Strategy.POSTFIX_ONLY;
                break;
            case "prefix-only":
                strategy = Strategy.PREFIX_ONLY;
                break;
            default:
                strategy = Strategy.POSTFIX_ONLY;
        }
    }

    public Query getQuery()
    {
        return query;
    }

    public void setQuery(Query query)
    {
        this.query = query;
    }

    public Map<Integer, List<TripleMeta>> getMetadata()
    {
        return metadata;
    }

    public void setMetadata(Map<Integer, List<TripleMeta>> metadata)
    {
        this.metadata = metadata;
    }

    public Dictionary getDictionary()
    {
        return dictionary;
    }

    public Map<String, Map<String, String>> getCursorMap()
    {
        return cursorMap;
    }

    public void setCursorMap(Map<String, Map<String, String>> cursorMap)
    {
        this.cursorMap = cursorMap;
    }

    public ExtendedIterator<Triple> find(Node subject, Node predicate, Node object) throws IOException
    {
        if(strategy.equals(Strategy.POSTFIX_ONLY))
        {
            for (QueryStrategy strategy : postfixStrategies)
            {
                if (strategy.canFind(subject, predicate, object))
                {
                    return strategy.find(subject, predicate, object);
                }
            }
        }
        else if(strategy.equals(Strategy.PREFIX_ONLY))
        {
            for (QueryStrategy strategy : strategies)
            {
                if (strategy.canFind(subject, predicate, object))
                {
                    return strategy.find(subject, predicate, object);
                }
            }
        }

        throw new IOException("Could not find a query strategy match");
    }

    public boolean hasTimeConstraints()
    {
        return query.getAfter() != null || query.getBefore() != null;
    }

    public void clearContext()
    {
        query = null;
        metadata.clear();
        cursorMap = new HashMap<>();
    }

    @Override
    public void close() throws IOException
    {
        log.debug("Closing QueryContext");
        dictionary.close();
        for(QueryStrategy strategy : strategies)
        {
            strategy.close();
        }
    }
}
