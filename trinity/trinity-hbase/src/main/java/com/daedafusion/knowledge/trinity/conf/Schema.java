package com.daedafusion.knowledge.trinity.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by mphilpot on 7/8/14.
 */
public class Schema
{
    private static final Logger log = Logger.getLogger(Schema.class);

    // Key Definition SPO :: hash(partition) + hash(subject) + hash(predicate) + hash(object)
    public static final int TRIPLE_KEY_LENGTH = 32; // 4 longs * 8 bytes

    public static final String NAMESPACE = "default"; // TODO separate namespace?
    public static final String T_SPO     = "t_spo";
    public static final String T_POS     = "t_pos";
    public static final String T_OSP     = "t_osp";

    // partition postfix SPO :: hash(subject) + hash(predicate) + hash(object) + hash(partition)
    public static final String T_SPO_P = "t_spo_p";
    public static final String T_POS_P = "t_pos_p";
    public static final String T_OSP_P = "t_osp_p";

    // Reified
    public static final String T_R_SPO = "t_r_spo";
    public static final String T_R_POS = "t_r_pos";
    public static final String T_R_OSP = "t_r_osp";

    public static final String T_R_SPO_P = "t_r_spo_p";
    public static final String T_R_POS_P = "t_r_pos_p";
    public static final String T_R_OSP_P = "t_r_osp_p";

    public static final String T_RESDICT = "t_resdict";
    public static final String T_PDICT   = "t_pdict";
    public static final String T_PLDICT  = "t_pldict";

    public static final TableName SPO     = TableName.valueOf(NAMESPACE, T_SPO);
    public static final TableName POS     = TableName.valueOf(NAMESPACE, T_POS);
    public static final TableName OSP     = TableName.valueOf(NAMESPACE, T_OSP);

    public static final TableName SPO_P   = TableName.valueOf(NAMESPACE, T_SPO_P);
    public static final TableName POS_P   = TableName.valueOf(NAMESPACE, T_POS_P);
    public static final TableName OSP_P   = TableName.valueOf(NAMESPACE, T_OSP_P);

    public static final TableName R_SPO = TableName.valueOf(NAMESPACE, T_R_SPO);
    public static final TableName R_POS = TableName.valueOf(NAMESPACE, T_R_POS);
    public static final TableName R_OSP = TableName.valueOf(NAMESPACE, T_R_OSP);

    public static final TableName R_SPO_P = TableName.valueOf(NAMESPACE, T_R_SPO_P);
    public static final TableName R_POS_P = TableName.valueOf(NAMESPACE, T_R_POS_P);
    public static final TableName R_OSP_P = TableName.valueOf(NAMESPACE, T_R_OSP_P);

    public static final TableName RESDICT = TableName.valueOf(NAMESPACE, T_RESDICT);
    public static final TableName PDICT   = TableName.valueOf(NAMESPACE, T_PDICT);
    public static final TableName PLDICT  = TableName.valueOf(NAMESPACE, T_PLDICT);

    public static final byte[] F_INFO            = "i".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_EPOCH           = "e".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_EXTERNAL_SOURCE = "xs".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_INGEST_ID       = "ii".getBytes(StandardCharsets.UTF_8);

    public static final byte[] F_RESOURCE  = "r".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_SUBJECT   = "s".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_PREDICATE = "p".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_OBJECT    = "o".getBytes(StandardCharsets.UTF_8);
    // Q_PARTITION
    public static final byte[] Q_OBJECT_LITERAL = "ol".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_OBJECT_LITERAL_DATATYPE = "od".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_OBJECT_LITERAL_LANG = "oll".getBytes(StandardCharsets.UTF_8);

    public static final byte[] F_DICTIONARY = "d".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_DVALUE     = "v".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_DTYPE      = "t".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_DLANG      = "l".getBytes(StandardCharsets.UTF_8);

    // TimeSeries

    // Key Definition ETS :: volume (short) + hash(partition) + -epoch (long) + hash(rdfType of Record) + hash(subject URI) + sequence (int)
    public static final int TIMESERIES_KEY_LENGTH = 38; // 1 short * 2 bytes + 4 longs * 8 bytes + 1 int * 4 bytes

    public static final String T_TS_ETS = "ts_ets"; // epoch, type, subject
    public static final String T_TS_SET = "ts_set"; // subject, epoch, type
    public static final String T_TS_TES = "ts_tes"; // type, epoch, subject

    public static final TableName ETS = TableName.valueOf(NAMESPACE, T_TS_ETS);
    public static final TableName SET = TableName.valueOf(NAMESPACE, T_TS_SET);
    public static final TableName TES = TableName.valueOf(NAMESPACE, T_TS_TES);

    // Uses F_INFO family
    public static final byte[] Q_SUBJECT_URI = "s".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_RESOURCE_URI = "r".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_PARTITION   = "pt".getBytes(StandardCharsets.UTF_8);
    public static final byte[] Q_TYPE        = "t".getBytes(StandardCharsets.UTF_8);

    private Configuration config;
    private HBaseAdmin    admin;

    private Schema()
    {
        config = HBaseConfig.getConfiguration();
    }

    public static Schema getInstance() throws IOException
    {
        Schema schema = new Schema();
        schema.init();
        return schema;
    }

    public void init() throws IOException
    {
        admin = new HBaseAdmin(config);
    }

    public void create(TableName tableName, byte[]... families) throws IOException
    {
        if(admin.tableExists(tableName))
        {
            log.warn(String.format("Table %s already exists", tableName.toString()));
            return;
        }

        HTableDescriptor descriptor = new HTableDescriptor(tableName);

        for(byte[] family : families)
        {
            descriptor.addFamily(new HColumnDescriptor(family));
        }

        admin.createTable(descriptor);
    }

    public void truncate(TableName tableName) throws IOException
    {
        HTableDescriptor descriptor = admin.getTableDescriptor(tableName);

        admin.disableTable(tableName);
        admin.deleteTable(tableName);
        admin.createTable(descriptor);
    }

    public void drop(TableName tableName) throws IOException
    {
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
    }

    public void createAll() throws IOException
    {
        create(SPO, F_INFO, F_RESOURCE);
        create(POS, F_INFO, F_RESOURCE);
        create(OSP, F_INFO, F_RESOURCE);
        create(SPO_P, F_INFO, F_RESOURCE);
        create(POS_P, F_INFO, F_RESOURCE);
        create(OSP_P, F_INFO, F_RESOURCE);
        create(R_SPO, F_INFO, F_RESOURCE);
        create(R_POS, F_INFO, F_RESOURCE);
        create(R_OSP, F_INFO, F_RESOURCE);
        create(R_SPO_P, F_INFO, F_RESOURCE);
        create(R_POS_P, F_INFO, F_RESOURCE);
        create(R_OSP_P, F_INFO, F_RESOURCE);
        create(RESDICT, F_DICTIONARY);
        create(PDICT, F_DICTIONARY);
        create(PLDICT, F_DICTIONARY);
        create(ETS, F_INFO);
        create(SET, F_INFO);
        create(TES, F_INFO);
    }

    public void truncateAll() throws IOException
    {
        truncate(SPO);
        truncate(POS);
        truncate(OSP);
        truncate(SPO_P);
        truncate(POS_P);
        truncate(OSP_P);
        truncate(R_SPO);
        truncate(R_POS);
        truncate(R_OSP);
        truncate(R_SPO_P);
        truncate(R_POS_P);
        truncate(R_OSP_P);
        truncate(RESDICT);
        truncate(PDICT);
        truncate(PLDICT);
        truncate(ETS);
        truncate(SET);
        truncate(TES);
    }

    public void dropAll() throws IOException
    {
        drop(SPO);
        drop(POS);
        drop(OSP);
        drop(SPO_P);
        drop(POS_P);
        drop(OSP_P);
        drop(R_SPO);
        drop(R_POS);
        drop(R_OSP);
        drop(R_SPO_P);
        drop(R_POS_P);
        drop(R_OSP_P);
        drop(RESDICT);
        drop(PDICT);
        drop(PLDICT);
        drop(ETS);
        drop(SET);
        drop(TES);
    }

    public static void main(String[] args) throws IOException
    {
        if(args.length != 1)
        {
            System.out.println("Missing operation (create, truncate, drop)");
            System.exit(1);
        }

        Configuration config = HBaseConfig.getConfiguration();

        if(args[0].equals("list"))
        {
            try
            {
                HBaseAdmin admin = new HBaseAdmin(config);

                for(TableName tn : admin.listTableNames())
                {
                    System.out.println(tn.toString());
                }

                return;
            }
            catch (IOException e)
            {
                log.error("", e);
            }
        }

        Schema schema = new Schema();

        if(args[0].equals("create"))
        {
            schema.createAll();

            return;
        }

        if(args[0].equals("truncate"))
        {
            schema.truncateAll();

            return;
        }

        if(args[0].equals("drop"))
        {
            schema.dropAll();

            return;
        }

        System.out.println("Invalid operation");
        System.exit(1);
    }
}
