package com.daedafusion.knowledge.ontology;

import javax.persistence.*;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mphilpot on 11/14/14.
 */
@Entity
@Table(name = "domain_assignment", indexes = {
        @Index(name="uuid_index", columnList = "uuid")
})
public class DomainAssignment
{
    private static final Logger log = Logger.getLogger(DomainAssignment.class);

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Deprecated
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(unique = true)
    private String domain;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="domain_assignment_set",
            joinColumns = @JoinColumn(name="domain_assignment_id"),
            inverseJoinColumns = @JoinColumn( name="ontology_set_id")
    )
    private Set<OntologySet> ontologySets;

    public DomainAssignment()
    {
        ontologySets = new HashSet<>();
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

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public Set<OntologySet> getOntologySets()
    {
        return ontologySets;
    }

    public void setOntologySets(Set<OntologySet> ontologySets)
    {
        this.ontologySets = ontologySets;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainAssignment that = (DomainAssignment) o;

        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        return result;
    }
}
