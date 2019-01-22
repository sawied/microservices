package com.github.sawied.microservice.trade.api.bean;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * 
 * @author weishuqi
 *
 */
public class HttpProxyClientfactory implements FactoryBean<CloseableHttpClient>, DisposableBean {
    /**
     * httpClient.
     */
    private CloseableHttpClient httpClient;
    /**
     * keystore资源.
     */
    private Resource keystoreResource;
    /**
     * 证书密码.
     */
    private char[] keystorePasswd;
    /**
     * 主机名验证
     */
    private boolean nameVerifier;
    /**
     * 代理服务器端口.
     */
    private int port;
    /**
     * 代理服务器地址.
     */
    private String host;
    /**
     * 连接超时
     */
    private int connectionTimeout = 5000;
    /**
     * 单次请求最长等待时间
     */
    private int socketTimeout = 60000;
    
    private String basicDefaultUserName=null;
    
    private String basicDefaultPassword=null;

    /**
     * 获取CloseableHttpClient接口.
     */
    @Override
    public CloseableHttpClient getObject() throws Exception {
        if (httpClient == null) {
            httpClient = buildHttpClient();
        }
        return httpClient;
    }

    /**
     * 构建httpclient.
     * 
     * @return CloseableHttpClient
     * @throws KeyManagementException
     *             KeyManagementException
     * @throws NoSuchAlgorithmException
     *             NoSuchAlgorithmException
     * @throws KeyStoreException
     *             KeyStoreException
     * @throws CertificateException
     *             CertificateException
     * @throws IOException
     *             IOException
     */
    public CloseableHttpClient buildHttpClient() throws KeyManagementException, NoSuchAlgorithmException,
            KeyStoreException, CertificateException, IOException {
        KeyStore keystore = null;
        final Boolean existKeyStore = keystoreResource != null;
        if (existKeyStore) {
            keystore = KeyStore.getInstance("JKS");
            keystore.load(keystoreResource.getInputStream(), keystorePasswd);
        }

        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(keystore, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return !existKeyStore;
            }
        }).build();
        HttpClientBuilder httpBuilder = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(connectionTimeout)
                        .setSocketTimeout(socketTimeout).build())
                .setSSLContext(sslContext);
        if (!nameVerifier) {
            httpBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier());
        }
        if (host != null && !host.trim().isEmpty()) {
            HttpHost proxyHost = new HttpHost(host, port);
            httpBuilder.setProxy(proxyHost);
        }
        
        if(basicDefaultUserName!=null&&basicDefaultPassword!=null){
        	CredentialsProvider provider = new BasicCredentialsProvider();
            // Create the authentication scope
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.basicDefaultUserName,new String(Base64.getDecoder().decode(this.basicDefaultPassword)));
            // Inject the credentials
            provider.setCredentials(AuthScope.ANY, credentials);
            // Set the default credentials provider
            httpBuilder.setDefaultCredentialsProvider(provider);

        }
        
        this.httpClient = httpBuilder
        		//.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
        		.build();
        return this.httpClient;
    }

    @Override
    public void destroy() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Class<?> getObjectType() {
        return CloseableHttpClient.class;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setKeystoreResource(Resource keystoreResource) {
        this.keystoreResource = keystoreResource;
    }

    public void setKeystorePasswd(char[] keystorePasswd) {
        this.keystorePasswd = keystorePasswd;
    }

    public void setNameVerifier(Boolean nameVerifier) {
        this.nameVerifier = nameVerifier;
    }

    public Resource getKeystoreResource() {
        return keystoreResource;
    }

    public char[] getKeystorePasswd() {
        return keystorePasswd;
    }

    public Boolean getNameVerifier() {
        return nameVerifier;
    }

	public void setBasicDefaultUserName(String basicDefaultUserName) {
		this.basicDefaultUserName = basicDefaultUserName;
	}

	public void setBasicDefaultPassword(String basicDefaultPassword) {
		this.basicDefaultPassword = basicDefaultPassword;
	}

    
    
}
