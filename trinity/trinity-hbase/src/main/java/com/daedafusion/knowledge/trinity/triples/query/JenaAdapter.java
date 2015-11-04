package com.daedafusion.knowledge.trinity.triples.query;

import com.daedafusion.knowledge.trinity.QueryException;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by mphilpot on 7/8/14.
 */
public class JenaAdapter extends GraphBase
{
    private static final Logger log = Logger.getLogger(JenaAdapter.class);

    private QueryContext context;

    @Override
    protected ExtendedIterator<Triple> graphBaseFind(TripleMatch tm)
    {
        Iterator<Triple> queryResults;

        try
        {
            // this is very odd behavior -- clean up a bit
            return context.find(
                    tm.getMatchSubject() != null ? tm.getMatchSubject() : ((Triple)tm).getSubject(),
                    tm.getMatchPredicate() != null ? tm.getMatchPredicate() : ((Triple)tm).getPredicate(),
                    tm.getMatchObject() != null ? tm.getMatchObject() : ((Triple)tm).getObject());
        }
        catch (IOException e)
        {
            log.error("", e);
            throw new QueryException(e);
        }
    }


    public void init(QueryContext context)
    {
        this.context = context;
    }

}
