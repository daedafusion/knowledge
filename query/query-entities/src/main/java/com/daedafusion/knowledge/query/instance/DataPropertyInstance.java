package com.daedafusion.knowledge.query.instance;

import com.daedafusion.sparql.Literal;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 12/12/14.
 */
public class DataPropertyInstance
{
    private static final Logger log = Logger.getLogger(DataPropertyInstance.class);

    private String uri;
    private Literal label;
    private Literal value;
    private String partition;

    private String reification;

    public DataPropertyInstance()
    {

    }

    public Literal getValue()
    {
        return value;
    }

    public void setValue(Literal value)
    {
        this.value = value;
    }

    public String getPartition()
    {
        return partition;
    }

    public void setPartition(String partition)
    {
        this.partition = partition;
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

        DataPropertyInstance that = (DataPropertyInstance) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (partition != null ? !partition.equals(that.partition) : that.partition != null) return false;
        return !(reification != null ? !reification.equals(that.reification) : that.reification != null);

    }

    @Override
    public int hashCode()
    {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (partition != null ? partition.hashCode() : 0);
        result = 31 * result + (reification != null ? reification.hashCode() : 0);
        return result;
    }
}
