package com.daedafusion.knowledge.query.definition;

import com.daedafusion.sparql.Literal;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by mphilpot on 12/12/14.
 */
public class DataPropertyDefinition
{
    private static final Logger log = Logger.getLogger(DataPropertyDefinition.class);

    public static final String CARDINALITY_MIN = "min";
    public static final String CARDINALITY_MAX = "max";
    public static final String CARDINALITY_EXACT = "exact";

    private String uri;
    private Set<String> range;
    private Set<Literal>          labels;
    private Set<Literal>          comments;
    private Map<String, Integer> cardinality;

    private String auto;

    private List<ControlledVocabulary> vocabularies;

    private String reification;

    public DataPropertyDefinition()
    {
        range = new HashSet<>();
        labels = new HashSet<>();
        comments = new HashSet<>();
        cardinality = new HashMap<>();
        vocabularies = new ArrayList<>();
    }

    public DataPropertyDefinition(String uri)
    {
        this();
        this.uri = uri;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public Set<String> getRange()
    {
        return range;
    }

    public void setRange(Set<String> range)
    {
        this.range = range;
    }

    public Set<Literal> getLabels()
    {
        return labels;
    }

    public void setLabels(Set<Literal> labels)
    {
        this.labels = labels;
    }

    public Set<Literal> getComments()
    {
        return comments;
    }

    public void setComments(Set<Literal> comments)
    {
        this.comments = comments;
    }

    public Map<String, Integer> getCardinality()
    {
        return cardinality;
    }

    public void setCardinality(Map<String, Integer> cardinality)
    {
        this.cardinality = cardinality;
    }

    public List<ControlledVocabulary> getVocabularies()
    {
        return vocabularies;
    }

    public void setVocabularies(List<ControlledVocabulary> vocabularies)
    {
        this.vocabularies = vocabularies;
    }

    public String getAuto()
    {
        return auto;
    }

    public void setAuto(String auto)
    {
        this.auto = auto;
    }

    public String getReification()
    {
        return reification;
    }

    public void setReification(String reification)
    {
        this.reification = reification;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataPropertyDefinition that = (DataPropertyDefinition) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (range != null ? !range.equals(that.range) : that.range != null) return false;
        if (labels != null ? !labels.equals(that.labels) : that.labels != null) return false;
        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
        if (cardinality != null ? !cardinality.equals(that.cardinality) : that.cardinality != null) return false;
        if (auto != null ? !auto.equals(that.auto) : that.auto != null) return false;
        if (vocabularies != null ? !vocabularies.equals(that.vocabularies) : that.vocabularies != null) return false;
        return !(reification != null ? !reification.equals(that.reification) : that.reification != null);

    }

    @Override
    public int hashCode()
    {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (range != null ? range.hashCode() : 0);
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (cardinality != null ? cardinality.hashCode() : 0);
        result = 31 * result + (auto != null ? auto.hashCode() : 0);
        result = 31 * result + (vocabularies != null ? vocabularies.hashCode() : 0);
        result = 31 * result + (reification != null ? reification.hashCode() : 0);
        return result;
    }
}
