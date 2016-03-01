package com.daedafusion.knowledge.trinity.triples.update.reified;

import com.daedafusion.knowledge.trinity.triples.update.ModelWriter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mphilpot on 7/9/14.
 */
public class ReifiedModelWriterImpl extends AbstractReifiedWriter implements ModelWriter
{
    private static final Logger log = Logger.getLogger(ReifiedModelWriterImpl.class);

    public ReifiedModelWriterImpl()
    {

    }

    public static ModelWriter getInstance(boolean buffered) throws IOException
    {
        ReifiedModelWriterImpl writer = new ReifiedModelWriterImpl();
        writer.init(buffered);

        return writer;
    }

    @Override
    public void delete(Model model, String partition) throws IOException
    {
        List<T> deletes = new ArrayList<>();

        for(Statement s : model.listStatements().toSet())
        {
            String subject = s.getSubject().toString();
            String predicate = s.getPredicate().toString();
            String object, objectLiteral, objectDatatype, objectLang;

            if(s.getObject().isLiteral())
            {
                objectLiteral = s.getObject().asLiteral().getString(); // no quotes

                deletes.add(new T(subject, predicate, objectLiteral));
            }
            else
            {
                object = s.getObject().asResource().getURI();

                deletes.add(new T(subject, predicate, object));
            }
        }

        delete(partition, deletes);
    }

    /**
     * Write all the triples within the Model to the triple store with the given metadata
     *
     * @param model
     * @param epoch
     * @param externalSource
     * @param ingestId
     */
    @Override
    public void write(Model model, String partition, Long epoch, String externalSource, String ingestId) throws IOException
    {
        for(Statement s : model.listStatements().toSet())
        {
            String subject = s.getSubject().toString();
            String predicate = s.getPredicate().toString();
            String object, objectLiteral, objectDatatype, objectLang;

            if(s.getObject().isLiteral())
            {
                if((s.getObject().asLiteral().getDatatypeURI() == null || s.getObject().asLiteral().getDatatypeURI().equals("")) &&
                        (s.getObject().asLiteral().getLanguage() == null || s.getObject().asLiteral().getLanguage().equals("")))
                {
                    // Simple literal, no datatype or lang construct
                    object = s.getObject().asLiteral().getString();

                    add(partition, subject, predicate, object, object, null, null, epoch, externalSource, ingestId);
                }
                else if(s.getObject().asLiteral().getDatatypeURI() != null && !s.getObject().asLiteral().getDatatypeURI().equals(""))
                {
//                    Data Typed literal -- use the ntriple representation to compute hash
//                    object = String.format("\"%s\"^^<%s>", s.getObject().asLiteral().getString(), s.getObject().asLiteral().getDatatypeURI());
                    object = s.getObject().asLiteral().getString();
                    objectLiteral = s.getObject().asLiteral().getString(); // no quotes
                    objectDatatype = s.getObject().asLiteral().getDatatypeURI(); // no brackets

                    add(partition, subject, predicate, object, objectLiteral, objectDatatype, null, epoch, externalSource, ingestId);
                }
                else if(s.getObject().asLiteral().getLanguage() != null && !s.getObject().asLiteral().getLanguage().equals(""))
                {
                    // String with lang tag
//                    object = String.format("\"%s\"@%s", s.getObject().asLiteral().getString(), s.getObject().asLiteral().getLanguage());
                    object = s.getObject().asLiteral().getString();
                    objectLiteral = s.getObject().asLiteral().getString();
                    objectLang = s.getObject().asLiteral().getLanguage();

                    add(partition, subject, predicate, object, objectLiteral, null, objectLang, epoch, externalSource, ingestId);
                }
                else
                {
//                    object = String.format("\"%s\"^^<%s>", s.getObject().asLiteral().getString(), s.getObject().asLiteral().getDatatypeURI());
                    log.error(String.format("Unknown Literal Combination (Should Not Happen) :: %s", s.getObject().asLiteral().toString()));
                    throw new IOException("Invalid triple literal");
                }
            }
            else
            {
                object = s.getObject().asResource().getURI();

                add(partition, subject, predicate, object, epoch, externalSource, ingestId);
            }


        }
    }

}
