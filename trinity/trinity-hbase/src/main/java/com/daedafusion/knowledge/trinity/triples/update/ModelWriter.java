package com.daedafusion.knowledge.trinity.triples.update;

import com.hp.hpl.jena.rdf.model.Model;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by mphilpot on 1/27/15.
 */
public interface ModelWriter extends Closeable
{
    void delete(Model model, String partition) throws IOException;

    void write(Model model, String partition, Long epoch, String externalSource, String ingestId) throws IOException;
}
