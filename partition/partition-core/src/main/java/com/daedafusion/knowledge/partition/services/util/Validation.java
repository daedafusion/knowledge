package com.daedafusion.knowledge.partition.services.util;

import com.daedafusion.knowledge.partition.Partition;
import com.google.common.hash.Hashing;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;

/**
 * Created by mphilpot on 7/15/14.
 */
public class Validation
{
    private static final Logger log = Logger.getLogger(Validation.class);


    public static void validateHash(Partition partition)
    {
        // TODO this needs to be the same as the trinity plugin hashing method.  Eventually this should be plugable
        String hash = Base64.encodeBase64String(
                Hashing.murmur3_128().hashString(partition.getUuid(), Charset.forName("UTF-8")).asBytes());

        if(partition.getHash() == null)
        {
            partition.setHash(hash);
        }
        else if(!partition.getHash().equals(hash))
        {
            log.warn("Incorrect hash value for partition. Overwriting");
            partition.setHash(hash);
        }
    }
}
