package com.daedafusion.knowledge.query.framework.providers;

import com.daedafusion.knowledge.trinity.conf.HBaseConfig;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.sf.ServiceFrameworkException;
import com.daedafusion.sf.providers.BackgroundServiceProvider;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Created by mphilpot on 6/29/17.
 */
public class SchemaLoader extends AbstractProvider implements BackgroundServiceProvider
{
    private static final Logger log = Logger.getLogger(SchemaLoader.class);

    public SchemaLoader()
    {
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {
                try
                {
                    Configuration config = HBaseConfig.getConfiguration();
                    HBaseAdmin admin = new HBaseAdmin(config);

                    log.info("Existing tables:");
                    Arrays.stream(admin.listTableNames()).forEach(log::info);

                    log.info("Creating tables:");
                    Schema.getInstance().createAll();

                    log.info("Installed tables:");
                    Arrays.stream(admin.listTableNames()).forEach(log::info);
                }
                catch (Exception e)
                {
                    log.error(e.getMessage(), e);
                    throw new ServiceFrameworkException(e);
                }
            }
        });
    }

    @Override
    public String getName()
    {
        return "Trinity Schema Loader";
    }
}
