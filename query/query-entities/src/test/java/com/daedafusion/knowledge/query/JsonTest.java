package com.daedafusion.knowledge.query;

import com.daedafusion.sparql.Literal;
import com.daedafusion.knowledge.query.definition.*;
import com.daedafusion.knowledge.query.instance.ClassInstance;
import com.daedafusion.knowledge.query.instance.DataPropertyInstance;
import com.daedafusion.knowledge.query.instance.ObjectPropertyInstance;
import com.daedafusion.knowledge.query.instance.Reification;

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
 * Created by mphilpot on 2/18/16.
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
    public void jsonClassEditor() throws IOException
    {
        ClassEditor x = new ClassEditor();
        x.getPartitions().add("abc");
        x.setInstance(new ClassInstance());
        x.setDefinition(new ClassDefinition());
        x.setInstanceUri("http://");
        x.setRdfType("asdf");

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        ClassEditor y = mapper.readValue(s, ClassEditor.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonClassDefinition() throws IOException
    {
        ClassDefinition x = new ClassDefinition();
        x.setInstanceLabelTemplate("a");
        x.setIsAbstract(true);
        x.setRdfType("b");
        x.setUriTemplate("c");
        x.getObjectProperties().add(new ObjectPropertyDefinition("asdf"));
        x.getDataProperties().add(new DataPropertyDefinition("jkl"));
        x.getComments().add(new Literal("q", "e", "t"));
        x.getLabels().add(new Literal("j", "k", "l"));
        x.getParents().add("zxcv");

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        ClassDefinition y = mapper.readValue(s, ClassDefinition.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonControlledVocabulary() throws IOException
    {
        ControlledVocabulary x = new ControlledVocabulary();
        x.setName("a");
        x.setDescription("b");
        x.setUri("c");
        x.getEntries().add(new VocabularyEntry());

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        ControlledVocabulary y = mapper.readValue(s, ControlledVocabulary.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonDataPropertyDefinition() throws IOException
    {
        DataPropertyDefinition x = new DataPropertyDefinition();
        x.setReification("a");
        x.setUri("b");
        x.setAuto("foo");
        x.getCardinality().put("min", 1);
        x.getVocabularies().add(new ControlledVocabulary());
        x.getComments().add(new Literal("q", "e", "t"));
        x.getLabels().add(new Literal("j", "k", "l"));

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        DataPropertyDefinition y = mapper.readValue(s, DataPropertyDefinition.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonObjectPropertyDefinition() throws IOException
    {
        ObjectPropertyDefinition x = new ObjectPropertyDefinition();
        x.setUri("a");
        x.setReification("b");
        x.getCardinality().put("min", 2);
        x.getComments().add(new Literal("q", "e", "t"));
        x.getLabels().add(new Literal("j", "k", "l"));
        x.getRange().add("asdf");

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        ObjectPropertyDefinition y = mapper.readValue(s, ObjectPropertyDefinition.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonVocabularyEntry() throws IOException
    {
        VocabularyEntry x = new VocabularyEntry();
        x.setOrdinal(1);
        x.setDescription("asdf");
        x.setValue("jkl");

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        VocabularyEntry y = mapper.readValue(s, VocabularyEntry.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonClassInstance() throws IOException
    {
        ClassInstance x = new ClassInstance();
        x.setReification(new Reification());
        x.setUri("b");
        x.setRdfType("c");
        x.setLabel(new Literal("a", "b", "c"));
        x.getPartitionUnion().add("asdf");
        x.getDataProperties().add(new DataPropertyInstance());
        x.getObjectProperties().add(new ObjectPropertyInstance());

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        ClassInstance y = mapper.readValue(s, ClassInstance.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonDataPropertyInstance() throws IOException
    {
        DataPropertyInstance x = new DataPropertyInstance();
        x.setLabel(new Literal("a", "b", "c"));
        x.setUri("a");
        x.setValue(new Literal("a", "x", "c"));
        x.setPartition("asdf");
        x.setReification("http://");

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        DataPropertyInstance y = mapper.readValue(s, DataPropertyInstance.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonObjectPropertyInstance() throws IOException
    {
        ObjectPropertyInstance x = new ObjectPropertyInstance();
        x.setReification("a");
        x.setUri("b");
        x.setLabel(new Literal("a", "b", "c"));
        x.setPartition("asdf");
        x.setRdfType("j");
        x.setResourceUri("k");
        x.setObjectLabel(new Literal("a", "b", "v"));

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        ObjectPropertyInstance y = mapper.readValue(s, ObjectPropertyInstance.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void jsonReification() throws IOException
    {
        Reification x = new Reification();
        x.object = "a";
        x.predicate = "b";
        x.subject = "c";

        assertAllFieldsSet(x);

        String s = mapper.writeValueAsString(x);

        Reification y = mapper.readValue(s, Reification.class);

        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }
}
