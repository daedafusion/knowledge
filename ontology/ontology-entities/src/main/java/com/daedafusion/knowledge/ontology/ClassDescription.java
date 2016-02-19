package com.daedafusion.knowledge.ontology;

import com.daedafusion.sparql.Literal;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 7/14/14.
 */
public class ClassDescription implements Comparable<ClassDescription>
{
    private static final Logger log = Logger.getLogger(ClassDescription.class);

    private String      namespacePrefix;
    private String      rdfType;
    private Set<Literal> labels;
    private Set<Literal> comments;

    private Boolean isAbstract;
    private Set<String>              parents;
    private List<PropertyDescription> dataProperties;
    private List<PropertyDescription> objectProperties;

    public ClassDescription()
    {
        labels = new HashSet<>();
        comments = new HashSet<>();
        parents = new HashSet<>();
        dataProperties = new ArrayList<>();
        objectProperties = new ArrayList<>();
        isAbstract = false;
    }

    public ClassDescription(String rdfType)
    {
        this();
        this.rdfType = rdfType;
    }

    public String getNamespacePrefix()
    {
        return namespacePrefix;
    }

    public void setNamespacePrefix(String namespacePrefix)
    {
        this.namespacePrefix = namespacePrefix;
    }

    public String getRdfType()
    {
        return rdfType;
    }

    public void setRdfType(String rdfType)
    {
        this.rdfType = rdfType;
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

    public Set<String> getParents()
    {
        return parents;
    }

    public void setParents(Set<String> parents)
    {
        this.parents = parents;
    }

    public List<PropertyDescription> getDataProperties()
    {
        return dataProperties;
    }

    public void setDataProperties(List<PropertyDescription> dataProperties)
    {
        this.dataProperties = dataProperties;
    }

    public List<PropertyDescription> getObjectProperties()
    {
        return objectProperties;
    }

    public void setObjectProperties(List<PropertyDescription> objectProperties)
    {
        this.objectProperties = objectProperties;
    }

    public Boolean getIsAbstract()
    {
        return isAbstract;
    }

    public void setIsAbstract(Boolean isAbstract)
    {
        this.isAbstract = isAbstract;
    }

    @Override
    public int compareTo(ClassDescription o)
    {
        return rdfType.compareTo(o.getRdfType());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDescription that = (ClassDescription) o;

        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
        if (dataProperties != null ? !dataProperties.equals(that.dataProperties) : that.dataProperties != null)
            return false;
        if (isAbstract != null ? !isAbstract.equals(that.isAbstract) : that.isAbstract != null) return false;
        if (labels != null ? !labels.equals(that.labels) : that.labels != null) return false;
        if (namespacePrefix != null ? !namespacePrefix.equals(that.namespacePrefix) : that.namespacePrefix != null)
            return false;
        if (objectProperties != null ? !objectProperties.equals(that.objectProperties) : that.objectProperties != null)
            return false;
        if (parents != null ? !parents.equals(that.parents) : that.parents != null) return false;
        if (rdfType != null ? !rdfType.equals(that.rdfType) : that.rdfType != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = namespacePrefix != null ? namespacePrefix.hashCode() : 0;
        result = 31 * result + (rdfType != null ? rdfType.hashCode() : 0);
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (isAbstract != null ? isAbstract.hashCode() : 0);
        result = 31 * result + (parents != null ? parents.hashCode() : 0);
        result = 31 * result + (dataProperties != null ? dataProperties.hashCode() : 0);
        result = 31 * result + (objectProperties != null ? objectProperties.hashCode() : 0);
        return result;
    }
}
