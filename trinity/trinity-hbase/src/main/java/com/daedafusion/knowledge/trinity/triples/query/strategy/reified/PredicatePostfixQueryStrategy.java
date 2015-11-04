package com.daedafusion.knowledge.trinity.triples.query.strategy.reified;

import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.triples.query.QueryContext;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by mphilpot on 1/26/15.
 */
public class PredicatePostfixQueryStrategy extends com.daedafusion.knowledge.trinity.triples.query.strategy.PredicatePostfixQueryStrategy
{
    private static final Logger log = Logger.getLogger(PredicatePostfixQueryStrategy.class);

    @Override
    public void init(QueryContext queryContext) throws IOException
    {
        this.context = queryContext;
        posTable = HBasePool.getInstance().getTable(Schema.R_POS_P);
    }
}
