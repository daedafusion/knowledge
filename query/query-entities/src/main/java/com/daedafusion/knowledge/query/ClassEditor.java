package com.daedafusion.knowledge.query;

import com.daedafusion.knowledge.query.definition.ClassDefinition;
import com.daedafusion.knowledge.query.instance.ClassInstance;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mphilpot on 12/12/14.
 */
public class ClassEditor
{
    private static final Logger log = Logger.getLogger(ClassEditor.class);

    private String rdfType;
    private String instanceUri;

    private ClassDefinition definition;
    private ClassInstance instance;

    /**
     * Partitions that contain the instanceUri triple (and probably the rdfType triple)
     */
    private Set<String> partitions;

    public ClassEditor()
    {
        definition = new ClassDefinition();
        partitions = new HashSet<>();
    }

    public ClassEditor(String rdfType)
    {
        this();
        this.rdfType = rdfType;
        this.definition.setRdfType(rdfType);
    }

    public String getRdfType()
    {
        return rdfType;
    }

    public void setRdfType(String rdfType)
    {
        this.rdfType = rdfType;
    }

    public String getInstanceUri()
    {
        return instanceUri;
    }

    public void setInstanceUri(String instanceUri)
    {
        this.instanceUri = instanceUri;
    }

    public ClassDefinition getDefinition()
    {
        return definition;
    }

    public void setDefinition(ClassDefinition definition)
    {
        this.definition = definition;
    }

    public ClassInstance getInstance()
    {
        return instance;
    }

    public void setInstance(ClassInstance instance)
    {
        this.instance = instance;
    }

    public Set<String> getPartitions()
    {
        return partitions;
    }

    public void setPartitions(Set<String> partitions)
    {
        this.partitions = partitions;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassEditor editor = (ClassEditor) o;

        if (rdfType != null ? !rdfType.equals(editor.rdfType) : editor.rdfType != null) return false;
        if (instanceUri != null ? !instanceUri.equals(editor.instanceUri) : editor.instanceUri != null) return false;
        if (definition != null ? !definition.equals(editor.definition) : editor.definition != null) return false;
        if (instance != null ? !instance.equals(editor.instance) : editor.instance != null) return false;
        return !(partitions != null ? !partitions.equals(editor.partitions) : editor.partitions != null);

    }

    @Override
    public int hashCode()
    {
        int result = rdfType != null ? rdfType.hashCode() : 0;
        result = 31 * result + (instanceUri != null ? instanceUri.hashCode() : 0);
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        result = 31 * result + (partitions != null ? partitions.hashCode() : 0);
        return result;
    }
}
