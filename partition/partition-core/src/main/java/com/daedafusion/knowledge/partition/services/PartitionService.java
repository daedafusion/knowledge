package com.daedafusion.knowledge.partition.services;

import com.daedafusion.knowledge.partition.framework.exceptions.ObjectNotFoundException;
import com.daedafusion.knowledge.partition.framework.exceptions.StorageException;
import com.daedafusion.knowledge.partition.services.exceptions.ServiceException;
import com.daedafusion.knowledge.partition.services.exceptions.UnauthorizedException;
import com.daedafusion.sf.ServiceFramework;
import com.daedafusion.sf.ServiceFrameworkFactory;
import com.daedafusion.knowledge.partition.Partition;
import com.daedafusion.knowledge.partition.framework.PartitionStorage;
import com.daedafusion.knowledge.partition.services.util.Validation;
import com.daedafusion.security.authorization.Authorization;
import com.daedafusion.security.bindings.SubjectUtil;
import com.daedafusion.security.common.Context;
import com.daedafusion.security.common.Identity;
import com.daedafusion.security.common.impl.DefaultContext;
import com.daedafusion.security.identity.IdentityStore;
import com.daedafusion.security.identity.SubjectInspector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 7/14/14.
 */
@Path("partition")
public class PartitionService
{
    private static final Logger log = Logger.getLogger(PartitionService.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Partition createPartition(Partition partition)
    {

        //Validation.validateRootPartition(partition);

        ServiceFramework framework = ServiceFrameworkFactory.getInstance().getFramework();
        PartitionStorage storage =  framework.getService(PartitionStorage.class);
        Authorization authn =  framework.getService(Authorization.class);

        Context context = new DefaultContext();

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("partition"), "POST", context))
        {
            Validation.validateHash(partition);
            try
            {
                return storage.createPartition(partition);
            }
            catch (StorageException e)
            {
                log.error("", e);
                throw new ServiceException(e);
            }
        }
        else
        {
            throw new UnauthorizedException();
        }

    }

    @DELETE
    @Path("{uuid}")
    public void deletePartition(@PathParam("uuid") String uuid)
    {
        try
        {
            // TODO create validate routine

            ObjectMapper mapper = new ObjectMapper();

            ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
            PartitionStorage storage = sf.getService(PartitionStorage.class);
            Authorization authn = sf.getService(Authorization.class);

            Partition partition = storage.getPartition(uuid);

            Context context = new DefaultContext();
            context.addContext("admin", mapper.writeValueAsString(partition.getAdmin()));

            if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("partition"), "DELETE", context))
            {
                // TODO should truncate the partition

                storage.deletePartition(uuid);
            }
            else
            {
                throw new UnauthorizedException();
            }
        }
        catch (JsonProcessingException | StorageException e)
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
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Partition getPartition(@PathParam("uuid") String uuid)
    {
        try
        {
            // TODO create validate routine

            ObjectMapper mapper = new ObjectMapper();

            ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
            PartitionStorage storage = sf.getService(PartitionStorage.class);
            Authorization authn = sf.getService(Authorization.class);

            Partition partition = storage.getPartition(uuid);

            Context context = new DefaultContext();
            context.addContext("admin", mapper.writeValueAsString(partition.getAdmin()));
            context.addContext("read", mapper.writeValueAsString(partition.getAdmin()));
            context.addContext("write", mapper.writeValueAsString(partition.getAdmin()));

            if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("partition"), "GET", context))
            {
                return partition;
            }
            else
            {
                throw new UnauthorizedException();
            }
        }
        catch (JsonProcessingException | StorageException e)
        {
            log.error("", e);
            throw new ServiceException(e);
        }
        catch (ObjectNotFoundException e)
        {
            log.error("", e);
            throw new NotFoundException();
        }
    }

    @PUT
    @Path("{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updatePartition(@PathParam("uuid") String uuid, Partition partition)
    {
        try
        {
            // TODO create validate routine

            ObjectMapper mapper = new ObjectMapper();

            ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
            PartitionStorage storage = sf.getService(PartitionStorage.class);
            Authorization authn = sf.getService(Authorization.class);

            Partition oldPartition = storage.getPartition(uuid);

            Context context = new DefaultContext();
            context.addContext("admin", mapper.writeValueAsString(oldPartition.getAdmin()));

            if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("partition"), "PUT", context))
            {
                Validation.validateHash(partition);
                storage.updatePartition(partition);
            }
            else
            {
                throw new UnauthorizedException();
            }
        }
        catch (JsonProcessingException | StorageException e)
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
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Partition> getPartitions(@QueryParam("tag") Set<String> tags,
                                         @QueryParam("system") Set<String> systemTags)
    {
        ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
        PartitionStorage storage = sf.getService(PartitionStorage.class);
        Authorization authn = sf.getService(Authorization.class);

        Context context = new DefaultContext();

        if(authn.isAuthorized(SubjectUtil.getSubject(), URI.create("partition:all"), "GET", context))
        {
            try
            {
                if (tags == null || tags.isEmpty())
                {
                    return storage.getAllPartitions();
                }
                else
                {
                    List<Partition> list = storage.getAllPartitions();

                    Iterator<Partition> iter = list.iterator();

                    while (iter.hasNext())
                    {
                        Partition p = iter.next();

                        if (!CollectionUtils.containsAny(p.getTags(), tags))
                        {
                            iter.remove();
                        }
                        else if (!CollectionUtils.containsAny(p.getSystemTags(), systemTags))
                        {
                            iter.remove();
                        }
                    }

                    return list;
                }
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
    @Path("user/{username}/read")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Partition> getReadablePartitions(@PathParam("username") String username,
                                                 @QueryParam("tag") Set<String> tags,
                                                 @QueryParam("system") Set<String> systemTags)
    {
        // TODO create validate routine

        ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
        PartitionStorage storage = sf.getService(PartitionStorage.class);
        Authorization authn = sf.getService(Authorization.class);
        SubjectInspector inspector = sf.getService(SubjectInspector.class);
        IdentityStore store = sf.getService(IdentityStore.class);

        Context context = new DefaultContext();

        Identity identity = null;

        if(!inspector.getFullyQualifiedUsername(SubjectUtil.getSubject()).equals(username))
        {
            if(!authn.isAuthorized(SubjectUtil.getSubject(), URI.create("partition:delegation"), "GET", context))
            {
                throw new UnauthorizedException();
            }

            try
            {
                identity = store.getIdentity(SubjectUtil.getSubject(), username, null);
            }
            catch (com.daedafusion.security.exceptions.UnauthorizedException e)
            {
                throw new UnauthorizedException();
            }
        }
        else
        {
            identity = store.getIdentity(SubjectUtil.getSubject());
        }

        Set<String> capabilities = identity.getAttributes().get(Identity.ATTR_CAPABILITIES);

        try
        {
            if (tags.isEmpty() && systemTags.isEmpty())
            {
                return storage.getPartitions(PartitionStorage.Action.Read,
                        capabilities != null ? capabilities : new HashSet<>());
            }
            else
            {
                return storage.getPartitions(PartitionStorage.Action.Read,
                        capabilities != null ? capabilities : new HashSet<>(),
                        tags, systemTags);
            }
        }
        catch (StorageException e)
        {
            log.error("", e);
            throw new ServiceException();
        }
    }

    @GET
    @Path("user/{username}/write")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Partition> getWritablePartitions(@PathParam("username") String username,
                                                 @QueryParam("tag") Set<String> tags,
                                                 @QueryParam("system") Set<String> systemTags)
    {
        // TODO create validate routine

        ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
        PartitionStorage storage = sf.getService(PartitionStorage.class);
        Authorization authn = sf.getService(Authorization.class);
        SubjectInspector inspector = sf.getService(SubjectInspector.class);
        IdentityStore store = sf.getService(IdentityStore.class);

        Context context = new DefaultContext();

        Identity identity = null;

        if(!inspector.getFullyQualifiedUsername(SubjectUtil.getSubject()).equals(username))
        {
            if(!authn.isAuthorized(SubjectUtil.getSubject(), URI.create("partition:delegation"), "GET", context))
            {
                throw new UnauthorizedException();
            }

            try
            {
                identity = store.getIdentity(SubjectUtil.getSubject(), username, null);
            }
            catch (com.daedafusion.security.exceptions.UnauthorizedException e)
            {
                throw new UnauthorizedException();
            }
        }
        else
        {
            identity = store.getIdentity(SubjectUtil.getSubject());
        }

        Set<String> capabilities = identity.getAttributes().get(Identity.ATTR_CAPABILITIES);

        try
        {
            if (tags.isEmpty() && systemTags.isEmpty())
            {
                return storage.getPartitions(PartitionStorage.Action.Write,
                        capabilities != null ? capabilities : new HashSet<>());
            }
            else
            {
                return storage.getPartitions(PartitionStorage.Action.Write,
                        capabilities != null ? capabilities : new HashSet<>(),
                        tags, systemTags);
            }
        }
        catch (StorageException e)
        {
            log.error("", e);
            throw new ServiceException();
        }
    }

    @GET
    @Path("user/{username}/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Partition> getAdminablePartitions(@PathParam("username") String username,
                                                  @QueryParam("tag") Set<String> tags,
                                                  @QueryParam("system") Set<String> systemTags)
    {
        // TODO create validate routine

        ServiceFramework sf = ServiceFrameworkFactory.getInstance().getFramework();
        PartitionStorage storage = sf.getService(PartitionStorage.class);
        Authorization authn = sf.getService(Authorization.class);
        SubjectInspector inspector = sf.getService(SubjectInspector.class);
        IdentityStore store = sf.getService(IdentityStore.class);

        Context context = new DefaultContext();

        Identity identity;

        if(!inspector.getFullyQualifiedUsername(SubjectUtil.getSubject()).equals(username))
        {
            if(!authn.isAuthorized(SubjectUtil.getSubject(), URI.create("partition:delegation"), "GET", context))
            {
                throw new UnauthorizedException();
            }

            try
            {
                identity = store.getIdentity(SubjectUtil.getSubject(), username, null);
            }
            catch (com.daedafusion.security.exceptions.UnauthorizedException e)
            {
                throw new UnauthorizedException();
            }
        }
        else
        {
            identity = store.getIdentity(SubjectUtil.getSubject());
        }

        Set<String> capabilities = identity.getAttributes().get(Identity.ATTR_CAPABILITIES);

        try
        {
            if (tags.isEmpty() && systemTags.isEmpty())
            {
                return storage.getPartitions(PartitionStorage.Action.Admin,
                        capabilities != null ? capabilities : new HashSet<>());
            }
            else
            {
                return storage.getPartitions(PartitionStorage.Action.Admin,
                        capabilities != null ? capabilities : new HashSet<>(),
                        tags, systemTags);
            }
        }
        catch (StorageException e)
        {
            log.error("", e);
            throw new ServiceException();
        }
    }
}
