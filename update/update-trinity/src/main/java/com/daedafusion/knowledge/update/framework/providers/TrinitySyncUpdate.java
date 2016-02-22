package com.daedafusion.knowledge.update.framework.providers;

import com.daedafusion.knowledge.trinity.util.HashBytes;
import com.daedafusion.sf.AbstractProvider;
import com.daedafusion.sf.LifecycleListener;
import com.daedafusion.knowledge.trinity.util.HBasePool;
import com.daedafusion.knowledge.trinity.util.Hash;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.triples.update.ModelWriter;
import com.daedafusion.knowledge.trinity.triples.update.ModelWriterPool;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.pool2.ObjectPool;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by mphilpot on 8/18/14.
 */
public class TrinitySyncUpdate extends AbstractProvider implements SyncUpdateProvider
{
    private static final Logger log = Logger.getLogger(TrinitySyncUpdate.class);

    private final ObjectPool<ModelWriter> pool;

    private final AtomicLong counter;

    public TrinitySyncUpdate()
    {
        pool = new ModelWriterPool(new ModelWriterPool.ModelWriterObjectFactory(true, false));
        counter = new AtomicLong(0L);

        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void init()
            {

            }

            @Override
            public void start()
            {

            }

            @Override
            public void stop()
            {
                // TODO need to handle case if pool objects are in use when stop is called
                pool.close();
            }

            @Override
            public void teardown()
            {

            }
        });
    }

    @Override
    public void update(Model model, Long epoch, String partition, String externalSource, String ingestId)
    {
        ModelWriter writer = null;
        try
        {
            writer = pool.borrowObject();

            counter.getAndAdd(model.size());
            log.info(String.format("Writing %d triples sync (Total :: %d)", model.size(), counter.get()));

            writer.write(model, partition, epoch, externalSource, ingestId);
        }
        catch (IOException e)
        {
            log.error("Invalidating model writer object", e);
            try
            {
                pool.invalidateObject(writer);
            }
            catch (Exception e1)
            {
                log.error("", e1);
            }
        }
        catch (Exception e)
        {
            log.error("", e);
            // TODO what should we do with the model?
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    pool.returnObject(writer);
                }
                catch (Exception e)
                {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public void delete(Model model, String partition)
    {
        ModelWriter writer = null;
        try
        {
            writer = pool.borrowObject();

            counter.getAndAdd(model.size());
            log.info(String.format("Removing %d triples sync (Total :: %d)", model.size(), counter.get()));

            writer.delete(model, partition);
        }
        catch (IOException e)
        {
            log.error("Invalidating model writer object", e);
            try
            {
                pool.invalidateObject(writer);
            }
            catch (Exception e1)
            {
                log.error("", e1);
            }
        }
        catch (Exception e)
        {
            log.error("", e);
            // TODO what should we do with the model?
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    pool.returnObject(writer);
                }
                catch (Exception e)
                {
                    log.error("", e);
                }
            }
        }
    }

    @Override
    public void delete(String subject, String partition)
    {
        // Preserve order
        Map<HTableInterface, List<Delete>> deletes = new LinkedHashMap<>();

        try(HTableInterface spoTable = HBasePool.getInstance().getTable(Schema.SPO))
        {
            deletes.put(HBasePool.getInstance().getTable(Schema.SPO), new ArrayList<>());
            deletes.put(HBasePool.getInstance().getTable(Schema.POS), new ArrayList<>());
            deletes.put(HBasePool.getInstance().getTable(Schema.OSP), new ArrayList<>());
            deletes.put(HBasePool.getInstance().getTable(Schema.SPO_P), new ArrayList<>());
            deletes.put(HBasePool.getInstance().getTable(Schema.POS_P), new ArrayList<>());
            deletes.put(HBasePool.getInstance().getTable(Schema.OSP_P), new ArrayList<>());

            Scan scan = new Scan();

            ByteBuffer buff = ByteBuffer.allocate(Schema.TRIPLE_KEY_LENGTH);
            buff.put(Hash.hashString(partition).getBytes());
            buff.put(Hash.hashString(subject).getBytes());

            scan.setStartRow(buff.array());

            buff = ByteBuffer.allocate(Schema.TRIPLE_KEY_LENGTH);
            buff.put(Hash.hashString(partition).getBytes());
            buff.put(Hash.hashString(subject).plusOne());

            scan.setStopRow(buff.array());

            ResultScanner scanner = spoTable.getScanner(scan);

            for(Result result : scanner)
            {
                HashBytes partitionHash = new HashBytes(result.getRow(), 0, HashBytes.SIZEOF_HASH);
                HashBytes subjectHash =  new HashBytes(result.getRow(), HashBytes.SIZEOF_HASH, HashBytes.SIZEOF_HASH);
                HashBytes predicateHash =  new HashBytes(result.getRow(), 2 * HashBytes.SIZEOF_HASH, HashBytes.SIZEOF_HASH);
                HashBytes objectHash =  new HashBytes(result.getRow(), 3 * HashBytes.SIZEOF_HASH, HashBytes.SIZEOF_HASH);

                byte[][] keys = new byte[6][];

                keys[0] = add(partitionHash, subjectHash, predicateHash, objectHash);
                keys[1] = add(partitionHash, predicateHash, objectHash, subjectHash);
                keys[2] = add(partitionHash, objectHash, subjectHash, predicateHash);
                keys[3] = add(subjectHash, predicateHash, objectHash, partitionHash);
                keys[4] = add(predicateHash, objectHash, subjectHash, partitionHash);
                keys[5] = add(objectHash, subjectHash, predicateHash, partitionHash);

                int i = 0;
                for(Map.Entry<HTableInterface, List<Delete>> e : deletes.entrySet())
                {
                    e.getValue().add(new Delete(keys[i]));
                    i++;
                }
            }

        }
        catch (IOException e)
        {
            log.error("", e);
        }
        finally
        {
            for(Map.Entry<HTableInterface, List<Delete>> e : deletes.entrySet())
            {
                try
                {
                    e.getKey().delete(e.getValue());
                    e.getKey().flushCommits();
                    e.getKey().close();
                }
                catch (IOException e1)
                {
                    log.error("", e1);
                }
            }
        }
    }

    private byte[] add(final HashBytes a, final HashBytes b, final HashBytes c, final HashBytes d)
    {
        return add(a.getBytes(), b.getBytes(), c.getBytes(), d.getBytes());
    }

    private byte[] add(final byte [] a, final byte [] b, final byte [] c, final byte [] d) {
        byte [] result = new byte[a.length + b.length + c.length + d.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length + b.length, c.length);
        System.arraycopy(d, 0, result, a.length + b.length + c.length, d.length);
        return result;
    }

    @Override
    public void truncate(String partition)
    {
        // TODO call MR job
        throw new RuntimeException("Not Yet Implemented");
    }

    @Override
    public String getNamedGraph()
    {
        return "default";
    }
}
