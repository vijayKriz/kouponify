package com.rest.kouponify.broker;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.kouponify.common.HttpUtil;
import com.rest.kouponify.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 2/15/16.
 */
@Slf4j
public class ShopifyBrokerImpl implements ShopifyBroker {

    //TODO make this url dynamic to support for multiple client shop
    private String CLIENT_URL = "https://kouponify.myshopify.com/admin/";
    private String REDIRECT_URL = "https://localhost:8080/kouponify/";
    private String nonce = "bizzyCode";
    private String SCOPE = "write_products,write_customers,write_orders,read_products,read_customers,read_orders";



    /**
     * One time manual operation to obtain accessCode
     *
     * @return accessCode
     */
    private String getAccessCode() throws IOException {
        StringBuffer AUTH_URL = new StringBuffer();
        AUTH_URL.append(CLIENT_URL);
        AUTH_URL.append("oauth/authorize?client_id=");
        AUTH_URL.append(HttpUtil.CLIENT_ID);
        AUTH_URL.append("&scope=");
        AUTH_URL.append(SCOPE);
        AUTH_URL.append("redirect_uri=");
        AUTH_URL.append(REDIRECT_URL);
        AUTH_URL.append("&state=");
        AUTH_URL.append(nonce);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(AUTH_URL.toString());
        CloseableHttpResponse response = httpclient.execute(httpGet);
        log.debug("Access Code:" + response.getEntity().toString());
        return response.getEntity().toString();
    }

    /**
     * One time operation to obtain accessToken
     *
     * @return
     */
    private String getAccessToken() throws IOException {
        StringBuffer OAUTH_URL = new StringBuffer();
        OAUTH_URL.append(CLIENT_URL);
        OAUTH_URL.append("oauth/access_token");
        List<NameValuePair> codeObj = new ArrayList<>();
        codeObj.add(new BasicNameValuePair("client_id", HttpUtil.CLIENT_ID));
        codeObj.add(new BasicNameValuePair("client_secret",HttpUtil.CLIENT_SECRET));
        codeObj.add(new BasicNameValuePair("code", HttpUtil.ACCESS_CODE));
        JSONObject object = HttpUtil.doPost(OAUTH_URL.toString(), codeObj);
        return object.getString("access_token");
    }

    /**
     * Use refresh Token to generate Access Token on expirations
     *
     * @return
     */
    private String refreshAccessToken() {
        //TODO replace static access token
        return HttpUtil.ACCESS_TOKEN;
    }


    @Override
    public List<Order> getOrders() throws IOException{
        StringBuffer ORDER_URL = new StringBuffer();
        ORDER_URL.append(CLIENT_URL);
        ORDER_URL.append("orders.json");
        JSONObject object = HttpUtil.doGet(ORDER_URL.toString());
        return collectionMapper(object);
    }

    @Override
    public Order findOrder(Long id) throws IOException{
        StringBuffer ORDER_URL = new StringBuffer();
        ORDER_URL.append(CLIENT_URL);
        ORDER_URL.append("orders/");
        ORDER_URL.append(id+".json");
        JSONObject object = HttpUtil.doGet(ORDER_URL.toString());
        return objectMapper(object);
    }

    private List<Order> collectionMapper(JSONObject object) throws IOException{
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class,
                Order.class);
        final ArrayList<Order> orders = mapper.readValue(object.getJSONArray("orders").toString(),
                type);

        return orders;
    }

    private Order objectMapper(JSONObject object) throws IOException{
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(Order.class);
        final Order order = mapper.readValue(object.getJSONArray("order").toString(),
                type);
        return order;
    }
}
