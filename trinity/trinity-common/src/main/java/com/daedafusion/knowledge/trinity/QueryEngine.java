package com.daedafusion.knowledge.trinity;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by mphilpot on 11/2/15.
 */
public interface QueryEngine extends Closeable
{
    void init() throws IOException;

    Model graphQuery(Query q) throws IOException;

    ResultSet execSelect(Query q) throws IOException;
}
