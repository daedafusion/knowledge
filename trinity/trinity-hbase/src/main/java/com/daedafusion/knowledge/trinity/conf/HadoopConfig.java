package com.daedafusion.knowledge.trinity.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 8/6/14.
 */
public class HadoopConfig
{
    private static final Logger log = Logger.getLogger(HadoopConfig.class);

    private static Configuration initialConfig;

    public static void setInitialConfig(Configuration config)
    {
        initialConfig = config;
    }

    public static Configuration getConfiguration()
    {
        Configuration config = initialConfig == null ? new Configuration() : initialConfig;

        config.setClassLoader(HadoopConfig.class.getClassLoader());

        // Override from distributed config
        String fsDefault = com.daedafusion.configuration.Configuration.getInstance().getString("fs.defaultFS");
        String frameworkName = com.daedafusion.configuration.Configuration.getInstance().getString("mapreduce.framework.name");
        String yarnResourceMgr = com.daedafusion.configuration.Configuration.getInstance().getString("yarn.resourcemanager.address");
        String yarnNodeMgrAux = com.daedafusion.configuration.Configuration.getInstance().getString("yarn.nodemanager.aux-services");

        if(fsDefault != null)
            config.set("fs.defaultFS", fsDefault);
        if(frameworkName != null)
            config.set("mapreduce.framework.name", frameworkName);
        if(yarnResourceMgr != null)
            config.set("yarn.resourcemanager.address", yarnResourceMgr);
        if(yarnNodeMgrAux != null)
            config.set("yarn.nodemanager.aux-services", yarnNodeMgrAux);

        return config;
    }
}
