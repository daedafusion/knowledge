package com.daedafusion.knowledge.ontology;

import javax.persistence.*;
import org.apache.log4j.Logger;

import java.util.UUID;

/**
 * Created by mphilpot on 7/14/14.
 */
@Entity
@Table(name = "ontology_meta", indexes = {
        @Index(name="uuid_index", columnList = "uuid")
})
public class OntologyMeta
{
    private static final Logger log = Logger.getLogger(OntologyMeta.class);

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Deprecated
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column
    private String url;

    @Column
    private String label;

    @Column
    private String comments;

    public OntologyMeta()
    {
        uuid = UUID.randomUUID().toString();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof OntologyMeta)) return false;

        OntologyMeta that = (OntologyMeta) o;

        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }
}
