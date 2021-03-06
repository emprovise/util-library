package com.emprovise.util.commons;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * HTTP Client Utility to call the Server and get the server response.
 *
 */
public class HttpClient {

    private CloseableHttpClient httpclient;
    private HttpContext context;
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public HttpClient() {
        httpclient = HttpClients.createDefault();
        context = new BasicHttpContext();
    }

    public HttpClient(String username, String password) {

        // Create HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        // Then provide the right credentials
        Credentials credentials = new UsernamePasswordCredentials(username, password);
        AuthScope authscope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(authscope, credentials);

        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        httpClientBuilder.addInterceptorFirst(new PreemptiveAuthInterceptor());

        if (isNotBlank(username)) {
            context = new BasicHttpContext();
            context.setAttribute("preemptive-auth", new BasicScheme());
        }

        httpclient = httpClientBuilder.build();
    }

    /**
     * Returns a new instance of {@link HttpClient} by creating an instance of {@link org.apache.http.client.HttpClient} with the
     * specified connection timeout parameters.
     *
     * @param remoteConnectionTimeout
     * 		set connection timeout (how long it takes to connect to remote host).
     * @param remoteSocketTimeout
     * 		set socket timeout (how long it takes to retrieve data from remote host).
     * @param connectionRequestTimeout
     * 		set timeout on how long when request can be considered expired.
     */
    public HttpClient(int remoteConnectionTimeout, int remoteSocketTimeout, int connectionRequestTimeout) {

        super();

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(remoteSocketTimeout)
                .setConnectTimeout(remoteConnectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(10);
        connManager.setValidateAfterInactivity(5000);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(connManager);
        httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
        httpclient = httpClientBuilder.build();
    }

    public HttpClient(String proxyHost, Integer proxyPort, String proxyUser, String proxyPassword) {

        super();

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(proxyHost, proxyPort),
                new UsernamePasswordCredentials(proxyUser, proxyPassword));

        HttpHost proxy = new HttpHost(proxyHost, proxyPort);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .build();

        httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
    }


    public String post(String url, Map<String, String> params, Map<String, String> headers) throws IOException {

        // Create a method instance.
        HttpPost httpPost = new HttpPost();

        if(headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        } else {
            httpPost.addHeader(HTTP.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        }

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        if (params != null) {
            for (Entry<String, String> entry : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = httpclient.execute(httpPost);
        return getStringResponse(response);
    }

    public String post(String url, String content, Map<String, String> headers) throws URISyntaxException {
        return post(new URI(url), content, headers);
    }

    public String post(URI uri, String content, Map<String, String> headers) {

        try {
            // Create a method instance.
            HttpPost httpPost = new HttpPost(uri);
            String contentType = DEFAULT_CONTENT_TYPE;

            if(headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());

                    if(HTTP.CONTENT_TYPE.equals(entry.getKey())) {
                        contentType = entry.getValue();
                    }
                }
            } else {
                httpPost.addHeader(HTTP.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
            }

            StringEntity stringEntity = new StringEntity(content);
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, contentType));
            httpPost.setEntity(stringEntity);

            HttpResponse response = httpclient.execute(httpPost);
            return getStringResponse(response);
        }
        catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String get(String url) throws IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet(new URI(url));
        HttpResponse response = httpclient.execute(httpGet, context);
        try {
            HttpEntity entity = response.getEntity();
            StringWriter writer = new StringWriter();
            IOUtils.copy(entity.getContent(), writer, "UTF-8");
            return writer.toString();
        } finally {
            EntityUtils.consume(response.getEntity());
            httpGet.releaseConnection();
        }
    }

    public String get(String url, Map<String, String> params, Map<String, String> headers) throws URISyntaxException {
        return get(new URI(url), params, headers);
    }

    public String get(URI uri, Map<String, String> params, Map<String, String> headers) {

        CloseableHttpResponse response = null;

        try {

            HttpClientContext context = HttpClientContext.create();
            CookieStore cookieStore = new BasicCookieStore();
            // Contextual attributes set the local context level will take precedence over those set at the client level.
            context.setCookieStore(cookieStore);

            RequestBuilder requestBuilder = RequestBuilder.get().setUri(uri);

            if (params != null) {
                for (Entry<String, String> entry : params.entrySet()) {
                    requestBuilder.addParameter(entry.getKey(),
                            entry.getValue());
                }
            }

            HttpUriRequest httpGet = requestBuilder.build();

            if(headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            } else {
                httpGet.addHeader(HTTP.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
            }

            response = httpclient.execute(httpGet);
            //EntityUtils.consume(response.getEntity());
            return getStringResponse(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(response != null) {
                    response.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getStringResponse(HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        StringBuilder builder = new StringBuilder();
        HttpEntity entity = response.getEntity();

        if (statusCode == 200) {
            InputStream contentData = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(contentData));
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } else {
            if(entity != null) {
                String responseString = EntityUtils.toString(entity);
                builder.append(responseString);
            }

            logger.error(builder.toString());
        }

        return builder.toString();
    }

    /**
     * Preemptive authentication interceptor
     */
    private static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

        /**
         * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest, org.apache.http.protocol.HttpContext)
         */
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            // Get the AuthState
            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

            // If no auth scheme available yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
                CredentialsProvider credentialsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials credentials = credentialsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                    if (credentials == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }

                    authState.update(authScheme, credentials);
                }
            }
        }
    }

    /**
     * Call the HTTP URL using GET method and get the response from the Server.
     * @param url
     * 		The HTTP URL which is called using the GET method.
     * @return {@link String}
     * 		response returned from the server.
     * @throws HttpException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String getServerResponse(String url) throws IOException, URISyntaxException {

        int connectiontimeout = 30000;
        int sotimeout = 60000;
        int poolTimeout = 10000;

        HttpClient clientUtil = new HttpClient(connectiontimeout, sotimeout, poolTimeout);
        return clientUtil.get(url, null, null);
    }


    /**
     * Ping the specified host in order to check if the server is available.
     * @param hostname
     * 		specifies the host name of the server to test for connectivity.
     * @return
     * 		true is host is available else false.
     */
    public static boolean isHostAvailable(String hostname) {

        Socket socket = null;
        boolean reachable = false;
        try {
            socket = new Socket(hostname, 80);
            reachable = true;
        } catch (Exception ex) {
            reachable = false;
        } finally {
            if (socket != null) {
                try { socket.close(); }
                catch(IOException e) {
                    logger.error("IOException", e);
                }
            }
        }
        return reachable;
    }
}
