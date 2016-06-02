package com.daedafusion.knowledge.trinity.triples.update;

import com.daedafusion.configuration.Configuration;
import com.daedafusion.knowledge.trinity.triples.update.reified.ReifiedModelWriterImpl;
import org.apache.commons.pool2.*;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import java.io.Closeable;

/**
 * Created by mphilpot on 8/18/14.
 */
public class ModelWriterPool extends GenericObjectPool<ModelWriter> implements Closeable
{

    private static final Logger log = Logger.getLogger(ModelWriterPool.class);

    public ModelWriterPool(PooledObjectFactory<ModelWriter> factory)
    {
        super(factory);
        setLifo(false);
        setMaxTotal(Configuration.getInstance().getInteger("modelWriterPool.maxTotal", 10));
    }

    public static class ModelWriterObjectFactory extends BasePooledObjectFactory<ModelWriter>
    {
        private final boolean isReified;
        private final boolean isBuffered;

        public ModelWriterObjectFactory(boolean isBuffered, boolean isReified)
        {
            this.isReified = isReified;
            this.isBuffered = isBuffered;
        }


        @Override
        public void destroyObject(PooledObject<ModelWriter> p) throws Exception
        {
            p.getObject().close();
        }

        @Override
        public ModelWriter create() throws Exception
        {
            ModelWriter mw = null;

            if(isReified)
            {
                ReifiedModelWriterImpl rmw = new ReifiedModelWriterImpl();
                rmw.init(isBuffered);
                mw = rmw;
            }
            else
            {
                ModelWriterImpl mwi = new ModelWriterImpl();
                mwi.init(isBuffered);
                mw = mwi;
            }
            return mw;
        }

        @Override
        public PooledObject<ModelWriter> wrap(ModelWriter modelWriter)
        {
            return new DefaultPooledObject<>(modelWriter);
        }
    }
}
