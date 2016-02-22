package com.daedafusion.knowledge.trinity.triples.update.reified;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.dictionary.Dictionary;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.triples.update.AbstractWriter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by mphilpot on 1/27/15.
 */
public abstract class AbstractReifiedWriter extends AbstractWriter
{
    private static final Logger log = Logger.getLogger(AbstractReifiedWriter.class);

    @Override
    public void init() throws IOException
    {
        spoTable = HBasePool.getInstance().getTable(Schema.R_SPO);
        posTable = HBasePool.getInstance().getTable(Schema.R_POS);
        ospTable = HBasePool.getInstance().getTable(Schema.R_OSP);

        spopTable = HBasePool.getInstance().getTable(Schema.R_SPO_P);
        pospTable = HBasePool.getInstance().getTable(Schema.R_POS_P);
        osppTable = HBasePool.getInstance().getTable(Schema.R_OSP_P);

        dictionary = new Dictionary();
        dictionary.init();

        resourceStrategy = new HashSet<>();

        for (char c : Configuration.getInstance().getString("resource.strategy", "so").toCharArray())
        {
            resourceStrategy.add(String.valueOf(c));
        }

        partitionCache = new HashMap<>();
    }
}
