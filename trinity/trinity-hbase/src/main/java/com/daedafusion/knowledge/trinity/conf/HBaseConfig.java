package com.daedafusion.knowledge.trinity.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/8/14.
 */
public class HBaseConfig
{
    private static final Logger log = Logger.getLogger(HBaseConfig.class);

    public static Configuration getConfiguration()
    {
        Configuration config = HadoopConfig.getConfiguration();

        // Skip version test since there may not be a hbase-default.xml on the classpath
        config.set("hbase.defaults.for.version.skip", "true");

        // Load in hbase-default.xml and hbase-site.xml if found
        HBaseConfiguration.addHbaseResources(config);

        // Override from distributed config
        String quorum = com.daedafusion.configuration.Configuration.getInstance().getString("hbase.zookeeper.quorum");
        String znode = com.daedafusion.configuration.Configuration.getInstance().getString("zookeeper.znode.parent");

        if(quorum != null)
            config.set("hbase.zookeeper.quorum", quorum);

        if(znode != null)
            config.set("zookeeper.znode.parent", znode);

        return config;
    }
}
