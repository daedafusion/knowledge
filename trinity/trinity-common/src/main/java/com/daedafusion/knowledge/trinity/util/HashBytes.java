package com.daedafusion.knowledge.trinity.util;

import com.google.common.primitives.UnsignedLong;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Created by mphilpot on 2/17/16.
 *
 * This class wraps the 128 bits (16 bytes) of the murmur3_128 bit hash
 *
 * It utilizes the org.apache.hadoop.hbase.util.Bytes for comparisions.  The 2.0 version when it is released will allow
 * us to use Bytes as an instance, thus deprecating this class.
 */
public class HashBytes
{
    private static final Logger log = Logger.getLogger(HashBytes.class);

    public static final int SIZEOF_HASH = 16;
    private static final byte[] EMPTY = new byte[SIZEOF_HASH];

    private byte[] backing;

    public HashBytes()
    {
        backing = new byte[SIZEOF_HASH];
    }

    public HashBytes(byte[] bytes)
    {
        if(bytes.length != SIZEOF_HASH)
            throw new IllegalArgumentException("HashBytes must be 16 bytes");

        backing = Bytes.copy(bytes);
    }

    public HashBytes(byte[] bytes, int offset, int length)
    {
        if(bytes.length != SIZEOF_HASH)
            throw new IllegalArgumentException("HashBytes must be 16 bytes");

        backing = Bytes.copy(bytes, offset, length);
    }

    public boolean isEmpty()
    {
        return Bytes.equals(backing, EMPTY);
    }

    public byte[] getBytes()
    {
        return backing;
    }

    public void setBytes(byte[] bytes)
    {
        if(bytes.length != SIZEOF_HASH)
            throw new IllegalArgumentException("HashBytes must be 16 bytes");

        backing = Bytes.copy(bytes);
    }

    public void setBytes(HashBytes hbytes)
    {
        setBytes(hbytes.getBytes());
    }

    /**
     * We need to mathematically add 1 to a 128 bit number.  Java primitives are lacking in this regard, so this is
     * the best I could come up with.
     * @return
     */
    public byte[] plusOne()
    {
        UnsignedLong high = UnsignedLong.fromLongBits(Bytes.toLong(backing, 0));
        UnsignedLong low = UnsignedLong.fromLongBits(Bytes.toLong(backing, 8));

        if(low.equals(UnsignedLong.MAX_VALUE))
        {
            if(high.equals(UnsignedLong.MAX_VALUE))
            {
                // Off the end of the range
                return new byte[SIZEOF_HASH];
            }
            else
            {
                high = high.plus(UnsignedLong.ONE);
            }
        }
        else
        {
            low = low.plus(UnsignedLong.ONE);
        }

        return Bytes.add(Bytes.toBytes(high.longValue()), Bytes.toBytes(low.longValue()));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashBytes hashBytes = (HashBytes) o;

        return Bytes.equals(backing, hashBytes.backing);

    }

    @Override
    public int hashCode()
    {
        return Bytes.hashCode(backing);
    }
}
