package com.epmapat.erp_epmapat;

import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ErpEpmapatApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() { return null; }
					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {}
				}
			};
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
			
			System.setProperty("javax.net.ssl.trustStore", "");
			System.setProperty("javax.net.ssl.trustStorePassword", "");
			System.setProperty("javax.net.ssl.keyStore", "");
			System.setProperty("javax.net.ssl.keyStorePassword", "");
			
		} catch (Exception e) {
			System.err.println("Warning: Could not set SSL trust all: " + e.getMessage());
		}
		SpringApplication.run(ErpEpmapatApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder aplication) {
		return aplication.sources(ErpEpmapatApplication.class);
	}
}
