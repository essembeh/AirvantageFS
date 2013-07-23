package org.essembeh.airvantagefs.session;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

public class MyHttpClientFactory {
	public DefaultHttpClient buildHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		configureCookieStore(httpClient);
		configureSSLHandling(httpClient);
		return httpClient;
	}

	private void configureCookieStore(DefaultHttpClient httpClient) {
		httpClient.setCookieStore(new BasicCookieStore());
	}

	private void configureSSLHandling(DefaultHttpClient httpClient) {
		SchemeRegistry schemeRegistry = httpClient.getConnectionManager().getSchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, buildSSLSocketFactory()));
	}

	private SSLSocketFactory buildSSLSocketFactory() {
		TrustStrategy trustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				return true;
			}
		};
		SSLSocketFactory sslSocketFactory = null;
		try {
			sslSocketFactory = new SSLSocketFactory(trustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Exception ignored) {
		}
		return sslSocketFactory;
	}

}
