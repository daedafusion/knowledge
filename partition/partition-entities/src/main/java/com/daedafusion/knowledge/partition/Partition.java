package com.daedafusion.knowledge.partition;

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mphilpot on 7/10/14.
 */
@Entity
@Table(name = "partitions", indexes = {
        @Index(name="partition_uuid_index", columnList = "uuid")
})
public class Partition
{
    private static final Logger log = Logger.getLogger(Partition.class);

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Deprecated
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column
    private String name;

    // Base64 Encoded
    @Column(unique = true)
    private String hash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "partition_read", joinColumns = @JoinColumn(name = "partition_id"))
    @Column(name = "read_labels")
    private Set<String> read;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "partition_write", joinColumns = @JoinColumn(name = "partition_id"))
    @Column(name = "write_labels")
    private Set<String> write;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "partition_admin", joinColumns = @JoinColumn(name = "partition_id"))
    @Column(name = "admin_labels")
    private Set<String> admin;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "partition_tags", joinColumns = @JoinColumn(name = "partition_id"))
    @Column(name = "tags")
    private Set<String> tags;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "system_tags", joinColumns = @JoinColumn(name = "partition_id"))
    @Column(name = "system_tags")
    private Set<String> systemTags;

    @Column
    private String creator; // fully qualified user

    @Column
    private String domain;

    public Partition()
    {
        uuid = UUID.randomUUID().toString();
        read = new HashSet<>();
        write = new HashSet<>();
        admin = new HashSet<>();
        tags = new HashSet<>();
        systemTags = new HashSet<>();
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

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public Set<String> getRead()
    {
        return read;
    }

    public void setRead(Set<String> read)
    {
        this.read = read;
    }

    public Set<String> getWrite()
    {
        return write;
    }

    public void setWrite(Set<String> write)
    {
        this.write = write;
    }

    public Set<String> getAdmin()
    {
        return admin;
    }

    public void setAdmin(Set<String> admin)
    {
        this.admin = admin;
    }

    public Set<String> getTags()
    {
        return tags;
    }

    public void setTags(Set<String> tags)
    {
        this.tags = tags;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public Set<String> getSystemTags()
    {
        return systemTags;
    }

    public void setSystemTags(Set<String> systemTags)
    {
        this.systemTags = systemTags;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Partition partition = (Partition) o;

        if (admin != null ? !admin.equals(partition.admin) : partition.admin != null) return false;
        if (creator != null ? !creator.equals(partition.creator) : partition.creator != null) return false;
        if (domain != null ? !domain.equals(partition.domain) : partition.domain != null) return false;
        if (hash != null ? !hash.equals(partition.hash) : partition.hash != null) return false;
        if (name != null ? !name.equals(partition.name) : partition.name != null) return false;
        if (read != null ? !read.equals(partition.read) : partition.read != null) return false;
        if (systemTags != null ? !systemTags.equals(partition.systemTags) : partition.systemTags != null) return false;
        if (tags != null ? !tags.equals(partition.tags) : partition.tags != null) return false;
        if (uuid != null ? !uuid.equals(partition.uuid) : partition.uuid != null) return false;
        if (write != null ? !write.equals(partition.write) : partition.write != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (read != null ? read.hashCode() : 0);
        result = 31 * result + (write != null ? write.hashCode() : 0);
        result = 31 * result + (admin != null ? admin.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (systemTags != null ? systemTags.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        return result;
    }
}
