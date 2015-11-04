package com.daedafusion.knowledge.trinity.triples.query.strategy;

import com.daedafusion.knowledge.trinity.triples.query.QueryContext;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by mphilpot on 7/8/14.
 */
public class AnyQueryStrategy extends AbstractQueryStrategy
{
    private static final Logger log = Logger.getLogger(AnyQueryStrategy.class);

    @Override
    public ExtendedIterator<Triple> find(Node subject, Node predicate, Node object)
    {
        return null;
    }

    @Override
    public boolean canFind(Node subject, Node predicate, Node object)
    {
        return false;
    }

    @Override
    public void close() throws IOException
    {

    }

    @Override
    public void init(QueryContext context) throws IOException
    {
        this.context = context;
        // TODO
    }
}
