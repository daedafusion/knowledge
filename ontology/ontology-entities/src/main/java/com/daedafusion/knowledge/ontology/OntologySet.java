package com.daedafusion.knowledge.ontology;

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.util.*;

/**
 * Created by mphilpot on 11/14/14.
 */
@Entity
@Table(name = "ontology_set", indexes = {
        @Index(name="uuid_index", columnList = "uuid")
})
public class OntologySet
{
    private static final Logger log = Logger.getLogger(OntologySet.class);

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Deprecated
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column
    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="ontology_set_meta",
            joinColumns = @JoinColumn(name="ontology_set_id"),
            inverseJoinColumns = @JoinColumn( name="ontology_meta_id")
    )
    private Set<OntologyMeta> ontologies;

    public OntologySet()
    {
        ontologies = new HashSet<>();
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<OntologyMeta> getOntologies()
    {
        return ontologies;
    }

    public void setOntologies(Set<OntologyMeta> ontologies)
    {
        this.ontologies = ontologies;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OntologySet that = (OntologySet) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
