package com.net;

/**
 * Created by zl on 16/1/15.
 */



import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

public class Delegator {
    private static final Logger logger = Logger.getLogger(Delegator.class);
    protected static final int MAX = 600;
    protected static final int MAX_PER_ROUTE = 400;
    protected boolean useProxy = false;
    protected List<HttpHost> proxies = Lists.newArrayList();
    protected int proxyIndex = 0;
    protected Random random = new Random();
    protected HttpClient client;

    public Delegator() {
        PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
        manager.setMaxTotal(600);
        manager.setDefaultMaxPerRoute(400);
        this.client = new DecompressingHttpClient(new DefaultHttpClient(manager));
    }

    public Delegator(int timeout) {
        PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
        manager.setMaxTotal(600);
        manager.setDefaultMaxPerRoute(400);
        this.client = new DecompressingHttpClient(new DefaultHttpClient(manager));
        HttpParams params = this.client.getParams();
        params.setParameter("http.connection.timeout", Integer.valueOf(timeout));
        params.setParameter("http.socket.timeout", Integer.valueOf(timeout));
    }

    public Delegator(int connectionTimeout, int socketTimeout) {
        PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
        manager.setMaxTotal(600);
        manager.setDefaultMaxPerRoute(400);
        this.client = new DecompressingHttpClient(new DefaultHttpClient(manager));
        HttpParams params = this.client.getParams();
        params.setParameter("http.connection.timeout", Integer.valueOf(connectionTimeout));
        params.setParameter("http.socket.timeout", Integer.valueOf(socketTimeout));
    }

    public HttpResponse getResponse(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        HttpResponse resp = this.client.execute(request);
        return resp;
    }

    protected synchronized HttpHost getNextProxy() {
        if(this.useProxy && this.proxies != null && !this.proxies.isEmpty()) {
            int idx = this.proxyIndex % this.proxies.size();
            ++this.proxyIndex;
            return (HttpHost)this.proxies.get(idx);
        } else {
            return null;
        }
    }

    protected HttpHost getRandomProxy() {
        if(this.useProxy && this.proxies != null && !this.proxies.isEmpty()) {
            int idx = this.random.nextInt(this.proxies.size());
            return (HttpHost)this.proxies.get(idx);
        } else {
            return null;
        }
    }

    protected HttpGet getRequest(String url) {
        HttpGet request = new HttpGet(url);
        if(this.useProxy) {
            HttpHost proxy = this.getRandomProxy();
            if(proxy != null) {
                request.getParams().setParameter("http.route.default-proxy", proxy);
            }
        }

        return request;
    }

    protected HttpPost postRequest(String url) {
        HttpPost request = new HttpPost(url);
        if(this.useProxy) {
            HttpHost proxy = this.getRandomProxy();
            if(proxy != null) {
                request.getParams().setParameter("http.route.default-proxy", proxy);
            }
        }

        return request;
    }

    public String get(String url) {
        HttpGet request = this.getRequest(url);

        try {
            HttpResponse e = this.client.execute(request);
            HttpEntity entity = e.getEntity();
            if(entity != null) {
                InputStream instream = entity.getContent();

                try {
                    BufferedReader ex = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
                    StringBuffer sb = new StringBuffer();
                    char[] buffer = new char[1024];

                    int size;
                    while((size = ex.read(buffer)) != -1) {
                        sb.append(buffer, 0, size);
                    }

                    String var10 = sb.toString();
                    return var10;
                } catch (IOException var18) {
                    throw var18;
                } catch (RuntimeException var19) {
                    request.abort();
                    throw var19;
                } finally {
                    instream.close();
                }
            }
        } catch (ClientProtocolException var21) {
            logger.error(url, var21);
        } catch (IOException var22) {
            logger.error(url, var22);
        } catch (Exception var23) {
            logger.error(url, var23);
        }

        return null;
    }

    public byte[] getBytes(String url) {
        HttpGet request = this.getRequest(url);

        try {
            HttpResponse e = this.client.execute(request);
            HttpEntity entity = e.getEntity();
            if(entity != null) {
                InputStream inStream = entity.getContent();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                boolean size = false;
                byte[] buffer = new byte[1024];

                int size1;
                while((size1 = inStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, size1);
                }

                return bos.toByteArray();
            }
        } catch (ClientProtocolException var9) {
            logger.error(url, var9);
        } catch (IOException var10) {
            logger.error(url, var10);
        } catch (Exception var11) {
            logger.error(url, var11);
        }

        return null;
    }

    public String post(String url, Map<String, ?> params) {
        HttpPost request = this.postRequest(url);
        LinkedList list = new LinkedList();
        Iterator e = params.entrySet().iterator();

        while(e.hasNext()) {
            Entry instream = (Entry)e.next();
            if(instream.getValue() != null) {
                list.add(new BasicNameValuePair((String)instream.getKey(), instream.getValue().toString()));
            }
        }

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            request.setEntity(entity);
        } catch (UnsupportedEncodingException var20) {
            logger.error(url, var20);
        }

        try {
            HttpResponse e1 = this.client.execute(request);
            HttpEntity entity1 = e1.getEntity();
            if(entity1 != null) {
                InputStream instream1 = entity1.getContent();

                try {
                    BufferedReader ex = new BufferedReader(new InputStreamReader(instream1, Charset.forName("UTF-8")));
                    StringBuffer sb = new StringBuffer();
                    char[] buffer = new char[1024];

                    int size;
                    while((size = ex.read(buffer)) != -1) {
                        sb.append(buffer, 0, size);
                    }

                    String var12 = sb.toString();
                    return var12;
                } catch (IOException var21) {
                    throw var21;
                } catch (RuntimeException var22) {
                    request.abort();
                    throw var22;
                } finally {
                    instream1.close();
                }
            }
        } catch (ClientProtocolException var24) {
            logger.error(url, var24);
        } catch (IOException var25) {
            if(var25 instanceof SocketTimeoutException) {
                logger.warn(url, var25);
            } else {
                logger.error(url, var25);
            }
        }

        return null;
    }

    public String postBytes(String url, Map<String, byte[]> params) {
        HttpPost request = this.postRequest(url);
        LinkedList list = new LinkedList();
        Iterator e = params.entrySet().iterator();

        while(e.hasNext()) {
            Entry instream = (Entry)e.next();
            if(instream.getValue() != null) {
                list.add(new BasicNameValuePair((String)instream.getKey(), new String((byte[])instream.getValue(), Charset.forName("latin1"))));
            }
        }

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "latin1");
            request.setEntity(entity);
        } catch (UnsupportedEncodingException var20) {
            logger.error(url, var20);
        }

        try {
            HttpResponse e1 = this.client.execute(request);
            HttpEntity entity1 = e1.getEntity();
            if(entity1 != null) {
                InputStream instream1 = entity1.getContent();

                try {
                    BufferedReader ex = new BufferedReader(new InputStreamReader(instream1, Charset.forName("UTF-8")));
                    StringBuffer sb = new StringBuffer();
                    char[] buffer = new char[1024];

                    int size;
                    while((size = ex.read(buffer)) != -1) {
                        sb.append(buffer, 0, size);
                    }

                    String var12 = sb.toString();
                    return var12;
                } catch (IOException var21) {
                    throw var21;
                } catch (RuntimeException var22) {
                    request.abort();
                    throw var22;
                } finally {
                    instream1.close();
                }
            }
        } catch (ClientProtocolException var24) {
            logger.error(url, var24);
        } catch (IOException var25) {
            if(var25 instanceof SocketTimeoutException) {
                logger.warn(url, var25);
            } else {
                logger.error(url, var25);
            }
        }

        return null;
    }

    public String post(String url, String json) {
        HttpPost request = this.postRequest(url);

        try {
            StringEntity entity = new StringEntity(json, "UTF-8");
            request.setEntity(entity);
        } catch (UnsupportedEncodingException var19) {
            logger.error(url, var19);
        }

        try {
            HttpResponse e = this.client.execute(request);
            HttpEntity entity1 = e.getEntity();
            if(entity1 != null) {
                InputStream instream = entity1.getContent();

                try {
                    BufferedReader ex = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
                    StringBuffer sb = new StringBuffer();
                    char[] buffer = new char[1024];

                    int size;
                    while((size = ex.read(buffer)) != -1) {
                        sb.append(buffer, 0, size);
                    }

                    String var11 = sb.toString();
                    return var11;
                } catch (IOException var20) {
                    throw var20;
                } catch (RuntimeException var21) {
                    request.abort();
                    throw var21;
                } finally {
                    instream.close();
                }
            }
        } catch (ClientProtocolException var23) {
            logger.error(url, var23);
        } catch (IOException var24) {
            if(var24 instanceof SocketTimeoutException) {
                logger.warn(url, var24);
            } else {
                logger.error(url, var24);
            }
        }

        return null;
    }

    public static void main(String[] args) {
        Delegator delegator = new Delegator(200);
        System.out.println(delegator.client.getParams().getParameter("http.protocol.version"));
        System.out.println(delegator.client.getParams().getParameter("http.socket.timeout"));
    }

    public boolean isUseProxy() {
        return this.useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public List<HttpHost> getProxies() {
        return this.proxies;
    }

    public void setProxies(List<HttpHost> proxies) {
        this.proxies = proxies;
    }
}

