package com.daedafusion.knowledge.ontology;

import javax.persistence.*;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/15/14.
 */
@Entity
@Table(name = "ontology_file", indexes = {
        @Index(name="ontfile_uuid_index", columnList = "uuid")
})
public class OntologyFile
{
    private static final Logger log = Logger.getLogger(OntologyFile.class);

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Deprecated
    private Long id;

    @Column
    private String uuid;

    @Column
    @Lob
    private String file;

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

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof OntologyFile)) return false;

        OntologyFile that = (OntologyFile) o;

        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }
}
