package com.daedafusion.knowledge.ontology;

import com.daedafusion.client.AbstractClient;
import com.daedafusion.client.exceptions.NotFoundException;
import com.daedafusion.client.exceptions.ServiceErrorException;
import com.daedafusion.client.exceptions.UnauthorizedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mphilpot on 8/27/14.
 */
public class OntologyClient extends AbstractClient
{
    private static final Logger log = Logger.getLogger(OntologyClient.class);

    private ObjectMapper mapper;

    public OntologyClient()
    {
        this(null, null);
    }

    public OntologyClient(String url)
    {
        this(url, null);
    }

    public OntologyClient(String url, CloseableHttpClient client)
    {
        super("ontology", url, client);
        mapper = new ObjectMapper();
    }

    public String getOntologyRDF(String uuid) throws URISyntaxException, ServiceErrorException, NotFoundException, UnauthorizedException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/ontology/%s", uuid)).build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, TEXT_XML);
        get.addHeader(AUTH, getAuthToken());

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

    public String getOntologyNT(String uuid) throws URISyntaxException, UnauthorizedException, NotFoundException, ServiceErrorException
    {
        URI uri = new URIBuilder(baseUrl).setPath(String.format("/ontology/%s", uuid)).build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, TEXT_PLAIN);
        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return EntityUtils.toString(response.getEntity());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public OntologyDescription getOntologyDescription(String domain) throws NotFoundException, ServiceErrorException, UnauthorizedException, URISyntaxException
    {
        return getOntologyDescription(domain, new ArrayList<String>());
    }

    public OntologyDescription getOntologyDescription(String domain, List<String> uuids) throws URISyntaxException, UnauthorizedException, NotFoundException, ServiceErrorException
    {
        URIBuilder builder = new URIBuilder(baseUrl).setPath(String.format("/ontologies/%s", domain));

        for(String uuid : uuids)
        {
            builder.addParameter("uuid", uuid);
        }

        URI uri = builder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, APPLICATION_JSON);
        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), OntologyDescription.class);
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public Map<String, Set<String>> getLabels(String domain, List<String> uuids) throws URISyntaxException, UnauthorizedException, NotFoundException, ServiceErrorException
    {
        URIBuilder builder = new URIBuilder(baseUrl).setPath(String.format("/ontologies/%s/labels", domain));

        for(String uuid : uuids)
        {
            builder.addParameter("uuid", uuid);
        }

        URI uri = builder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, APPLICATION_JSON);
        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Map<String, Set<String>>>(){});
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public String getDomainOntologyRDF(String domain, List<String> uuids) throws URISyntaxException, UnauthorizedException, NotFoundException, ServiceErrorException
    {
        URIBuilder builder = new URIBuilder(baseUrl).setPath(String.format("/ontologies/%s", domain));

        for(String uuid : uuids)
        {
            builder.addParameter("uuid", uuid);
        }

        URI uri = builder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, TEXT_XML);
        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return EntityUtils.toString(response.getEntity());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    public String getDomainOntologyNT(String domain, List<String> uuids) throws URISyntaxException, UnauthorizedException, NotFoundException, ServiceErrorException
    {
        URIBuilder builder = new URIBuilder(baseUrl).setPath(String.format("/ontologies/%s", domain));

        for(String uuid : uuids)
        {
            builder.addParameter("uuid", uuid);
        }

        URI uri = builder.build();

        HttpGet get = new HttpGet(uri);

        get.addHeader(ACCEPT, TEXT_PLAIN);
        get.addHeader(AUTH, getAuthToken());

        try(CloseableHttpResponse response = client.execute(get))
        {
            throwForStatus(response.getStatusLine());

            return EntityUtils.toString(response.getEntity());
        }
        catch (IOException e)
        {
            throw new ServiceErrorException(e.getMessage(), e);
        }
    }

    private void throwForStatus(StatusLine statusLine) throws NotFoundException, ServiceErrorException, UnauthorizedException
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
}
