package com.daedafusion.knowledge.query;

import com.daedafusion.knowledge.query.exceptions.ServiceErrorException;
import com.daedafusion.knowledge.query.exceptions.UnauthorizedException;
import com.daedafusion.knowledge.trinity.Query;
import com.daedafusion.sparql.SparqlResults;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by mphilpot on 10/1/14.
 */
public class QueryClient implements Closeable
{
    private static final Logger log = Logger.getLogger(QueryClient.class);

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

    public QueryClient()
    {
        this(null, null);
    }

    public QueryClient(String url)
    {
        this(url, null);
    }

    public QueryClient(String url, CloseableHttpClient client)
    {
        this.client = client;
        baseUrl = URI.create(url);
        mapper = new ObjectMapper();

        if(client == null)
        {
            this.client = HttpClients.createSystem();
        }
    }

    public String ping() throws URISyntaxException, ServiceErrorException
    {
        URIBuilder uriBuilder = new URIBuilder(baseUrl).setPath("/health/ping");

        URI uri = uriBuilder.build();

        HttpGet get = new HttpGet(uri);

        try (CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return EntityUtils.toString(response.getEntity());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public SparqlResults sparqlQuery(Query query) throws URISyntaxException, ServiceErrorException, UnauthorizedException
    {
        URIBuilder uriBuilder = new URIBuilder(baseUrl).setPath("/query");

        URI uri = uriBuilder.build();

        HttpPost post = new HttpPost(uri);

        post.addHeader(ACCEPT, "application/sparql-results+json");
        post.addHeader(AUTH, getAuthToken());

        try
        {
            post.setEntity(new StringEntity(mapper.writeValueAsString(query), ContentType.APPLICATION_JSON));
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalArgumentException("Invalid query", e);
        }

        try (CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), SparqlResults.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public SparqlResults knowledgeQuery(String domain,
                                        Query query) throws URISyntaxException, ServiceErrorException, UnauthorizedException
    {
        URIBuilder uriBuilder = new URIBuilder(baseUrl).setPath(String.format("/query/%s", domain));

        URI uri = uriBuilder.build();

        HttpPost post = new HttpPost(uri);

        post.addHeader(ACCEPT, "application/sparql-results+json");
        post.addHeader(AUTH, getAuthToken());

        try
        {
            post.setEntity(new StringEntity(mapper.writeValueAsString(query), ContentType.APPLICATION_JSON));
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalArgumentException("Invalid query", e);
        }

        try (CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), SparqlResults.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public SparqlResults spiderQuery(Query query, Integer depth) throws UnauthorizedException, ServiceErrorException, URISyntaxException
    {
        URIBuilder uriBuilder = new URIBuilder(baseUrl).setPath("/query/spider");

        if(depth != null)
        {
            uriBuilder.addParameter("depth", depth.toString());
        }

        URI uri = uriBuilder.build();

        HttpPost post = new HttpPost(uri);

        post.addHeader(ACCEPT, "application/sparql-results+json");
        post.addHeader(AUTH, getAuthToken());

        try
        {
            post.setEntity(new StringEntity(mapper.writeValueAsString(query), ContentType.APPLICATION_JSON));
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalArgumentException("Invalid query", e);
        }

        try (CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), SparqlResults.class);
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
