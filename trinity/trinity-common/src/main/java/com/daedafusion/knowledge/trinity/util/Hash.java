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

    public static HashBytes hashString(String value)
    {
        return new HashBytes(Hashing.murmur3_128().hashString(value, Charset.forName("UTF-8")).asBytes());
    }

    public static HashBytes hashBytes(byte[] bytes)
    {
        return new HashBytes(Hashing.murmur3_128().hashBytes(bytes).asBytes());
    }

    public static HashBytes hashNode(Node node)
    {
        return hashString(node.toString());
    }
}
