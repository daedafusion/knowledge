package com.daedafusion.sparql;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mphilpot on 6/23/14.
 */
public class Results
{
    private static final Logger log = Logger.getLogger(Results.class);

    private List<Map<String, Map<String, String>>> bindings;

    public Results()
    {
        bindings = new ArrayList<Map<String, Map<String, String>>>();
    }

    public List<Map<String, Map<String, String>>> getBindings()
    {
        return bindings;
    }

    public void setBindings(List<Map<String, Map<String, String>>> bindings)
    {
        this.bindings = bindings;
    }

    public ConstructResult getConstructResult(int i)
    {
        return new ConstructResult(bindings.get(i));
    }

    public static class ConstructResult
    {
        private Map<String, Map<String, String>> result;

        protected ConstructResult(Map<String, Map<String, String>> result)
        {
            this.result = result;
        }

        public Map<String, String> getSubject()
        {
            return result.get("s");
        }

        public Map<String, String> getPredicate()
        {
            return result.get("p");
        }

        public Map<String, String> getObject()
        {
            return result.get("p");
        }
    }

}
