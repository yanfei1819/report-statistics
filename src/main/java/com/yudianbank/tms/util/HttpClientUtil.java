package com.yudianbank.tms.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * Created by chengtianren on 2017/5/14.
 */
public class HttpClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 模拟Json格式的post请求
     */
    public static String doPost(String url, String json, String cookieValue, boolean useHttps,
                                CookieStore cookie, RequestConfig requestConfig, Map<String, String> headers) {
        if (url == null) return "";
        CloseableHttpClient client = null;
        String strReturn = "";
        try {
            if (!useHttps) useHttps = StringUtils.startsWithIgnoreCase(url, "https");
            client = getHttpClient(useHttps, cookie);
            HttpPost httpPost = new HttpPost(url.trim());
            httpPost.setHeader("Content-type", MediaType.APPLICATION_JSON_VALUE);
            if (StringUtils.hasText(cookieValue)) httpPost.setHeader("Cookie", cookieValue);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet())
                    httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            httpPost.setConfig((null == requestConfig) ? getRequestConfig() : requestConfig);
            StringEntity postEntity = new StringEntity(json, ProjectUtil.DEFAULT_CHARSET);
            httpPost.setEntity(postEntity);
            HttpResponse res = client.execute(httpPost);
            strReturn = printResponse(res);
        } catch (SocketTimeoutException ste) {
            LOGGER.info("SocketTimeoutException", ste);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        } finally {
            IOUtils.closeQuietly(client);
        }
        return strReturn;
    }

    // 发送post请求
    public static String doPost(String url, String json, boolean useHttps, CookieStore cookie,
                                RequestConfig requestConfig, Map<String, String> headers) {
        return doPost(url, json, null, useHttps, cookie, requestConfig, headers);
    }

    // 发送post请求
    public static String doPost(String url, String json, boolean useHttps, Map<String, String> headers) {
        return doPost(url, json, useHttps, null, null, headers);
    }

    // 发送post请求
    public static String doPost(String url, String json) {
        return doPost(url, json, false, null);
    }

    // 构建请求配置
    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000).setExpectContinueEnabled(false).build();
    }

    // 打印返回结果,并返回给调用者
    private static String printResponse(HttpResponse httpResponse) throws Exception {
        String rtnStr = "error";
        HttpEntity entity = httpResponse.getEntity();
        if (httpResponse.getStatusLine().getStatusCode() == 200 && entity != null) {
            String responseString = EntityUtils.toString(entity, ProjectUtil.DEFAULT_CHARSET);
            rtnStr = responseString.replace("\r\n", "");
        } else {
            LOGGER.warn("HTTP接口返回的状态码不为200！{}", httpResponse.getStatusLine());
        }
        // 关闭底层流
        EntityUtils.consumeQuietly(entity);
        return rtnStr;
    }

    /**
     * 获得一个httpClient
     */
    private static CloseableHttpClient getHttpClient(boolean useHttps, CookieStore cookie) {
        CloseableHttpClient client = null;
        try {
            HttpClientBuilder builder = HttpClients.custom();
            if (cookie != null) builder = builder.setDefaultCookieStore(cookie);
            if (useHttps) {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
                SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(sslContext);
                builder = builder.setSSLSocketFactory(ssl);
            }
            client = builder.build();
        } catch (Exception e) {
            LOGGER.error("Get CloseableHttpClient ERROR!", e);
        }
        return client;
    }
}