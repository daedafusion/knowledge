package com.daedafusion.knowledge.update;

import com.daedafusion.client.AbstractClient;
import com.daedafusion.client.exceptions.ServiceErrorException;
import com.daedafusion.client.exceptions.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mphilpot on 2/25/16.
 */
public class UpdateClient extends AbstractClient
{
    private static final Logger log = Logger.getLogger(UpdateClient.class);

    private ObjectMapper mapper;

    public UpdateClient()
    {
        this(null, null);
    }

    public UpdateClient(String url)
    {
        this(url, null);
    }

    public UpdateClient(String url, CloseableHttpClient client)
    {
        super("update", url, client);
        mapper = new ObjectMapper();
    }

    public void async(String nTriples,
                      Long epoch, String partition, String externalSource, String ingestId) throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath("/update/async").build();

        HttpPost post = new HttpPost(uri);

        post.addHeader("x-update-epoch", String.valueOf(epoch));
        post.addHeader("x-update-partition", partition);
        post.addHeader("x-update-source", externalSource);
        post.addHeader("x-update-ingestid", ingestId);
        post.addHeader(AUTH, getAuthToken());

        post.setEntity(new StringEntity(nTriples, ContentType.TEXT_PLAIN));


        try (CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public void sync(String nTriples,
                      Long epoch, String partition, String externalSource, String ingestId) throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath("/update/sync").build();

        HttpPost post = new HttpPost(uri);

        post.addHeader("x-update-epoch", String.valueOf(epoch));
        post.addHeader("x-update-partition", partition);
        post.addHeader("x-update-source", externalSource);
        post.addHeader("x-update-ingestid", ingestId);
        post.addHeader(AUTH, getAuthToken());

        post.setEntity(new StringEntity(nTriples, ContentType.TEXT_PLAIN));


        try (CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());
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
