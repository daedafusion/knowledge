package com.daedafusion.knowledge.ontology.services;

import com.daedafusion.knowledge.ontology.DomainAssignment;
import com.daedafusion.knowledge.ontology.OntologyDescription;
import com.daedafusion.cache.Cache;
import com.daedafusion.cache.CacheManager;
import com.daedafusion.knowledge.ontology.OntologyMeta;
import com.daedafusion.knowledge.ontology.OntologySet;
import com.daedafusion.knowledge.ontology.framework.OntologyCompiler;
import com.daedafusion.knowledge.ontology.services.exceptions.ServiceException;
import com.daedafusion.knowledge.ontology.services.exceptions.UnauthorizedException;
import com.daedafusion.sparql.Literal;
import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import com.daedafusion.knowledge.ontology.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.ontology.framework.exceptions.StorageException;
import com.daedafusion.knowledge.ontology.framework.OntologyStorage;
import com.daedafusion.knowledge.ontology.model.OntologyModel;
import com.daedafusion.security.authorization.Authorization;
import com.daedafusion.security.bindings.SubjectUtil;
import com.daedafusion.security.common.Context;
import com.daedafusion.security.common.impl.DefaultContext;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.codec.Charsets;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

/**
 * Created by mphilpot on 7/14/14.
 */
@Path("/")
public class OntologyService
{
    private static final Logger log = Logger.getLogger(OntologyService.class);

    @GET
    @Path("ontologies/{domain}")
    @Produces(MediaType.APPLICATION_JSON)
    public OntologyDescription getOntologyDescription(@PathParam("domain") String domain,
                                                      @QueryParam("uuid") List<String> uuids)
    {
        try
        {
            ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

            OntologyStorage storage = framework.getService(OntologyStorage.class);
            OntologyCompiler compiler = framework.getService(OntologyCompiler.class);

            Authorization authn = framework.getService(Authorization.class);

            Context context = new DefaultContext();
            context.addContext("domain", domain);

            if (authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology"), "GET", context))
            {
                Cache<String, OntologyDescription> ontDescCache = framework.getService(CacheManager.class).getCache("ontology-description-cache");
                String key = String.format("%s-%d", domain, uuids.hashCode());

                if(ontDescCache.contains(key))
                {
                    return ontDescCache.get(key);
                }

                DomainAssignment assignment = storage.getDomainAssignment(domain);

                Model model = ModelFactory.createDefaultModel();

                for(OntologySet set : assignment.getOntologySets())
                {
                    for (OntologyMeta meta : set.getOntologies())
                    {
                        if (uuids.isEmpty() || uuids.contains(meta.getUuid()))
                        {
                            ByteArrayInputStream bais = new ByteArrayInputStream(storage.getOntology(meta.getUuid()).getBytes(Charsets.UTF_8));

                            model.read(bais, null, "RDF/XML");
                        }
                    }
                }

                OntologyModel ontModel = new OntologyModel(model);

                OntologyDescription description = compiler.describeOntologyClosure(ontModel);

                ontDescCache.put(key, description);

                return description;
            }
            else
            {
                throw new UnauthorizedException();
            }
        }
        catch (StorageException e)
        {
            log.error("", e);
            throw new ServiceException();
        }
        catch (ObjectNotFoundException e)
        {
            log.error("", e);
            throw new NotFoundException("Ontology not found");
        }
    }

    @GET
    @Path("ontologies/{domain}/labels")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Set<Literal>> getLabelMap(@PathParam("domain") String domain,
                                                @QueryParam("uuid") List<String> uuids)
    {
        try
        {
            ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

            OntologyStorage storage = framework.getService(OntologyStorage.class);

            Authorization authn = framework.getService(Authorization.class);

            Context context = new DefaultContext();
            context.addContext("domain", domain);

            if (authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology"), "GET", context))
            {
                Cache<String, Map<String, Set<Literal>>> ontLabelCache = framework.getService(CacheManager.class).getCache("ontology-label-cache");
                String key = String.format("%s-%d", domain, uuids.hashCode());

                if(ontLabelCache.contains(key))
                {
                    return ontLabelCache.get(key);
                }

                Map<String, Set<Literal>> result = new HashMap<>();

                DomainAssignment assignment = storage.getDomainAssignment(domain);

                Model model = ModelFactory.createDefaultModel();

                for(OntologySet set : assignment.getOntologySets())
                {
                    set.getOntologies()
                            .stream()
                            .filter(meta -> uuids.isEmpty() || uuids.contains(meta.getUuid()))
                            .forEach(meta -> {
                                ByteArrayInputStream bais = new ByteArrayInputStream(storage.getOntology(meta.getUuid()).getBytes(Charsets.UTF_8));

                                model.read(bais, null, "RDF/XML");
                            });
                }

                OntologyModel ontModel = new OntologyModel(model);

                for(String uri : ontModel.getClasses())
                {
                    result.put(uri, ontModel.getLabels(uri));
                }
                for(String uri : ontModel.getDataProperties())
                {
                    result.put(uri, ontModel.getLabels(uri));
                }
                for(String uri : ontModel.getObjectProperties())
                {
                    result.put(uri, ontModel.getLabels(uri));
                }

                ontLabelCache.put(key, result);

                return result;
            }
            else
            {
                throw new UnauthorizedException();
            }
        }
        catch (StorageException e)
        {
            log.error("", e);
            throw new ServiceException();
        }
        catch (ObjectNotFoundException e)
        {
            log.error("", e);
            throw new NotFoundException();
        }
    }

    @GET
    @Path("ontologies/{domain}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    public String getOntologyForDomain(@HeaderParam("accept") String acceptType,
                                       @PathParam("domain") String domain,
                                       @QueryParam("uuid") List<String> uuids)
    {
        try
        {
            ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

            OntologyStorage storage = framework.getService(OntologyStorage.class);

            Authorization authn = framework.getService(Authorization.class);

            Context context = new DefaultContext();
            context.addContext("domain", domain);

            if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology"), "GET", context))
            {
                DomainAssignment assignment = storage.getDomainAssignment(domain);

                Model model = ModelFactory.createDefaultModel();

                for(OntologySet set : assignment.getOntologySets())
                {
                    set.getOntologies()
                            .stream()
                            .filter(meta -> uuids.isEmpty() || uuids.contains(meta.getUuid()))
                            .forEach(meta -> {
                                ByteArrayInputStream bais = new ByteArrayInputStream(storage.getOntology(meta.getUuid()).getBytes(Charsets.UTF_8));

                                model.read(bais, null, "RDF/XML");
                            });
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if(acceptType.contains(MediaType.TEXT_XML))
                {
                    model.write(baos, null, "RDF/XML");
                }
                else
                {
                    model.write(baos, null, "N-TRIPLE");
                }

                model.close();

                return baos.toString("UTF-8");
            }
            else
            {
                throw new UnauthorizedException();
            }
        }
        catch (StorageException | UnsupportedEncodingException e)
        {
            log.error("", e);
            throw new ServiceException();
        }
        catch (ObjectNotFoundException e)
        {
            log.error("", e);
            throw new NotFoundException("Ontology not found");
        }
    }
}
