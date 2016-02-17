package com.daedafusion.knowledge.trinity.triples.query.strategy;

import com.daedafusion.knowledge.trinity.util.HashBytes;
import com.daedafusion.sparql.Literal;
import com.daedafusion.knowledge.trinity.TripleMeta;
import com.daedafusion.knowledge.trinity.conf.Schema;
import com.daedafusion.knowledge.trinity.triples.query.QueryContext;
import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.NiceIterator;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mphilpot on 9/5/14.
 */
public class ScanIterator extends NiceIterator<Triple>
{
    private static final Logger log = Logger.getLogger(ScanIterator.class);

    /**
     * key => partition
     */
    private final Map<String, ResultScanner> scanners;
    private final Map<String, Iterator<Result>> iters;
    private final KeyParser        keyParser;
    private final QueryContext     context;
    private final Map<String, String> rootKeys64;

    private Iterator<Result> currentIterator;
    private String currentPartition;

    private byte[] previousKey;

    public ScanIterator(Map<String, String> rootKeys64, Map<String, ResultScanner> scanners, KeyParser keyParser, QueryContext context)
    {
        this.scanners = scanners;
        this.iters = new HashMap<>();
        for(Map.Entry<String, ResultScanner> entry : scanners.entrySet())
        {
            this.iters.put(entry.getKey(), entry.getValue().iterator());
        }
        this.keyParser = keyParser;
        this.context = context;
        this.rootKeys64 = rootKeys64;
    }

    @Override
    public boolean hasNext()
    {
        if(iters.isEmpty())
        {
            return false;
        }

        boolean hasNext = false;
        currentIterator = null;
        currentPartition = null;

        for(Iterator<Map.Entry<String, Iterator<Result>>> it = iters.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry<String, Iterator<Result>> entry  = it.next();

            String partition = entry.getKey();
            Iterator<Result> ir = entry.getValue();

            boolean thisIterNext = ir.hasNext();

            if(!thisIterNext)
            {
                it.remove();
                // We've exhausted this stage/partition, but not the entire stage
                if(!context.getCursorMap().containsKey(partition))
                    context.getCursorMap().put(partition, new HashMap<String, String>());

                context.getCursorMap().get(partition).put(rootKeys64.get(partition), "");
            }
            else
            {
                hasNext = true;
                currentIterator = ir;
                currentPartition = partition;
                break;
            }
        }

        if(!hasNext)
        {
            // We've exhausted the stage, remove the crumbs
            for(String p : rootKeys64.keySet())
            {
                if(context.getCursorMap().containsKey(p))
                    context.getCursorMap().get(p).remove(rootKeys64.get(p));
            }
        }


        return hasNext;
    }

    @Override
    public Triple next()
    {
        if(previousKey != null)
        {
            if(!context.getCursorMap().containsKey(currentPartition))
                context.getCursorMap().put(currentPartition, new HashMap<String, String>());

            context.getCursorMap().get(currentPartition).put(rootKeys64.get(currentPartition), Base64.encodeBase64String(previousKey));
        }

        Result result = currentIterator.next();

        previousKey = result.getRow();

//        Long lPartition = keyParser.getPartitionHash(result.getRow());
        HashBytes lSubject = keyParser.getSubjectHash(result.getRow());
        HashBytes lPredicate = keyParser.getPredicateHash(result.getRow());
        HashBytes lObject = keyParser.getObjectHash(result.getRow());

        Triple t = createTriple(result, lSubject, lPredicate, lObject);

        TripleMeta tm = new TripleMeta();
        tm.setPartition(Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_PARTITION)));
        tm.setEpoch(Bytes.toLong(result.getValue(Schema.F_INFO, Schema.Q_EPOCH)));
        tm.setExternalSourceHash(Bytes.toString(result.getValue(Schema.F_INFO, Schema.Q_EXTERNAL_SOURCE)));
        tm.setIngestId(Bytes.toString(result.getValue(Schema.F_INFO, Schema.Q_EXTERNAL_SOURCE)));

        if(!context.getMetadata().containsKey(t.hashCode()))
        {
            context.getMetadata().put(t.hashCode(), new ArrayList<TripleMeta>());
        }
        context.getMetadata().get(t.hashCode()).add(tm);

        return t;
    }

    @Override
    public void close()
    {
        for(ResultScanner scanner : scanners.values())
        {
            scanner.close();
        }
    }

    protected Triple createTriple(Result result, HashBytes subjectHash, HashBytes predicateHash, HashBytes objectHash)
    {
        Node subject, predicate, object;

        if(context.getDictionary().isCached(subjectHash))
        {
            subject = NodeFactory.createURI(context.getDictionary().getResource(subjectHash));
        }
        else if(result.containsColumn(Schema.F_RESOURCE, Schema.Q_SUBJECT))
        {
            String s = Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_SUBJECT));
            context.getDictionary().cacheResource(subjectHash, s);
            subject = NodeFactory.createURI(s);
        }
        else
        {
            subject = NodeFactory.createURI(context.getDictionary().getResource(subjectHash));
        }

        if(context.getDictionary().isCached(predicateHash))
        {
            predicate = NodeFactory.createURI(context.getDictionary().getPredicate(predicateHash));
        }
        else if(result.containsColumn(Schema.F_RESOURCE, Schema.Q_PREDICATE))
        {
            String p = Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_PREDICATE));
            context.getDictionary().cachePredicate(predicateHash, p);
            predicate = NodeFactory.createURI(p);
        }
        else
        {
            predicate = NodeFactory.createURI(context.getDictionary().getPredicate(predicateHash));
        }

        String objResource = null;
        String objLiteral = null;
        String objDatatype = null;
        String objLang = null;

        if(context.getDictionary().isCached(objectHash))
        {
            if(context.getDictionary().isCachedLiteral(objectHash))
            {
                Literal lit = context.getDictionary().getLiteral(objectHash);
                objLiteral = lit.value;
                objDatatype = lit.type;
                objLang = lit.lang;
            }
            else
            {
                objResource = context.getDictionary().getResource(objectHash);
            }
        }
        else if(result.containsColumn(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL_DATATYPE))
        {
            objLiteral = Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL));
            objDatatype = Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL_DATATYPE));
            Literal lit = new Literal();
            lit.value = objLiteral;
            lit.type = objDatatype;
            context.getDictionary().cacheLiteral(objectHash, lit);
        }
        else if(result.containsColumn(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL_LANG))
        {
            objLiteral = Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL));
            objLang = Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL_LANG));
            Literal lit = new Literal();
            lit.value = objLiteral;
            lit.lang = objLang;
            context.getDictionary().cacheLiteral(objectHash, lit);
        }
        else if(result.containsColumn(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL))
        {
            // Plain literal
            objLiteral = Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_OBJECT_LITERAL));
            Literal lit = new Literal();
            lit.value = objLiteral;
            context.getDictionary().cacheLiteral(objectHash, lit);
        }
        else if(result.containsColumn(Schema.F_RESOURCE, Schema.Q_OBJECT))
        {
            objResource = Bytes.toString(result.getValue(Schema.F_RESOURCE, Schema.Q_OBJECT));
            context.getDictionary().cacheResource(objectHash, objResource);
        }
        else
        {
            // We can retrieve resources & literals as literals and inspect to determine
            Literal lit = context.getDictionary().getLiteral(objectHash);

            if(lit.type == null && lit.lang == null)
            {
                if(lit.value.startsWith("<"))
                {
                    // Resource
                    objResource = lit.value;
                    context.getDictionary().cacheResource(objectHash, objResource);
                }
                else
                {
                    // Plain Literal
                    objLiteral = lit.value;
                    context.getDictionary().cacheLiteral(objectHash, lit);
                }
            }
            else
            {
                // Typed or lang literal
                objLiteral = lit.value;
                objDatatype = lit.type;
                objLang = lit.lang;
                context.getDictionary().cacheLiteral(objectHash, lit);
            }
        }

        if (objResource != null)
        {
            object = NodeFactory.createURI(objResource);
        }
        else if (objDatatype == null && objLang == null)
        {
            // Plain literal
            object = NodeFactory.createLiteral(objLiteral);
        }
        else if (objDatatype != null)
        {
            object = NodeFactory.createLiteral(objLiteral, new BaseDatatype(objDatatype));
        }
        else
        {
            object = NodeFactory.createLiteral(objLiteral, objLang, false);
        }


        return new Triple(subject, predicate, object);
    }
}
