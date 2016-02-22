package com.daedafusion.knowledge.query.instance;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 1/30/15.
 */
public class Reification
{
    private static final Logger log = Logger.getLogger(Reification.class);

    public String subject;
    public String predicate;
    public String object;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reification that = (Reification) o;

        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (predicate != null ? !predicate.equals(that.predicate) : that.predicate != null) return false;
        return !(object != null ? !object.equals(that.object) : that.object != null);

    }

    @Override
    public int hashCode()
    {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }
}
