package com.daedafusion.knowledge.partition.framework.providers;

import com.daedafusion.knowledge.partition.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.partition.Partition;
import com.daedafusion.knowledge.partition.framework.PartitionStorage;
import com.google.common.collect.Sets;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mphilpot on 3/27/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HibernatePartitionStorageIT
{
    private final HibernatePartitionStorageProvider storage;

    public HibernatePartitionStorageIT()
    {
        this.storage = new HibernatePartitionStorageProvider();
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test0NotFoundGet()
    {
        storage.getPartition("abc");
    }
    @Test(expected = ObjectNotFoundException.class)
    public void test0NotFoundUpdate()
    {
        Partition p = new Partition();
        p.setUuid("abc");
        storage.updatePartition(p);
    }
    @Test(expected = ObjectNotFoundException.class)
    public void test0NotFoundDelete()
    {
        storage.deletePartition("abc");
    }

    @Test
    public void test10CreateTest()
    {
        Partition p = new Partition();
        p.setName("test1");
        p.setCreator("mark.philpot@daedafusion.com");
        p.getAdmin().add("mark.philpot@daedafusion.com");
        p.getAdmin().add("#global");
        p.getWrite().add("mark.philpot@daedafusion.com");
        p.getRead().add("mark.philpot@daedafusion.com");
        p.getRead().add("#daedafusion.com");
        p.getTags().add("foo");
        p.getTags().add("bar");
        p.getSystemTags().add("user");

        p = storage.createPartition(p);

        assertThat(p.getId(), is(notNullValue()));
    }

    @Test
    public void test20queries()
    {
        List<Partition> list = storage.getAllPartitions();
        assertThat(list.size(), is(1));
    }

    @Test
    public void test30update()
    {
        List<Partition> list = storage.getAllPartitions();
        assertThat(list.size(), is(1));
        Partition p = list.get(0);
        String uuid = p.getUuid();

        assertThat(uuid, is(notNullValue()));
        assertThat(p.getName(), is("test1"));

        p.setName("test1update");

        storage.updatePartition(p);

        p = storage.getPartition(uuid);

        assertThat(p, is(notNullValue()));
        assertThat(p.getUuid(), is(uuid));
        assertThat(p.getName(), is("test1update"));
    }

    @Test
    public void test40queries()
    {
        Partition p = new Partition();
        p.setName("test2");
        p.setCreator("mark.philpot@daedafusion.com");
        p.getAdmin().add("mark.philpot@daedafusion.com");
        p.getAdmin().add("#global");
        p.getWrite().add("mark.philpot@daedafusion.com");
        p.getRead().add("mark.philpot@daedafusion.com");
        p.getRead().add("#daedafusion.com");
        p.getRead().add("#other.com");
        p.getTags().add("shoo");
        p.getSystemTags().add("cases");

        p = storage.createPartition(p);

        assertThat(p.getId(), is(notNullValue()));

        List<Partition> list = storage.getAllPartitions();
        assertThat(list.size(), is(2));

        list = storage.getPartitions(PartitionStorage.Action.Read, Sets.newHashSet("nothing"));

        assertThat(list.size(), is(0));

        list = storage.getPartitions(PartitionStorage.Action.Write, Sets.newHashSet("nothing"));

        assertThat(list.size(), is(0));

        list = storage.getPartitions(PartitionStorage.Action.Admin, Sets.newHashSet("nothing"));

        assertThat(list.size(), is(0));

        list = storage.getPartitions(PartitionStorage.Action.Read, Sets.newHashSet("#other.com"));

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getName(), is("test2"));

        list = storage.getPartitions(PartitionStorage.Action.Read, Sets.newHashSet("#daedafusion.com", "#other.com"));

        assertThat(list.size(), is(2));

        list = storage.getPartitions(PartitionStorage.Action.Read, Sets.newHashSet("#daedafusion.com"), Sets.newHashSet("absent"));

        assertThat(list.size(), is(0));

        list = storage.getPartitions(PartitionStorage.Action.Read, Sets.newHashSet("#daedafusion.com"), Sets.newHashSet("shoo", "absent"));

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getName(), is("test2"));

        list = storage.getPartitions(PartitionStorage.Action.Read, Sets.newHashSet("#daedafusion.com"), new HashSet<String>(), Sets.newHashSet("user"));

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getName(), is("test1update"));
    }

}

