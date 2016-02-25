package com.daedafusion.knowledge.trinity.triples.update;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by mphilpot on 7/9/14.
 */
public class TripleWriter extends AbstractWriter
{
    private static final Logger log = Logger.getLogger(TripleWriter.class);

    protected TripleWriter()
    {

    }

    public static TripleWriter getInstance() throws IOException
    {
        TripleWriter writer = new TripleWriter();
        writer.init();

        return writer;
    }

    /**
     *
     * @param subject Should be an unbracketed URI
     * @param predicate Should be an unbracketed URI
     * @param object Should either be an unbracketed URI or a
     *               quoted literal in the form "foobar" or "foobar"@lang or "foobar"^^xsd:type or "foobar"^^&gt;http://www.w3.org/2001/XMLSchema#string%lt;
     * @param partition
     * @param epoch
     * @param externalSource
     * @param ingestId
     * @throws IOException
     */
    public void write(String subject, String predicate, String object, String partition, Long epoch, String externalSource, String ingestId) throws IOException
    {
        add(partition, subject, predicate, object, epoch, externalSource, ingestId);
    }

}
