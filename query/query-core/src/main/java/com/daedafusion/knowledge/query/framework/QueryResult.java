package com.daedafusion.knowledge.query.framework;

import com.daedafusion.knowledge.trinity.TripleMeta;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mphilpot on 9/5/14.
 */
public class QueryResult
{
    private static final Logger log = Logger.getLogger(QueryResult.class);

    private Model                          model;
    private List<ResultSet>                resultSets;
    private String                         cursor;
    private Map<Integer, List<TripleMeta>> metaData;

    public QueryResult()
    {
        metaData = new HashMap<>();
    }

    public Model getModel()
    {
        return model;
    }

    public void setModel(Model model)
    {
        this.model = model;
    }

    public String getCursor()
    {
        return cursor;
    }

    public void setCursor(String cursor)
    {
        this.cursor = cursor;
    }

    public Map<Integer, List<TripleMeta>> getMetaData()
    {
        return metaData;
    }

    public void setMetaData(Map<Integer, List<TripleMeta>> metaData)
    {
        this.metaData = metaData;
    }

    public List<ResultSet> getResultSets()
    {
        return resultSets;
    }

    public void setResultSets(List<ResultSet> resultSets)
    {
        this.resultSets = resultSets;
    }
}
