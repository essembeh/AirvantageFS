package org.essembeh.airvantagefs.session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.essembeh.airvantagefs.bean.TimestampedValue;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AmsSession {

	private final static String API = "https://edge.airvantage.net/device";

	private final User user;

	public AmsSession(User user) {
		super();
		this.user = user;
		System.err.println("Using session with id: " + user.getUsername());
	}

	public void pushData(List<Map<String, List<TimestampedValue>>> values) throws Exception {
		HttpClient httpClient = new MyHttpClientFactory().buildHttpClient();
		try {
			HttpPost request = new HttpPost(API + "/messages");
			request.addHeader("content-type", "application/json");
			String auth = user.toBasicAuthString();
			request.addHeader("authorization", auth);
			String json = new ObjectMapper().writeValueAsString(values);
			System.err.println("POST: " + request.getURI().toString() + " " + json);
			request.setEntity(new StringEntity(json));
			HttpResponse response = httpClient.execute(request);
			System.err.println(response.getStatusLine());
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public void pushContent(String nodePath, String content) throws Exception {
		System.out.println("Push --> " + nodePath + ": " + content);
		Map<String, List<TimestampedValue>> values = new HashMap<>();
		values.put(nodePath, Arrays.asList(new TimestampedValue(System.currentTimeMillis(), content)));
		pushData(Arrays.asList(values));
	}
}
