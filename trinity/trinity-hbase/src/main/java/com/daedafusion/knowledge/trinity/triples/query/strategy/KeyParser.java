package com.daedafusion.knowledge.trinity.triples.query.strategy;

import com.daedafusion.knowledge.trinity.util.HashBytes;

/**
 * Created by mphilpot on 9/5/14.
 */
public interface KeyParser
{
    HashBytes getPartitionHash(byte[] key);
    HashBytes getSubjectHash(byte[] key);
    HashBytes getPredicateHash(byte[] key);
    HashBytes getObjectHash(byte[] key);
}
