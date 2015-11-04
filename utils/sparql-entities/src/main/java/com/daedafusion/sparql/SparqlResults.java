package com.daedafusion.sparql;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 6/23/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SparqlResults
{
    private static final Logger log = Logger.getLogger(SparqlResults.class);

    private Head head;
    private Results results;

    @JsonProperty("boolean")
    private Boolean bool;

    public SparqlResults()
    {
        head = new Head();
        results = null;
        bool = null;
    }

    public Boolean getBool()
    {
        return bool;
    }

    public void setBool(Boolean bool)
    {
        this.bool = bool;
    }

    public Results getResults()
    {
        return results;
    }

    public void setResults(Results results)
    {
        this.results = results;
    }

    public Head getHead()
    {
        return head;
    }

    public void setHead(Head head)
    {
        this.head = head;
    }
}
