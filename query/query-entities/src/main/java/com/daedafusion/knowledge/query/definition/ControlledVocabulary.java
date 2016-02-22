package com.daedafusion.knowledge.query.definition;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mphilpot on 12/12/14.
 */
public class ControlledVocabulary
{
    private static final Logger log = Logger.getLogger(ControlledVocabulary.class);

    private String uri;
    private String name;
    private String description;

    private List<VocabularyEntry> entries;

    public ControlledVocabulary()
    {
        entries = new ArrayList<>();
    }

    public ControlledVocabulary(String uri)
    {
        this();
        this.uri = uri;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<VocabularyEntry> getEntries()
    {
        return entries;
    }

    public void setEntries(List<VocabularyEntry> entries)
    {
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControlledVocabulary that = (ControlledVocabulary) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return !(entries != null ? !entries.equals(that.entries) : that.entries != null);

    }

    @Override
    public int hashCode()
    {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (entries != null ? entries.hashCode() : 0);
        return result;
    }
}
