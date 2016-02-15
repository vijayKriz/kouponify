package com.rest.kouponify.common;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by vijay on 2/15/16.
 */
public class HttpUtil {

    private static String SHOPIFY_AUTH_HEADER = "X-Shopify-Access-Token";
    private static String ACCESS_TOKEN = "****************************";

    public static JSONObject doGet(String URL) throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(URL);
        httpGet.addHeader(SHOPIFY_AUTH_HEADER, ACCESS_TOKEN);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        return getJsonObject(response);
    }

    public static JSONObject doPost(String URL, List<NameValuePair> postBody) throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(URL);
        httpPost.setEntity(new UrlEncodedFormEntity(postBody));
        CloseableHttpResponse response = httpclient.execute(httpPost);
        return getJsonObject(response);
    }

    public static JSONObject getJsonObject(CloseableHttpResponse response) throws IOException{
        JSONObject result = null;
        try {
            HttpEntity entity = response.getEntity();
            String retSrc = EntityUtils.toString(entity);
            result = new JSONObject(retSrc);
        } finally {
            response.close();
        }
        return result;
    }

}
