package org.essembeh.airvantagefs.session;

import org.apache.commons.codec.binary.Base64;

public class User {

	private final String username;
	private final String password;

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public String toBasicAuthString() {
		String auth = username + ":"  + password;
		return "Basic " + Base64.encodeBase64String(auth.getBytes()); 
	}
	
	public String getUsername() {
		return username;
	}
}
