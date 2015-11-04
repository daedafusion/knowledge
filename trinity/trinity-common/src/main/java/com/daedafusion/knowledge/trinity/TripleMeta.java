package com.daedafusion.knowledge.trinity;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/8/14.
 */
public class TripleMeta
{
    private static final Logger log = Logger.getLogger(TripleMeta.class);

    private String partition;
    private Long epoch;
    private String externalSourceHash;
    private String ingestId;

    public TripleMeta()
    {

    }

    public Long getEpoch()
    {
        return epoch;
    }

    public void setEpoch(Long epoch)
    {
        this.epoch = epoch;
    }

    public String getExternalSourceHash()
    {
        return externalSourceHash;
    }

    public void setExternalSourceHash(String externalSourceHash)
    {
        this.externalSourceHash = externalSourceHash;
    }

    public String getIngestId()
    {
        return ingestId;
    }

    public void setIngestId(String ingestId)
    {
        this.ingestId = ingestId;
    }

    public String getPartition()
    {
        return partition;
    }

    public void setPartition(String partition)
    {
        this.partition = partition;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TripleMeta that = (TripleMeta) o;

        if (partition != null ? !partition.equals(that.partition) : that.partition != null) return false;
        if (epoch != null ? !epoch.equals(that.epoch) : that.epoch != null) return false;
        if (externalSourceHash != null ? !externalSourceHash.equals(that.externalSourceHash) : that.externalSourceHash != null)
            return false;
        return !(ingestId != null ? !ingestId.equals(that.ingestId) : that.ingestId != null);

    }

    @Override
    public int hashCode()
    {
        int result = partition != null ? partition.hashCode() : 0;
        result = 31 * result + (epoch != null ? epoch.hashCode() : 0);
        result = 31 * result + (externalSourceHash != null ? externalSourceHash.hashCode() : 0);
        result = 31 * result + (ingestId != null ? ingestId.hashCode() : 0);
        return result;
    }
}
