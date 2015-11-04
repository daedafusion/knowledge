package com.df.argos.commons.entities.sparql;

import com.daedafusion.sparql.SparqlResults;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by mphilpot on 6/23/14.
 */
public class BookTest
{
    private static Logger log = Logger.getLogger(BookTest.class);

    @Test
    public void deserialize() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        SparqlResults results = mapper.readValue(BookTest.class.getClassLoader().getResourceAsStream("books.json"), SparqlResults.class);

        assertThat(results.getHead().getVars().size(), is(2));
        assertThat(results.getHead().getVars(), hasItem("book"));
        assertThat(results.getResults().getBindings().size(), is(7));
        assertThat(results.getResults().getBindings().get(0).keySet().size(), is(2));
        assertThat(results.getResults().getBindings().get(0).get("book").get("type"), is("uri"));
    }

    @Test
    public void serialize() throws IOException
    {
        // Tested by inspection

        ObjectMapper mapper = new ObjectMapper();

        SparqlResults results = mapper.readValue(BookTest.class.getClassLoader().getResourceAsStream("books.json"), SparqlResults.class);

        String s = mapper.writeValueAsString(results);

        //log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results));
    }
}
