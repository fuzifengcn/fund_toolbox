package com.fzf.oop.util;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * OK HTTP请求工具类
 * @author fuzifeng
 */
public class OkHttpUtils {
    


    public static final MediaType JSON_MEDIA_TYPE =MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            //设置OKHTTP的连接池的保活时间为60秒，它默认是5分钟，如果在5分钟之内创建超过2000个连接就报内存溢出
            .connectionPool(new ConnectionPool(5, 60, TimeUnit.SECONDS))
            .build();
    
    
    
    public static String get(String url) {
        return get(url,null,null);
    }
    public static String get(String url,Map<String,String> params) {
        return get(url,params,null);
    }
    public static String get(String url, Map<String,String> param, Map<String,String> headers) {

        String addParamToUrl = addParamToUrl(url,param);

        Request.Builder builder = new Request.Builder().url(addParamToUrl).get();
       if (headers!= null && headers.size()>0 ){
           for (String headerKey : headers.keySet()) {
               builder.header(headerKey,headers.get(headerKey));
           }
       }
        Request request = builder.build();
        ResponseBody body = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response != null && response.isSuccessful()){
                body = response.body();
                if (body != null) {
                    return body.string();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(body != null) {
                body.close();
            }
        }
        
        return null;
    }
    public static String sendPostByJsonRequestBody(String url,Object paramObject) {


        RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, JSON.toJSONString(paramObject));
        Request request = new Request.Builder().url(url).post(requestBody).build();

        ResponseBody body = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response != null && response.isSuccessful()){
                body = response.body();
                if (body != null) {
                    return body.string();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(body != null) {
                body.close();
            }
        }

        return null;
    }


    /**
     * URL增加参数
     * @param oldUrlStr  原URL 可带参数
     * @param param      新增加参数 字符串 &符号连接
     * @return 增加参数后的URL
     */
    private static String addParamToUrl(String oldUrlStr, String param) {
        try {
            URI uri = new URI(oldUrlStr);
            String scheme = StringUtils.isBlank(uri.getScheme())?"":uri.getScheme();
            String userInfo = StringUtils.isBlank(uri.getUserInfo())?"":uri.getUserInfo();
            int port = uri.getPort();
            String host = StringUtils.isBlank(uri.getHost())?"":uri.getHost();
            String path = StringUtils.isBlank(uri.getPath())?"":uri.getPath();
            String query = uri.getQuery();
            String fragment = uri.getFragment();
            String newQuery;
            String andStr = "&";
            
            if(query == null) {
                newQuery = param;
            }else {
                if(query.startsWith(andStr)) {
                    query = query.substring(1);
                }
                if(query.endsWith(andStr)) {
                    query = query.substring(0,query.length()-1);
                }
                if(!"".equals(param)) {
                    newQuery = query+andStr+param;
                }else {
                    newQuery = query;
                    
                }
            }
            return new URI(scheme, userInfo, host, port, path, newQuery, fragment).toURL().toExternalForm();
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String addParamToUrl(String oldUrlStr, Map<String, String> param) {

        StringBuilder stringBuilder = new StringBuilder();

        if (param != null && param.size() > 0) {
            Set<Entry<String, String>> entrySet = param.entrySet();
            for (Entry<String, String> entry : entrySet) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            return addParamToUrl(oldUrlStr, stringBuilder.substring(0, stringBuilder.length() - 1));
        }
        return addParamToUrl(oldUrlStr, "");

    }
    
    public static String addParamToUrlByName(String oldUrlStr, String name,String value) {
        if(StringUtils.isNotBlank(name)) {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put(name, value);
            return addParamToUrl(oldUrlStr, hashMap);
        }
        return null;
    }
    
}
