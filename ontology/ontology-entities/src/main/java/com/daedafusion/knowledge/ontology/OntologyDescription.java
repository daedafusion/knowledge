package com.daedafusion.knowledge.ontology;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mphilpot on 7/14/14.
 */
public class OntologyDescription
{
    private static final Logger log = Logger.getLogger(OntologyDescription.class);

    private List<ClassDescription> classes;

    public OntologyDescription()
    {
        classes = new ArrayList<>();
    }

    public List<ClassDescription> getClasses()
    {
        return classes;
    }

    public void setClasses(List<ClassDescription> classes)
    {
        this.classes = classes;
    }
}
