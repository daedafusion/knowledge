package com.daedafusion.knowledge.trinity.triples.query.strategy;

/**
 * Created by mphilpot on 9/5/14.
 */
public interface KeyParser
{
    Long getPartitionHash(byte[] bytes);
    Long getSubjectHash(byte[] bytes);
    Long getPredicateHash(byte[] bytes);
    Long getObjectHash(byte[] bytes);
}
