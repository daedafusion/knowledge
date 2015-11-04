package com.daedafusion.sparql;

import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 1/7/15.
 */
public class Literal
{
    private static final Logger log = Logger.getLogger(Literal.class);

    public String value;
    public String type;
    public String lang;

    public Literal()
    {
        value = null;
        type = null;
        lang = null;
    }

    public Literal(String value, String type, String lang)
    {
        this.value = value;
        this.type = type;
        this.lang = lang;
    }

    @Override
    public String toString()
    {
        if(type != null)
            return String.format("\"%s\"^^<%s>", value, type);
        else if(lang != null)
            return String.format("\"%s\"@%s", value, lang);
        else
            return value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Literal literal = (Literal) o;

        if (lang != null ? !lang.equals(literal.lang) : literal.lang != null) return false;
        if (type != null ? !type.equals(literal.type) : literal.type != null) return false;
        if (value != null ? !value.equals(literal.value) : literal.value != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        return result;
    }
}
