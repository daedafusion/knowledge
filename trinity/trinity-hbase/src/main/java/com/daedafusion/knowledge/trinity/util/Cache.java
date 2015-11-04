package com.daedafusion.knowledge.trinity.util;

import java.io.Closeable;

/**
 * Created by mphilpot on 7/9/14.
 */
public interface Cache<K, V> extends Closeable
{
    void put(K key, V value);

    V get(K key);

    boolean contains(K key);
}
