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
    public void init(boolean buffered) throws IOException
    {
        this.buffered = buffered;
        tables = new HashMap<>();
        mutators = new HashMap<>();

        tables.put(Schema.SPO, HBasePool.getInstance().getTable(Schema.R_SPO));
        tables.put(Schema.POS, HBasePool.getInstance().getTable(Schema.R_POS));
        tables.put(Schema.OSP, HBasePool.getInstance().getTable(Schema.R_OSP));
        tables.put(Schema.SPO_P, HBasePool.getInstance().getTable(Schema.R_SPO_P));
        tables.put(Schema.POS_P, HBasePool.getInstance().getTable(Schema.R_POS_P));
        tables.put(Schema.OSP_P, HBasePool.getInstance().getTable(Schema.R_OSP_P));

        mutators.put(Schema.SPO, HBasePool.getInstance().getBufferedMutator(Schema.R_SPO));
        mutators.put(Schema.POS, HBasePool.getInstance().getBufferedMutator(Schema.R_POS));
        mutators.put(Schema.OSP, HBasePool.getInstance().getBufferedMutator(Schema.R_OSP));
        mutators.put(Schema.SPO_P, HBasePool.getInstance().getBufferedMutator(Schema.R_SPO_P));
        mutators.put(Schema.POS_P, HBasePool.getInstance().getBufferedMutator(Schema.R_POS_P));
        mutators.put(Schema.OSP_P, HBasePool.getInstance().getBufferedMutator(Schema.R_OSP_P));

        dictionary = new Dictionary();
        dictionary.init(buffered);

        resourceStrategy = new HashSet<>();

        for (char c : Configuration.getInstance().getString("resource.strategy", "so").toCharArray())
        {
            resourceStrategy.add(String.valueOf(c));
        }

        partitionCache = new HashMap<>();
    }
}
