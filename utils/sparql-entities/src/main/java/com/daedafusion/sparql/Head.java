package com.daedafusion.sparql;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by mphilpot on 6/23/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Head
{
    private static final Logger log = Logger.getLogger(Head.class);

    private List<String>        vars;

    // This is a deviation from the spec... should be a List<String>
    private Map<String, String> links;

    public Head()
    {
        vars = null;
        links = null;
    }

    public List<String> getVars()
    {
        return vars;
    }

    public void setVars(List<String> vars)
    {
        this.vars = vars;
    }

    public Map<String, String> getLinks()
    {
        return links;
    }

    public void setLinks(Map<String, String> links)
    {
        this.links = links;
    }
}
