package com.daedafusion.knowledge.query.services.util;

import com.daedafusion.knowledge.query.framework.QueryResult;
import com.daedafusion.sparql.SparqlResults;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by mphilpot on 5/9/16.
 */
public class SparqlResultsRendererTest
{
    private static final Logger log = Logger.getLogger(SparqlResultsRendererTest.class);

    @Test
    public void testLiteralsResultSetRenderer()
    {
        Model model = ModelFactory.createDefaultModel();

        model.read(SparqlResultsRendererTest.class.getClassLoader().getResourceAsStream("sample.nt"), null, "N-TRIPLE");

        assertThat(model.size(), is(5L));

        Query query = QueryFactory.create("select ?o where { ?s ?p ?o . }");

        try(QueryExecution qe = QueryExecutionFactory.create(query, model))
        {
            ResultSet resultSet = qe.execSelect();

            QueryResult qr = new QueryResult();
            qr.setResultSets(Collections.singletonList(resultSet));

            SparqlResults sr = SparqlResultsRenderer.renderResultSet(qr);

            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writeValueAsString(sr);

            assertThat(json, is(IOUtils.toString(SparqlResultsRendererTest.class.getClassLoader().getResourceAsStream("select_literals.json"))));
        }
        catch (Exception e)
        {
            log.error("Error", e);
            fail();
        }


    }
}
