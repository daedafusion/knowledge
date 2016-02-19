package com.daedafusion.knowledge.query.definition;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 12/12/14.
 */
public class VocabularyEntry implements Comparable<VocabularyEntry>
{
    private static final Logger log = Logger.getLogger(VocabularyEntry.class);

    private String value;
    private String description;
    private Integer ordinal;

    @Override
    public int compareTo(VocabularyEntry o)
    {
        return ordinal.compareTo(o.ordinal);
    }

    public VocabularyEntry()
    {

    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal)
    {
        this.ordinal = ordinal;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VocabularyEntry that = (VocabularyEntry) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (ordinal != null ? !ordinal.equals(that.ordinal) : that.ordinal != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (ordinal != null ? ordinal.hashCode() : 0);
        return result;
    }
}
