package com.daedafusion.knowledge.ontology.model;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by mphilpot on 7/14/14.
 */
public class OntologyModelTest
{
    private static final Logger log = Logger.getLogger(OntologyModelTest.class);

    private OntologyModel model;

    @Before
    public void before() throws IOException
    {
        model = new OntologyModel();
        model.load(OntologyModelTest.class.getClassLoader().getResourceAsStream("editor_annotation.rdf"), "RDF/XML");
        model.load(OntologyModelTest.class.getClassLoader().getResourceAsStream("vocabulary_common.rdf"), "RDF/XML");
        model.load(OntologyModelTest.class.getClassLoader().getResourceAsStream("cpe.rdf"), "RDF/XML");

        log.info("Finished loading");
    }

    @Test
    public void testGetClasses()
    {
        Set<String> classes = model.getClasses();

//        Set<String> ops = model.getObjectProperties("http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement");
//        Set<String> ops = model.getObjectProperties("http://stix.mitre.org/Campaign#RelationshipStatement");

        assertThat(classes.size(), is(10));

        log.info(classes);
    }

    @Test
    public void dataPropertyRange()
    {
        Set<String> dps = model.getDataProperties();

        Set<String> range = model.getRangeOfProperty("http://cpe.mitre.org/cpe#CPE", "http://cpe.mitre.org/cpe#cpeName");

        assertThat(range.size(), is(1));
        assertThat(range.iterator().next(), is("http://www.w3.org/2001/XMLSchema#string"));
    }

    @Test
    public void objectPropertyRange()
    {
        Set<String> range = model.getRangeOfProperty("http://cpe.mitre.org/cpe#CPE", "http://cpe.mitre.org/cpe#deprecationInfo");

        assertThat(range.size(), is(1));
        assertThat(range.iterator().next(), is("http://cpe.mitre.org/cpe#DeprecationInfo"));
    }

}
