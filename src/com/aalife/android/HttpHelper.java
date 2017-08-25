package com.aalife.android;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpHelper {
    public static final int DEFAULT_TIMEOUT = 3000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
    public static final int DEFAULT_HOST_CONNECTIONS = 30;
    public static final int DEFAULT_MAX_CONNECTIONS = 60;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    
	public static String post(String url, List<NameValuePair> params) {
		String result = "{\"result\":\"no\"}";
		try {
			HttpPost request = new HttpPost(url);
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = getHttpClient().execute(request);			

			if(response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity()).trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
    
	public static String post2(String url, List<NameValuePair> params) {
		String result = "{\"result\":\"no\"}";
		try {
			HttpPost request = new HttpPost(url);
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = getHttpClient2().execute(request);			

			if(response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity()).trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String post(String url) {
		String result = "{\"result\":\"no\"}";
		try {
			HttpPost request = new HttpPost(url);
			HttpResponse response = getHttpClient().execute(request);			

			if(response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity()).trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static HttpClient getHttpClient() {	
		HttpParams params = new BasicHttpParams();
        
        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_SOCKET_TIMEOUT);
        ConnManagerParams.setTimeout(params, DEFAULT_TIMEOUT);
        
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(DEFAULT_HOST_CONNECTIONS));  
        ConnManagerParams.setMaxTotalConnections(params, DEFAULT_MAX_CONNECTIONS); 
        HttpConnectionParams.setSocketBufferSize(params, DEFAULT_SOCKET_BUFFER_SIZE);
        
        HttpProtocolParams.setUseExpectContinue(params, true); 
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8); 
        HttpClientParams.setRedirecting(params, false);
        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";  
        HttpProtocolParams.setUserAgent(params, userAgent); 
        HttpConnectionParams.setTcpNoDelay(params, true);

        HttpClient httpClient = new DefaultHttpClient(params);
        return httpClient;
    }
	
	public static HttpClient getHttpClient2() {		
        HttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_SOCKET_TIMEOUT);
        ConnManagerParams.setTimeout(params, DEFAULT_TIMEOUT);
		
        return httpClient;
    }
	
	public static String formatJson(List<Map<String, String>> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Iterator<Map<String, String>> it = list.iterator();
		while(it.hasNext()) {
			sb.append("{");
			Map<String, String> map = it.next();
			Iterator<Entry<String, String>> im = map.entrySet().iterator();
			while(im.hasNext()) {
				Entry<String, String> entry = im.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				sb.append("\"" + key + "\":" + "\"" + value + "\",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("},");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		
		return sb.toString();
	}
	
}
