package com.daedafusion.knowledge.trinity.util;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by mphilpot on 2/17/16.
 */
public class TestHashBytes
{
    private static final Logger log = Logger.getLogger(TestHashBytes.class);

    @Test
    public void testPlusOneInitial()
    {
        byte[] target = new byte[] {
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01,
        };

        HashBytes hb = new HashBytes();
        byte[] result = hb.plusOne();

        assertThat(Bytes.equals(result, target), is(true));
    }

    @Test
    public void testPlusOneBasicRollover()
    {
        byte[] start = new byte[] {
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x0F,
        };
        byte[] target = new byte[] {
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x10,
        };

        HashBytes hb = new HashBytes(start);
        byte[] result = hb.plusOne();

        assertThat(Bytes.equals(result, target), is(true));
    }

    @Test
    public void testPlusOneLowToHigh()
    {
        byte[] start = new byte[] {
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF
        };
        byte[] target = new byte[] {
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        };

        HashBytes hb = new HashBytes(start);
        byte[] result = hb.plusOne();

        assertThat(Bytes.equals(result, target), is(true));
    }

    @Test
    public void testPlusOneHighRollover()
    {
        byte[] start = new byte[] {
                (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
                (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF
        };
        byte[] target = new byte[] {
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
        };

        HashBytes hb = new HashBytes(start);
        byte[] result = hb.plusOne();

        assertThat(Bytes.equals(result, target), is(true));
    }
}
