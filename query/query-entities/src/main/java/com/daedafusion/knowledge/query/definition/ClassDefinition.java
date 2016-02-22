package com.daedafusion.knowledge.query.definition;

import com.daedafusion.sparql.Literal;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 12/12/14.
 */
public class ClassDefinition
{
    private static final Logger log = Logger.getLogger(ClassDefinition.class);

    private String rdfType;
    private Set<Literal> labels;
    private Set<Literal> comments;
    private Set<String> parents;
    private Boolean isAbstract;

    private String instanceLabelTemplate;
    private String uriTemplate;

    private List<DataPropertyDefinition> dataProperties;
    private List<ObjectPropertyDefinition> objectProperties;

    public ClassDefinition()
    {
        labels = new HashSet<>();
        comments = new HashSet<>();
        parents = new HashSet<>();
        dataProperties = new ArrayList<>();
        objectProperties = new ArrayList<>();
        isAbstract = false;
    }

    public ClassDefinition(String rdfType)
    {
        this();
        this.rdfType = rdfType;
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

    public String getInstanceLabelTemplate()
    {
        return instanceLabelTemplate;
    }

    public void setInstanceLabelTemplate(String instanceLabelTemplate)
    {
        this.instanceLabelTemplate = instanceLabelTemplate;
    }

    public List<DataPropertyDefinition> getDataProperties()
    {
        return dataProperties;
    }

    public void setDataProperties(List<DataPropertyDefinition> dataProperties)
    {
        this.dataProperties = dataProperties;
    }

    public List<ObjectPropertyDefinition> getObjectProperties()
    {
        return objectProperties;
    }

    public void setObjectProperties(List<ObjectPropertyDefinition> objectProperties)
    {
        this.objectProperties = objectProperties;
    }

    public String getUriTemplate()
    {
        return uriTemplate;
    }

    public void setUriTemplate(String uriTemplate)
    {
        this.uriTemplate = uriTemplate;
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDefinition that = (ClassDefinition) o;

        if (rdfType != null ? !rdfType.equals(that.rdfType) : that.rdfType != null) return false;
        if (labels != null ? !labels.equals(that.labels) : that.labels != null) return false;
        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
        if (parents != null ? !parents.equals(that.parents) : that.parents != null) return false;
        if (isAbstract != null ? !isAbstract.equals(that.isAbstract) : that.isAbstract != null) return false;
        if (instanceLabelTemplate != null ? !instanceLabelTemplate.equals(that.instanceLabelTemplate) : that.instanceLabelTemplate != null)
            return false;
        if (uriTemplate != null ? !uriTemplate.equals(that.uriTemplate) : that.uriTemplate != null) return false;
        if (dataProperties != null ? !dataProperties.equals(that.dataProperties) : that.dataProperties != null)
            return false;
        return !(objectProperties != null ? !objectProperties.equals(that.objectProperties) : that.objectProperties != null);

    }

    @Override
    public int hashCode()
    {
        int result = rdfType != null ? rdfType.hashCode() : 0;
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (parents != null ? parents.hashCode() : 0);
        result = 31 * result + (isAbstract != null ? isAbstract.hashCode() : 0);
        result = 31 * result + (instanceLabelTemplate != null ? instanceLabelTemplate.hashCode() : 0);
        result = 31 * result + (uriTemplate != null ? uriTemplate.hashCode() : 0);
        result = 31 * result + (dataProperties != null ? dataProperties.hashCode() : 0);
        result = 31 * result + (objectProperties != null ? objectProperties.hashCode() : 0);
        return result;
    }
}
