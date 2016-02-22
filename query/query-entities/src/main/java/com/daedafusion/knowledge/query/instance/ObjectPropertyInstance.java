package com.daedafusion.knowledge.query.instance;

import com.daedafusion.sparql.Literal;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 12/12/14.
 */
public class ObjectPropertyInstance
{
    private static final Logger log = Logger.getLogger(ObjectPropertyInstance.class);

    private String uri;
    private Literal label;
    private String resourceUri;
    private String partition;

    // Derived
    private String rdfType;
    private Literal objectLabel;

    private String reification;

    public ObjectPropertyInstance()
    {

    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getRdfType()
    {
        return rdfType;
    }

    public void setRdfType(String rdfType)
    {
        this.rdfType = rdfType;
    }

    public String getResourceUri()
    {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri)
    {
        this.resourceUri = resourceUri;
    }

    public String getPartition()
    {
        return partition;
    }

    public void setPartition(String partition)
    {
        this.partition = partition;
    }

    public Literal getObjectLabel()
    {
        return objectLabel;
    }

    public void setObjectLabel(Literal objectLabel)
    {
        this.objectLabel = objectLabel;
    }

    public Literal getLabel()
    {
        return label;
    }

    public void setLabel(Literal label)
    {
        this.label = label;
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

        ObjectPropertyInstance that = (ObjectPropertyInstance) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (resourceUri != null ? !resourceUri.equals(that.resourceUri) : that.resourceUri != null) return false;
        if (partition != null ? !partition.equals(that.partition) : that.partition != null) return false;
        if (rdfType != null ? !rdfType.equals(that.rdfType) : that.rdfType != null) return false;
        if (objectLabel != null ? !objectLabel.equals(that.objectLabel) : that.objectLabel != null) return false;
        return !(reification != null ? !reification.equals(that.reification) : that.reification != null);

    }

    @Override
    public int hashCode()
    {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (resourceUri != null ? resourceUri.hashCode() : 0);
        result = 31 * result + (partition != null ? partition.hashCode() : 0);
        result = 31 * result + (rdfType != null ? rdfType.hashCode() : 0);
        result = 31 * result + (objectLabel != null ? objectLabel.hashCode() : 0);
        result = 31 * result + (reification != null ? reification.hashCode() : 0);
        return result;
    }
}
