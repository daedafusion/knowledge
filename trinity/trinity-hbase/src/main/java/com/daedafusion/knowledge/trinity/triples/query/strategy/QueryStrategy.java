package com.daedafusion.knowledge.trinity.triples.query.strategy;

import com.daedafusion.knowledge.trinity.triples.query.QueryContext;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by mphilpot on 7/8/14.
 */
public interface QueryStrategy extends Closeable
{
    ExtendedIterator<Triple> find(Node subject, Node predicate, Node object) throws IOException;

    void init(QueryContext queryContext) throws IOException;

    boolean canFind(Node subject, Node predicate, Node object);
}
