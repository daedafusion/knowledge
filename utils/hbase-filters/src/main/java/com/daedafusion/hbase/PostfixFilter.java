package com.daedafusion.hbase;

import com.daedafusion.hbase.protobuf.CustomFilterProtos;
import com.google.protobuf.HBaseZeroCopyByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by mphilpot on 12/16/14.
 */
public class PostfixFilter extends FilterBase
{
    private static final Logger log = Logger.getLogger(PostfixFilter.class);

    private CompareFilter.CompareOp compareOp;
    private byte[] postfix;
    private int offset;

    public PostfixFilter()
    {
        super();
    }

    public PostfixFilter(final CompareFilter.CompareOp compareOp, final byte[] postfix, int offset)
    {
        this();
        this.compareOp = compareOp;
        this.postfix = postfix;
        this.offset = offset;
    }

    @Override
    public boolean filterRowKey(byte[] buffer, int offset, int length) throws IOException
    {
        if(buffer == null || this.postfix == null)
            return true;

        if(length < this.postfix.length+this.offset)
            return true;

        int cmp = Bytes.compareTo(buffer, offset+this.offset, this.postfix.length, this.postfix, 0, this.postfix.length);

        switch (this.compareOp)
        {
            case EQUAL:
                return cmp != 0;
            case NOT_EQUAL:
                return cmp == 0;
            default:
                // Filter out by default
                return true;
        }
    }

    @Override
    public byte[] toByteArray() throws IOException
    {
        CustomFilterProtos.PostfixFilter.Builder builder = CustomFilterProtos.PostfixFilter.newBuilder();
        builder.setCompareOp(compareOp.ordinal());
        builder.setOffset(offset);
        builder.setPostfix(HBaseZeroCopyByteString.wrap(postfix));

        return builder.build().toByteArray();
    }

    public static PostfixFilter parseFrom(final byte[] bytes) throws DeserializationException
    {
        CustomFilterProtos.PostfixFilter proto;
        try
        {
            proto = CustomFilterProtos.PostfixFilter.parseFrom(bytes);
        }
        catch (InvalidProtocolBufferException e)
        {
            throw new DeserializationException(e);
        }

        final CompareFilter.CompareOp compareOp = CompareFilter.CompareOp.values()[proto.getCompareOp()];
        final int offset = proto.getOffset();
        final byte[] postfix = proto.getPostfix().toByteArray();

        return new PostfixFilter(compareOp, postfix, offset);
    }

}
