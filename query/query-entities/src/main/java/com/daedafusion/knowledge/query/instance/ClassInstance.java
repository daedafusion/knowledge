package com.daedafusion.knowledge.query.instance;

import com.daedafusion.sparql.Literal;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 12/12/14.
 */
public class ClassInstance
{
    private static final Logger log = Logger.getLogger(ClassInstance.class);

    private String rdfType;
    private String uri;
    private Literal label;

    private List<DataPropertyInstance> dataProperties;
    private List<ObjectPropertyInstance> objectProperties;

    private Reification reification;

    /**
     * Union of all partitions that contain parts of this instance
     */
    private Set<String> partitionUnion;

    public ClassInstance()
    {
        dataProperties = new ArrayList<>();
        objectProperties = new ArrayList<>();
        partitionUnion = new HashSet<>();
    }

    public ClassInstance(String uri)
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

    public Literal getLabel()
    {
        return label;
    }

    public void setLabel(Literal label)
    {
        this.label = label;
    }

    public List<DataPropertyInstance> getDataProperties()
    {
        return dataProperties;
    }

    public void setDataProperties(List<DataPropertyInstance> dataProperties)
    {
        this.dataProperties = dataProperties;
    }

    public List<ObjectPropertyInstance> getObjectProperties()
    {
        return objectProperties;
    }

    public void setObjectProperties(List<ObjectPropertyInstance> objectProperties)
    {
        this.objectProperties = objectProperties;
    }

    public Set<String> getPartitionUnion()
    {
        return partitionUnion;
    }

    public void setPartitionUnion(Set<String> partitionUnion)
    {
        this.partitionUnion = partitionUnion;
    }

    public String getRdfType()
    {
        return rdfType;
    }

    public void setRdfType(String rdfType)
    {
        this.rdfType = rdfType;
    }

    public Reification getReification()
    {
        return reification;
    }

    public void setReification(Reification reification)
    {
        this.reification = reification;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassInstance that = (ClassInstance) o;

        if (rdfType != null ? !rdfType.equals(that.rdfType) : that.rdfType != null) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (dataProperties != null ? !dataProperties.equals(that.dataProperties) : that.dataProperties != null)
            return false;
        if (objectProperties != null ? !objectProperties.equals(that.objectProperties) : that.objectProperties != null)
            return false;
        if (reification != null ? !reification.equals(that.reification) : that.reification != null) return false;
        return !(partitionUnion != null ? !partitionUnion.equals(that.partitionUnion) : that.partitionUnion != null);

    }

    @Override
    public int hashCode()
    {
        int result = rdfType != null ? rdfType.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (dataProperties != null ? dataProperties.hashCode() : 0);
        result = 31 * result + (objectProperties != null ? objectProperties.hashCode() : 0);
        result = 31 * result + (reification != null ? reification.hashCode() : 0);
        result = 31 * result + (partitionUnion != null ? partitionUnion.hashCode() : 0);
        return result;
    }
}
