package com.daedafusion.knowledge.query.framework.providers;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by mphilpot on 9/1/14.
 */
public class QueryConverter
{
    private static final Logger log = Logger.getLogger(QueryConverter.class);

    private Map<String, String> prefixes;

    public QueryConverter()
    {
        prefixes = new HashMap<>();

        prefixes.put("rdf", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
    }

    public String getSparql(WhereClauseListener wcl, String knowledgeQuery)
    {
        QueryLexer lexer = new QueryLexer(new ANTLRInputStream(knowledgeQuery));

        QueryParser parser = new QueryParser(new CommonTokenStream(lexer));

        parser.addErrorListener(new BaseErrorListener(){
            @Override
            public void syntaxError(@NotNull Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol, int line, int charPositionInLine, @NotNull String msg, @Nullable RecognitionException e)
            {
                super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
                throw new QueryParserException("Invalid query");
            }
        });

        parser.setBuildParseTree(true);

//        WhereClauseListener wcl = new WhereClauseListener();
//        wcl.init(ontologyCache.get(domain));

        parser.addParseListener(wcl);

        parser.expQuery();

        List<String> clauses = new ArrayList<>();

        for(Set<String> clauseSet : wcl.getClauseSets())
        {
            StringBuilder builder = new StringBuilder();

            LinkedList<String> list = new LinkedList<>(clauseSet);
            Iterator<String> iter = list.descendingIterator();

            if(wcl.getClauseSets().size() > 1)
                builder.append("{ ");

            while(iter.hasNext())
            {
                builder.append(iter.next());
            }

            if(wcl.getClauseSets().size() > 1)
                builder.append("} ");

            clauses.add(builder.toString());
        }

        String sparql = String.format("%s construct where { %s } ", buildPrefixes(), StringUtils.join(clauses, " UNION "));

        log.info(sparql);

        return sparql;
    }



    private String buildPrefixes()
    {
        StringBuilder builder = new StringBuilder();
        for(String key : prefixes.keySet())
        {
            builder.append(String.format("prefix %s: %s \n", key, prefixes.get(key)));
        }

        return builder.toString();
    }
}
