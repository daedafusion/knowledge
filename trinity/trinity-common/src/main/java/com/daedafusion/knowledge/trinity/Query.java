package com.daedafusion.knowledge.trinity;

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by mphilpot on 7/8/14.
 */
@Entity
@Table(name = "queries")
public class Query
{
    private static final Logger log = Logger.getLogger(Query.class);

    public static final String SPARQL_QUERY = "application/sparql-query";
    public static final String KNOWLEDGE_QUERY = "application/knowledge-query";
    public static final String SPIDER_QUERY = "application/spider-query";

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Deprecated
    private Long id;

    @Column
    private String query;

    @Column
    private String queryType;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column
    private Set<String> partitions;

    @Column
    private Long after;

    @Column
    private Long before;

    @Column
    private Integer limit;

    @Column
    private Integer offset;

    @Column
    private String cursor;

    public Query()
    {
        limit = 100;
        offset = 0;
        partitions = new TreeSet<>();
        queryType = SPARQL_QUERY;
    }

    public Query(Query query)
    {
        this();
        this.query = query.query;
        this.queryType = query.queryType;
        this.partitions.addAll(query.partitions);
        this.after = query.after;
        this.before = query.before;
        this.limit = query.limit;
        this.offset = query.offset;
        this.cursor = query.cursor;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public Set<String> getPartitions()
    {
        return partitions;
    }

    public void setPartitions(Set<String> partitions)
    {
        this.partitions = partitions;
    }

    public Long getAfter()
    {
        return after;
    }

    public void setAfter(Long after)
    {
        this.after = after;
    }

    public Long getBefore()
    {
        return before;
    }

    public void setBefore(Long before)
    {
        this.before = before;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }

    public Integer getOffset()
    {
        return offset;
    }

    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }

    public String getCursor()
    {
        return cursor;
    }

    public void setCursor(String cursor)
    {
        this.cursor = cursor;
    }

    public String getQueryType()
    {
        return queryType;
    }

    public void setQueryType(String queryType)
    {
        this.queryType = queryType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query1 = (Query) o;

        if (query != null ? !query.equals(query1.query) : query1.query != null) return false;
        if (queryType != null ? !queryType.equals(query1.queryType) : query1.queryType != null) return false;
        if (partitions != null ? !partitions.equals(query1.partitions) : query1.partitions != null) return false;
        if (after != null ? !after.equals(query1.after) : query1.after != null) return false;
        if (before != null ? !before.equals(query1.before) : query1.before != null) return false;
        if (limit != null ? !limit.equals(query1.limit) : query1.limit != null) return false;
        if (offset != null ? !offset.equals(query1.offset) : query1.offset != null) return false;
        return !(cursor != null ? !cursor.equals(query1.cursor) : query1.cursor != null);

    }

    @Override
    public int hashCode()
    {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (queryType != null ? queryType.hashCode() : 0);
        result = 31 * result + (partitions != null ? partitions.hashCode() : 0);
        result = 31 * result + (after != null ? after.hashCode() : 0);
        result = 31 * result + (before != null ? before.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (offset != null ? offset.hashCode() : 0);
        result = 31 * result + (cursor != null ? cursor.hashCode() : 0);
        return result;
    }
}
