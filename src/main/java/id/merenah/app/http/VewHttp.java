package id.merenah.app.http;


import id.merenah.app.http.config.HttpConfig;
import id.merenah.app.http.config.HttpConnectionPool;
import id.merenah.app.http.config.HttpProxy;
import id.merenah.app.http.enumeration.ProxyType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.Authenticator;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Slf4j
public class VewHttp {

    private HttpConfig config;
    private OkHttpClient client;

    public VewHttp() {
    }

    public HttpConfig getConfig() {
        return config;
    }

    public void setConfig(HttpConfig config) {
        this.config = config;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public Retrofit.Builder builder() throws RuntimeException {
        try {
            return new Retrofit.Builder()
                    .baseUrl(config.getUrl())
                    .client(client == null ? client().build() : client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @NotNull
    public OkHttpClient.Builder client() throws NoSuchAlgorithmException, KeyManagementException {
        if (config == null)
            throw new RuntimeException("Config is null");
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.connectTimeout(config.getTimeout().getConnectTimeout(), config.getTimeout().getConnectTimeoutUnit());
        httpClient.readTimeout(config.getTimeout().getReadTimeout(), config.getTimeout().getReadTimeoutUnit());
        httpClient.writeTimeout(config.getTimeout().getWriteTimeout(), config.getTimeout().getWriteTimeoutUnit());
        httpClient.retryOnConnectionFailure(config.isRetryConnectionFailure());
        httpClient.callTimeout(config.getTimeout().getConnectTimeout(), config.getTimeout().getConnectTimeoutUnit());

        httpClient.interceptors().clear();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder request = original.newBuilder();
            if (config.getAgent() != null)
                request.header("User-Agent", config.getAgent());
            if (!config.getHeaders().isEmpty()) {
                config.getHeaders().forEach(customHeader -> {
                    request.header(customHeader.getKey(), customHeader.getValue());
                });
            }
            if (config.isRetryConnectionFailure()) request.header("Connection", "close");
            return chain.proceed(request.build());
        });

        if (!config.getNetworkInterceptors().isEmpty()) {
            config.getNetworkInterceptors().forEach(interceptor -> {
                httpClient.addNetworkInterceptor(interceptor);
            });
        }
        if (!config.getInterceptors().isEmpty()) {
            config.getInterceptors().forEach(interceptor -> {
                httpClient.addInterceptor(interceptor);
            });
        }
        if (config.getConnectionPool() != null) {
            HttpConnectionPool httpConnectionPool = config.getConnectionPool();
            httpClient.connectionPool(new ConnectionPool(httpConnectionPool.getMaxIdle(), httpConnectionPool.getKeepAlive(), httpConnectionPool.getKeepAliveDuration()));
        }


        ConnectionSpec connectionSpecClearText = new ConnectionSpec
                .Builder(ConnectionSpec.CLEARTEXT)
                .build();
        switch (config.getTLS()) {
            case TLS1_1:
                ConnectionSpec tls11 = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_0, TlsVersion.TLS_1_1)
                        .build();

                httpClient.connectionSpecs(List.of(tls11, connectionSpecClearText));
                break;
            case TLS1_2:
                ConnectionSpec tls12 = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();
                httpClient.connectionSpecs(List.of(tls12, connectionSpecClearText));
                break;
            case TLS1_3:
                ConnectionSpec tls13 = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_3)
                        .build();
                httpClient.connectionSpecs(List.of(tls13, connectionSpecClearText));
                break;
        }

        this.setTrustManager(httpClient);

        if (config.getProxy() != null) {
            HttpProxy httpProxy = config.getProxy();
            if (httpProxy.isProxy()) {
                log.debug("CONNECTION TO {} USE PROXY", config.getUrl());

                if (httpProxy.isAuth()) {
                    log.debug("CONNECTION TO {} USE PROXY WITH AUTH AND PROXY TYPE {}", config.getUrl(), httpProxy.getType());
                    switch (httpProxy.getType()) {
                        case SOCKS:
                            java.net.Authenticator.setDefault(new java.net.Authenticator() {
                                private final PasswordAuthentication authentication = new PasswordAuthentication(httpProxy.getUsername(), httpProxy.getPassword().toCharArray());

                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return authentication;
                                }
                            });
                            httpClient.proxy(new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(httpProxy.getHost(), httpProxy.getPort())));
                            break;
                        case HTTP:
                            Authenticator proxyAuthenticator = (route, response) -> {
                                String credential = Credentials.basic(httpProxy.getUsername(), httpProxy.getPassword());
                                return response.request().newBuilder()
                                        .header("Proxy-Authorization", credential)
                                        .build();
                            };
                            httpClient.authenticator(proxyAuthenticator);
                            httpClient.proxyAuthenticator(proxyAuthenticator);
                            httpClient.proxy(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(httpProxy.getHost(), httpProxy.getPort())));
                            break;

                    }
                }

                httpClient.proxySelector(new ProxySelector() {
                    @Override
                    public List<Proxy> select(URI uri) {

                        // Host
                        final String host = uri.getHost();

                        String noProxyHost = httpProxy.getUrlSkip().stream().collect(Collectors.joining(","));
                        if (StringUtils.contains(noProxyHost, host)) {
                            return List.of(Proxy.NO_PROXY);
                        } else {
                            // Add Proxy
                            if (httpProxy.getType().equals(ProxyType.HTTP)) {
                                return List.of(new Proxy(Proxy.Type.HTTP,
                                        new InetSocketAddress(httpProxy.getHost(), httpProxy.getPort())));
                            } else {
                                return List.of(new Proxy(Proxy.Type.SOCKS,
                                        new InetSocketAddress(httpProxy.getHost(), httpProxy.getPort())));
                            }

                        }
                    }

                    @Override
                    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                        throw new UnsupportedOperationException("Proxy Not supported yet.");
                    }
                });
            }
        }
        return httpClient;
    }

    public void setTrustManager(OkHttpClient.Builder httpClient) throws NoSuchAlgorithmException, KeyManagementException {
        /* create a trust manager that does not validate certificate chains */
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        httpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        httpClient.hostnameVerifier((String s, SSLSession sslSession) -> true);


    }


}
