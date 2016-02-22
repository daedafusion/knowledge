package com.daedafusion.knowledge.update.framework;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Created by mphilpot on 8/18/14.
 */
public interface SyncUpdate
{
    void update(String namedGraph, Model model, Long epoch, String partition, String externalSource, String ingestId);
    void delete(String namedGraph, Model model, String partition);
    void delete(String namedGraph, String subject, String partition);
    void truncate(String namedGraph, String partition);
}
