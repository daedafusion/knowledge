package com.daedafusion.knowledge.update;

import com.daedafusion.knowledge.update.exceptions.ServiceErrorException;
import com.daedafusion.knowledge.update.exceptions.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mphilpot on 2/25/16.
 */
public class UpdateClient implements Closeable
{
    private static final Logger log = Logger.getLogger(UpdateClient.class);

    protected static final String ACCEPT = "accept";
    protected static final String CONTENT = "content-type";
    protected static final String AUTH = "authorization";

    protected static final String TEXT_XML = "text/xml";
    protected static final String TEXT_PLAIN = "text/plain";
    protected static final String APPLICATION_JSON = "application/json";

    private ObjectMapper mapper;
    private URI baseUrl;
    private CloseableHttpClient client;
    private String authToken;

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
        this.client = client;
        baseUrl = URI.create(url);
        mapper = new ObjectMapper();

        if(client == null)
        {
            this.client = HttpClients.createSystem();
        }
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

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    @Override
    public void close() throws IOException
    {
        if(client != null)
        {
            client.close();
        }
    }
}
