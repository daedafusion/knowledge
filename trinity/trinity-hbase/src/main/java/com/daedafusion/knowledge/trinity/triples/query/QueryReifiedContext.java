package com.daedafusion.knowledge.trinity.triples.query;

import com.daedafusion.knowledge.trinity.dictionary.Dictionary;
import com.daedafusion.knowledge.trinity.triples.query.strategy.reified.*;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by mphilpot on 1/26/15.
 */
public class QueryReifiedContext extends QueryContext
{
    private static final Logger log = Logger.getLogger(QueryReifiedContext.class);

    @Override
    public void init() throws IOException
    {
        dictionary = new Dictionary();
        dictionary.init();

        ObjectQueryStrategy oqs = new ObjectQueryStrategy();
        oqs.init(this);
        SubjectQueryStrategy sqs = new SubjectQueryStrategy();
        sqs.init(this);
        PredicateQueryStrategy pqs = new PredicateQueryStrategy();
        pqs.init(this);
//        AnyQueryStrategy aqs = new AnyQueryStrategy();
//        aqs.init(this);

        ObjectPostfixQueryStrategy opqs = new ObjectPostfixQueryStrategy();
        opqs.init(this);
        SubjectPostfixQueryStrategy spqs = new SubjectPostfixQueryStrategy();
        spqs.init(this);
        PredicatePostfixQueryStrategy ppqs = new PredicatePostfixQueryStrategy();
        ppqs.init(this);

        strategies.add(oqs);
        strategies.add(sqs);
        strategies.add(pqs);
        //strategies.add(aqs);

        postfixStrategies.add(opqs);
        postfixStrategies.add(spqs);
        postfixStrategies.add(ppqs);
    }
}
