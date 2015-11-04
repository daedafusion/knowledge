package com.daedafusion.knowledge.trinity.util;

import com.google.common.hash.Hashing;
import com.hp.hpl.jena.graph.Node;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;

/**
 * Created by mphilpot on 7/8/14.
 */
public class Hash
{
    private static final Logger log = Logger.getLogger(Hash.class);

    public static long hashString(String value)
    {
        return Hashing.murmur3_128().hashString(value, Charset.forName("UTF-8")).asLong();
    }

    public static long hashBytes(byte[] bytes)
    {
        return Hashing.murmur3_128().hashBytes(bytes).asLong();
    }

    public static long hashNode(Node node)
    {
        return hashString(node.toString());
    }
}
