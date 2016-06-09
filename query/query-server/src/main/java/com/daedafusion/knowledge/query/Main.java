package com.daedafusion.knowledge.query;

import com.codahale.metrics.servlets.AdminServlet;
import com.daedafusion.configuration.Configuration;
import com.daedafusion.discovery.DiscoveryRegistrationListener;
import com.daedafusion.knowledge.query.Application;
import com.daedafusion.security.bindings.AuthorizationTokenFilter;
import com.daedafusion.service.JettyServerBuilder;
import com.daedafusion.service.OptionsUtil;
import com.daedafusion.service.bootstrap.*;
import com.daedafusion.service.metrics.HealthCheckServletContextListener;
import com.daedafusion.service.metrics.MetricsServletContextListener;
import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Filter;
import java.util.*;

/**
 * Created by mphilpot on 2/22/16.
 */
public class Main
{
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception
    {
        // Process Command Line Options
        Options options = OptionsUtil.getStandardServerOptions();
        CommandLineParser parser = new PosixParser();

        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("h"))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(String.format("java -jar %s [options] [args]", "server.jar"), options);
            return;
        }

        List<Bootstrap> bootstraps = new ArrayList<>();

        BootstrapConfig bootstrapConfig = new BootstrapConfig();

        if(cmd.hasOption("b"))
        {
            String bootstrapProperties = cmd.getOptionValue("b");

            bootstrapConfig.setBootstrapFile(bootstrapProperties);
        }

        bootstrapConfig.boot();

        bootstraps.add(bootstrapConfig);

        bootstraps.add(new BootstrapCrypto().boot());
        bootstraps.add(new BootstrapDefaultLogging().boot());
        bootstraps.add(new BootstrapServiceFramework().boot());

        // Setup Server
        JettyServerBuilder builder = new JettyServerBuilder();

        builder.newServletContext().addJerseyApplication(Application.class);

        builder.addFilter(new AuthorizationTokenFilter());

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        Map<String, Object> objects = framework.getServiceRegistry().getExternalResources();

        for(Object obj : objects.values())
        {
            if(obj instanceof Filter)
            {
                builder.addFilter((Filter) obj);
            }
        }

        if(cmd.hasOption("s"))
        {
            builder.addSslConnector(Configuration.getInstance().getInteger("servicePort"), cmd.hasOption("m"));
        }
        else
        {
            builder.addDefaultConnector(Configuration.getInstance().getInteger("servicePort"));
        }

        DiscoveryRegistrationListener discoveryListener = null;

        if(cmd.hasOption("H"))
        {
            discoveryListener = new DiscoveryRegistrationListener(
                    Configuration.getInstance().getString("serviceName"),
                    cmd.getOptionValue("H"),
                    Configuration.getInstance().getInteger("servicePort"),
                    cmd.hasOption("s")
            );
        }
        else
        {
            discoveryListener = new DiscoveryRegistrationListener(
                    Configuration.getInstance().getString("serviceName"),
                    Configuration.getInstance().getInteger("servicePort"),
                    cmd.hasOption("s")
            );
        }

        builder.addListener(discoveryListener);

        // Add Metrics
        builder.addListener(new MetricsServletContextListener());
        builder.addListener(new HealthCheckServletContextListener());

        ServletHolder h = builder.newServletHolder("/health/*");
        h.setServlet(new AdminServlet());

        Server server = builder.build();

        try
        {
            server.start();
            server.join();
        }
        catch(Exception e)
        {
            log.warn("", e);
            server.stop();
        }
        finally
        {
            Collections.reverse(bootstraps);

            for(Bootstrap b : bootstraps)
            {
                b.teardown();
            }
        }
    }
}
