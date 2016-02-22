package com.daedafusion.knowledge.query;

import com.daedafusion.client.AbstractClient;
import com.daedafusion.client.exceptions.ServiceErrorException;
import com.daedafusion.client.exceptions.UnauthorizedException;
import com.daedafusion.knowledge.query.instance.ClassInstance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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

/**
 * Created by mphilpot on 10/1/14.
 */
public class EditorClient extends AbstractClient
{
    private static final Logger log = Logger.getLogger(EditorClient.class);

    private ObjectMapper mapper;

    public EditorClient()
    {
        this(null, null);
    }

    public EditorClient(String url)
    {
        this(url, null);
    }

    public EditorClient(String url, CloseableHttpClient client)
    {
        super("query", url, client);
        mapper = new ObjectMapper();
    }

    public ClassEditor getDefinition(String rdfType) throws URISyntaxException, ServiceErrorException, UnauthorizedException
    {
        URIBuilder uriBuilder = new URIBuilder(baseUrl).setPath(String.format("/editor/%s", Base64.encodeBase64URLSafeString(rdfType.getBytes())));

        URI uri = uriBuilder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, APPLICATION_JSON);
        get.addHeader(AUTH, getAuthToken());

        try (CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), ClassEditor.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public ClassEditor getInstance(String rdfType,
                                   String instanceUri,
                                   List<String> partitions) throws URISyntaxException, ServiceErrorException, UnauthorizedException
    {
        URIBuilder uriBuilder = new URIBuilder(baseUrl).setPath(String.format("/editor/%s/%s",
                Base64.encodeBase64URLSafeString(rdfType.getBytes()),
                Base64.encodeBase64URLSafeString(instanceUri.getBytes())));

        for(String p : partitions)
        {
            uriBuilder.addParameter("partition", p);
        }

        URI uri = uriBuilder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, APPLICATION_JSON);
        get.addHeader(AUTH, getAuthToken());

        try (CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), ClassEditor.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public ClassInstance newInstance(ClassInstance instance, String targetPartition, String domain) throws URISyntaxException, ServiceErrorException, UnauthorizedException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/editor/%s", targetPartition)).build();

        HttpPost post = new HttpPost(uri);

        post.addHeader(ACCEPT, APPLICATION_JSON);
        post.addHeader(AUTH, getAuthToken());

        if(domain != null)
        {
            post.addHeader("domain", domain);
        }

        try
        {
            post.setEntity(new StringEntity(mapper.writeValueAsString(instance), ContentType.APPLICATION_JSON));
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalArgumentException("Invalid instance", e);
        }

        try (CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), ClassInstance.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public ClassInstance updateInstance(ClassInstance instance, String domain) throws URISyntaxException, ServiceErrorException, UnauthorizedException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/editor")).build();

        HttpPut put = new HttpPut(uri);

        put.addHeader(ACCEPT, APPLICATION_JSON);
        put.addHeader(AUTH, getAuthToken());

        if(domain != null)
        {
            put.addHeader("domain", domain);
        }

        try
        {
            put.setEntity(new StringEntity(mapper.writeValueAsString(instance), ContentType.APPLICATION_JSON));
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalArgumentException("Invalid instance", e);
        }

        try (CloseableHttpResponse response = client.execute(put))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), ClassInstance.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    private void throwForStatus(StatusLine statusLine) throws ServiceErrorException, UnauthorizedException
    {
        if(statusLine.getStatusCode() >= 400)
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
