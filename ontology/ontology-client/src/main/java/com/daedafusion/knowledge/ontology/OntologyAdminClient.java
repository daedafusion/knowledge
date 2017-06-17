package com.daedafusion.knowledge.ontology;

import com.daedafusion.knowledge.ontology.exceptions.NotFoundException;
import com.daedafusion.knowledge.ontology.exceptions.ServiceErrorException;
import com.daedafusion.knowledge.ontology.exceptions.UnauthorizedException;
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

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by mphilpot on 8/27/14.
 */
public class OntologyAdminClient implements Closeable
{
    private static final Logger log = Logger.getLogger(OntologyAdminClient.class);

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

    public OntologyAdminClient()
    {
        this(null, null);
    }

    public OntologyAdminClient(String url)
    {
        this(url, null);
    }

    public OntologyAdminClient(String url, CloseableHttpClient client)
    {
        this.client = client;
        baseUrl = URI.create(url);
        mapper = new ObjectMapper();
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

    public List<OntologyMeta> getOntologyMeta() throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URIBuilder builder = new URIBuilder(baseUrl).setPath(String.format("/admin/ontologies/meta"));

        URI uri = builder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, APPLICATION_JSON);
//        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatusNFE(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<OntologyMeta>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }

    }

    public List<DomainAssignment> getDomainAssignments() throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URIBuilder builder = new URIBuilder(baseUrl).setPath(String.format("/admin/assignments"));

        URI uri = builder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, APPLICATION_JSON);
//        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<DomainAssignment>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public DomainAssignment getDomainAssignment(String domain) throws URISyntaxException, UnauthorizedException, NotFoundException, ServiceErrorException
    {
        URIBuilder builder = new URIBuilder(baseUrl).setPath(String.format("/admin/assignments/%s", domain));

        URI uri = builder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, APPLICATION_JSON);
//        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatusNFE(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), DomainAssignment.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public void deleteDomainAssignment(String domain) throws URISyntaxException, UnauthorizedException, NotFoundException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/assignments/%s", domain)).build();

        HttpDelete delete = new HttpDelete(uri);

//        delete.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(delete))
        {
            throwForStatusNFE(response.getStatusLine());

            EntityUtils.consumeQuietly(response.getEntity());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public DomainAssignment saveDomainAssignment(DomainAssignment assignment) throws IOException, UnauthorizedException, ServiceErrorException, URISyntaxException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/assignments")).build();

        HttpPost post = new HttpPost(uri);

//        post.addHeader(AUTH, getAuthToken());

        post.setEntity(new StringEntity(mapper.writeValueAsString(assignment), ContentType.APPLICATION_JSON));

        try(CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), DomainAssignment.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public void updateDomainAssignment(DomainAssignment assignment) throws IOException, URISyntaxException, UnauthorizedException, NotFoundException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/assignments/%s", assignment.getUuid())).build();

        HttpPut put = new HttpPut(uri);

//        put.addHeader(AUTH, getAuthToken());

        put.setEntity(new StringEntity(mapper.writeValueAsString(assignment), ContentType.APPLICATION_JSON));

        try(CloseableHttpResponse response = client.execute(put))
        {
            throwForStatusNFE(response.getStatusLine());

            EntityUtils.consumeQuietly(response.getEntity());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public List<OntologySet> getOntologySets() throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URIBuilder builder = new URIBuilder(baseUrl).setPath(String.format("/admin/set"));

        URI uri = builder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, APPLICATION_JSON);
//        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<OntologySet>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public OntologySet saveOntologySet(OntologySet oSet) throws IOException, UnauthorizedException, ServiceErrorException, URISyntaxException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/set")).build();

        HttpPost post = new HttpPost(uri);

//        post.addHeader(AUTH, getAuthToken());

        post.setEntity(new StringEntity(mapper.writeValueAsString(oSet), ContentType.APPLICATION_JSON));

        try(CloseableHttpResponse response = client.execute(post))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), OntologySet.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public void updateOntologySet(OntologySet oSet) throws IOException, URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/set/%s", oSet.getUuid())).build();

        HttpPut put = new HttpPut(uri);

//        put.addHeader(AUTH, getAuthToken());

        put.setEntity(new StringEntity(mapper.writeValueAsString(oSet), ContentType.APPLICATION_JSON));

        try(CloseableHttpResponse response = client.execute(put))
        {
            throwForStatusNFE(response.getStatusLine());

            EntityUtils.consumeQuietly(response.getEntity());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public void removeOntologySet(String uuid) throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/set/%s", uuid)).build();

        HttpDelete delete = new HttpDelete(uri);

//        delete.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(delete))
        {
            throwForStatusNFE(response.getStatusLine());

            EntityUtils.consumeQuietly(response.getEntity());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public OntologyMeta uploadOntologyRDF(String file) throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/ontology")).build();

        HttpPost post = new HttpPost(uri);

        post.addHeader(ACCEPT, APPLICATION_JSON);
//        post.addHeader(AUTH, getAuthToken());

        post.setEntity(new StringEntity(file, ContentType.TEXT_XML));

        try(CloseableHttpResponse response = client.execute(post))
        {
            throwForStatusNFE(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), OntologyMeta.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public OntologyMeta uploadOntologyNT(String file) throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/ontology")).build();

        HttpPost post = new HttpPost(uri);

        post.addHeader(ACCEPT, APPLICATION_JSON);
//        post.addHeader(AUTH, getAuthToken());

        post.setEntity(new StringEntity(file, ContentType.TEXT_PLAIN));

        try(CloseableHttpResponse response = client.execute(post))
        {
            throwForStatusNFE(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), OntologyMeta.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public void deleteOntology(String uuid) throws URISyntaxException, UnauthorizedException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/admin/ontology/%s", uuid)).build();

        HttpDelete delete = new HttpDelete(uri);

//        delete.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(delete))
        {
            throwForStatusNFE(response.getStatusLine());

            EntityUtils.consumeQuietly(response.getEntity());
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
                throw new NotFoundException("Ontology not found");
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
