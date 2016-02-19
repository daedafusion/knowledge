package com.daedafusion.knowledge.partition;

import com.daedafusion.client.AbstractClient;
import com.daedafusion.client.exceptions.NotFoundException;
import com.daedafusion.client.exceptions.ServiceErrorException;
import com.daedafusion.client.exceptions.UnauthorizedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

/**
 * Created by mphilpot on 10/6/14.
 */
public class PartitionClient extends AbstractClient
{
    private static final Logger log = Logger.getLogger(PartitionClient.class);

    private ObjectMapper mapper;

    public PartitionClient()
    {
        this(null, null);
    }

    public PartitionClient(String url)
    {
        this(url, null);
    }

    public PartitionClient(String url, CloseableHttpClient client)
    {
        super("partition", url, client);
        mapper = new ObjectMapper();
    }

    public Partition createPartition(Partition partition)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException, IOException
    {
        URI uri = new URIBuilder(baseUrl).setPath("/partition").build();

        HttpPost post = new HttpPost(uri);

        post.addHeader(AUTH, getAuthToken());
        post.addHeader(ACCEPT, APPLICATION_JSON);

        post.setEntity(new StringEntity(mapper.writeValueAsString(partition), ContentType.APPLICATION_JSON));

        try(CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), Partition.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public void deletePartition(String uuid)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException, NotFoundException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/partition/%s", uuid)).build();

        HttpDelete delete = new HttpDelete(uri);

        delete.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(delete))
        {
            throwForStatusNFE(response.getStatusLine());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public Partition getPartition(String uuid)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException, NotFoundException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/partition/%s", uuid)).build();

        HttpGet get  = new HttpGet(uri);

        get.addHeader(AUTH, getAuthToken());
        get.addHeader(ACCEPT, APPLICATION_JSON);

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatusNFE(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), Partition.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public void updatePartition(Partition partition)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException, IOException, NotFoundException
    {
        URI uri = new URIBuilder(baseUrl).setPath("/partition").build();

        HttpPut put = new HttpPut(uri);

        put.addHeader(AUTH, getAuthToken());

        put.setEntity(new StringEntity(mapper.writeValueAsString(partition), ContentType.APPLICATION_JSON));

        try(CloseableHttpResponse response = client.execute(put))
        {
            throwForStatusNFE(response.getStatusLine());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public List<Partition> getDomainPartitions(String domain)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/partition/domain/%s", domain)).build();

        HttpGet get  = new HttpGet(uri);

        get.addHeader(AUTH, getAuthToken());
        get.addHeader(ACCEPT, APPLICATION_JSON);

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<Partition>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public List<Partition> getPartitions(Set<String> tags)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URIBuilder uriB = new URIBuilder(baseUrl).setPath("/partition/all");

        if(tags != null)
        {
            for(String t : tags)
            {
                uriB.addParameter("tag", t);
            }
        }

        URI uri = uriB.build();

        HttpGet get  = new HttpGet(uri);

        get.addHeader(AUTH, getAuthToken());
        get.addHeader(ACCEPT, APPLICATION_JSON);

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<Partition>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public List<Partition> getReadablePartitions(String username, Set<String> tags, Set<String> systemTags)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URIBuilder uriB = new URIBuilder(baseUrl).setPath(String.format("/partition/user/%s/read", username));

        if(tags != null)
        {
            for(String t :tags)
            {
                uriB.addParameter("tag", t);
            }
        }
        if(systemTags != null)
        {
            for(String t : systemTags)
            {
                uriB.addParameter("system", t);
            }
        }

        URI uri = uriB.build();

        HttpGet get  = new HttpGet(uri);

        get.addHeader(AUTH, getAuthToken());
        get.addHeader(ACCEPT, APPLICATION_JSON);

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<Partition>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public List<Partition> getWritablePartitions(String username, Set<String> tags, Set<String> systemTags)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URIBuilder uriB = new URIBuilder(baseUrl).setPath(String.format("/partition/user/%s/write", username));

        if(tags != null)
        {
            for(String t :tags)
            {
                uriB.addParameter("tag", t);
            }
        }
        if(systemTags != null)
        {
            for(String t : systemTags)
            {
                uriB.addParameter("system", t);
            }
        }

        URI uri = uriB.build();

        HttpGet get  = new HttpGet(uri);

        get.addHeader(AUTH, getAuthToken());
        get.addHeader(ACCEPT, APPLICATION_JSON);

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<Partition>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public List<Partition> getAdminablePartitions(String username, Set<String> tags, Set<String> systemTags)
            throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URIBuilder uriB = new URIBuilder(baseUrl).setPath(String.format("/partition/user/%s/admin", username));

        if(tags != null)
        {
            for(String t :tags)
            {
                uriB.addParameter("tag", t);
            }
        }
        if(systemTags != null)
        {
            for(String t : systemTags)
            {
                uriB.addParameter("system", t);
            }
        }

        URI uri = uriB.build();

        HttpGet get  = new HttpGet(uri);

        get.addHeader(AUTH, getAuthToken());
        get.addHeader(ACCEPT, APPLICATION_JSON);

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<Partition>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    private void throwForStatusNFE(StatusLine statusLine) throws NotFoundException, ServiceErrorException, UnauthorizedException
    {
        if(statusLine.getStatusCode() >= 300)
        {
            if(statusLine.getStatusCode() == 404)
            {
                throw new NotFoundException("Partition not found");
            }
            else if(statusLine.getStatusCode() == 401)
            {
                throw new UnauthorizedException(statusLine.getReasonPhrase());
            }
            else
            {
                throw new ServiceErrorException(statusLine.getReasonPhrase());
            }
        }
    }

    private void throwForStatus(StatusLine statusLine) throws ServiceErrorException, UnauthorizedException
    {
        if(statusLine.getStatusCode() >= 300)
        {
            if(statusLine.getStatusCode() == 401)
            {
                throw new UnauthorizedException(statusLine.getReasonPhrase());
            }
            else
            {
                throw new ServiceErrorException(statusLine.getReasonPhrase());
            }
        }
    }
}
