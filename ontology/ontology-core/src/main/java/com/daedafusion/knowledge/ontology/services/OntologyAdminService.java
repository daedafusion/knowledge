package com.daedafusion.knowledge.ontology.services;

import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import com.daedafusion.knowledge.ontology.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.ontology.framework.exceptions.StorageException;
import com.daedafusion.knowledge.ontology.services.exceptions.ServiceException;
import com.daedafusion.knowledge.ontology.services.exceptions.UnauthorizedException;
import com.daedafusion.knowledge.ontology.DomainAssignment;
import com.daedafusion.knowledge.ontology.OntologyMeta;
import com.daedafusion.knowledge.ontology.OntologySet;
import com.daedafusion.knowledge.ontology.framework.OntologyStorage;
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
import java.util.List;

/**
 * Created by mphilpot on 7/14/14.
 */
@Path("admin")
public class OntologyAdminService
{
    private static final Logger log = Logger.getLogger(OntologyAdminService.class);

    @GET
    @Path("ontologies/meta")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OntologyMeta> getOntologyMeta()
    {
        try
        {
            ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

            OntologyStorage storage = framework.getService(OntologyStorage.class);

            Authorization authn = framework.getService(Authorization.class);

            Context context = new DefaultContext();

            if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:meta"), "GET", context))
            {
                return storage.getOntologyMeta();
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
    }

    @GET
    @Path("assignments")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DomainAssignment> getDomainAssignments()
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:assignments"), "GET", context))
        {
            try
            {
                return storage.getDomainAssignments();
            }
            catch (StorageException e)
            {
                log.error("", e);
                throw new ServiceException();
            }
        }
        else
        {
            throw new UnauthorizedException();
        }
    }

    @GET
    @Path("assignments/{domain}")
    @Produces(MediaType.APPLICATION_JSON)
    public DomainAssignment getDomainAssignment(@PathParam("domain") String domain)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();
        context.addContext("domain", domain);

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:assignments"), "GET", context))
        {
            try
            {
                return storage.getDomainAssignment(domain);
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
        else
        {
            throw new UnauthorizedException();
        }
    }

    @DELETE
    @Path("assignments/{domain}")
    public void deleteDomainAssignment(@PathParam("domain") String domain)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();
        context.addContext("domain", domain);

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:assignment"), "DELETE", context))
        {
            try
            {
                storage.removeDomainAssignment(domain);
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
        else
        {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Path("assignments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DomainAssignment saveDomainAssignment(DomainAssignment assignment)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:assignment"), "POST", context))
        {
            try
            {
                return storage.saveDomainAssignment(assignment);
            }
            catch (StorageException e)
            {
                log.error("", e);
                throw new ServiceException();
            }
        }
        else
        {
            throw new UnauthorizedException();
        }
    }

    @PUT
    @Path("assignments/{domain}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateDomainAssignment(@PathParam("domain") String domain,
                                       DomainAssignment assignment)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();
        context.addContext("domain", domain);

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:assignment"), "PUT", context))
        {
            try
            {
                storage.updateDomainAssignment(assignment);
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
        else
        {
            throw new UnauthorizedException();
        }
    }

    @GET
    @Path("set")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OntologySet> getOntologySets()
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:set"), "GET", context))
        {
            try
            {
                return storage.getOntologySets();
            }
            catch (StorageException e)
            {
                log.error("", e);
                throw new ServiceException();
            }
        }
        else
        {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Path("set")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OntologySet saveOntologySet(OntologySet oSet)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:set"), "POST", context))
        {
            try
            {
                return storage.saveOntologySet(oSet);
            }
            catch (StorageException e)
            {
                log.error("", e);
                throw new ServiceException();
            }
        }
        else
        {
            throw new UnauthorizedException();
        }
    }

    @PUT
    @Path("set/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateOntologySet(@PathParam("uuid") String uuid,
                                  OntologySet oSet)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:assignment"), "PUT", context))
        {
            try
            {
                storage.updateOntologySet(oSet);
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
        else
        {
            throw new UnauthorizedException();
        }
    }

    @DELETE
    @Path("set/{uuid}")
    public void removeOntologySet(@PathParam("uuid") String uuid)
    {
        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();

        OntologyStorage storage = framework.getService(OntologyStorage.class);
        Authorization authn = framework.getService(Authorization.class);

        Context context = new DefaultContext();

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology:set"), "DELETE", context))
        {
            try
            {
                storage.removeOntologySet(uuid);
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
        else
        {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Path("ontology")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public OntologyMeta uploadOntologyForDomain(@HeaderParam("content-type") String contentType,
                                                @HeaderParam("x-ontology-set-uuid") String ontologySetUuid,
                                                String ontologyFile)
    {
        try
        {
            ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
            OntologyStorage storage = sf.getService(OntologyStorage.class);
            Authorization authn = sf.getService(Authorization.class);

            Context context = new DefaultContext();

            if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology"), "POST", context))
            {
                String rdfXml;

                if(contentType.contains(MediaType.TEXT_XML))
                {
                    rdfXml = ontologyFile;
                }
                else
                {
                    Model model = ModelFactory.createDefaultModel();

                    ByteArrayInputStream bais = new ByteArrayInputStream(ontologyFile.getBytes(Charsets.UTF_8));

                    model.read(bais, null, "N-TRIPLE");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    model.write(baos, null, "RDF/XML");

                    model.close();

                    rdfXml = baos.toString("UTF-8");
                }

                return storage.saveOntology(rdfXml);
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
            throw new NotFoundException();
        }
    }

    @DELETE
    @Path("ontology/{uuid}")
    public void removeOntology(@PathParam("uuid") String uuid)
    {
        try
        {
            ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
            OntologyStorage storage = sf.getService(OntologyStorage.class);
            Authorization authn = sf.getService(Authorization.class);

            Context context = new DefaultContext();

            if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("ontology"), "DELETE", context))
            {
                storage.removeOntology(uuid);
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
}
