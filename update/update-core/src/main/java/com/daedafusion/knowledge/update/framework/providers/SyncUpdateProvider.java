package com.daedafusion.knowledge.update.framework.providers;

import com.daedafusion.sf.Provider;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Created by mphilpot on 8/18/14.
 */
public interface SyncUpdateProvider extends Provider
{
    void update(Model model, Long epoch, String partition, String externalSource, String ingestId);
    void delete(Model model, String partition);
    void delete(String subject, String partition);
    void truncate(String partition);

    String getNamedGraph();
}
