package com.daedafusion.knowledge.update.framework.providers;

import com.daedafusion.sf.Provider;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Created by mphilpot on 8/18/14.
 */
public interface AsyncUpdateProvider extends Provider
{
    void update(Model model, Long epoch, String partition, String externalSource, String ingestId);
    void update(String nTripleLine, Long epoch, String partition, String externalSource, String ingestId);
    void truncate(String partition);

    String getNamedGraph();
}
