package com.daedafusion.knowledge.partition;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by mphilpot on 3/31/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonTest
{
    private static final Logger log = Logger.getLogger(JsonTest.class);

    private ObjectMapper mapper = new ObjectMapper();

    private void assertAllFieldsSet(Object o)
    {
        try
        {
            for(PropertyDescriptor pd : Introspector.getBeanInfo(o.getClass()).getPropertyDescriptors())
            {
                assertThat(pd.getReadMethod().getName(), pd.getReadMethod().invoke(o), is(notNullValue()));
            }
        }
        catch(Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void jsonPartition() throws IOException
    {
        Partition x = new Partition();
        x.setDomain("asdf");
        x.setCreator("qwerty");
        x.setId(123L);
        x.setName("foo");
        x.setHash("ABC");
        x.getAdmin().add("a");
        x.getRead().add("b");
        x.getWrite().add("c");
        x.getTags().add("d");
        x.getSystemTags().add("e");

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        Partition y = mapper.readValue(s, Partition.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }
}