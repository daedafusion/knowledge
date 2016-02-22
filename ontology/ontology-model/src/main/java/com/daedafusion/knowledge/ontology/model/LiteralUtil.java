package com.daedafusion.knowledge.ontology.model;

import com.hp.hpl.jena.sparql.util.NodeFactoryExtra;
import org.apache.log4j.Logger;

/**
 * Created by mphilpot on 7/14/14.
 */
public class LiteralUtil
{
    private static final Logger log = Logger.getLogger(LiteralUtil.class);

    public static String getLiteralValue(String literal)
    {
        return NodeFactoryExtra.parseNode(literal).getLiteral().getLexicalForm();
    }

    public static String getLanguage(String literal)
    {
        return NodeFactoryExtra.parseNode(literal).getLiteral().language();
    }

    public static String getDatatypeURI(String literal)
    {
        return NodeFactoryExtra.parseNode(literal).getLiteral().getDatatypeURI();
    }

    public static String buildLiteral(String value, String lang, String datatype)
    {
        StringBuilder builder = new StringBuilder(String.format("\"%s\"", value));

        if(lang != null)
        {
            builder.append(String.format("@%s", lang));
        }
        else if(datatype != null)
        {
            if(datatype.contains(":"))
            {
                builder.append(String.format("^^%s", datatype));
            }
            else
            {
                builder.append(String.format("^^<%s>", datatype));
            }
        }

        return builder.toString();
    }
}
