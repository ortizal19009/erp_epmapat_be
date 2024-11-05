package com.epmapat.erp_epmapat.servicio.microservicio_recaudacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaudacionMicroservice {
	@Autowired
	private RestTemplate restTemplate;
	private final String URL_FACTURA = "http://localhost:8081/facturas/";
	private final String URL_CAJA = "http://localhost:8081/cajas/";

	/* RECAUDACION MICROSERVICE */
	public List<Object> sinCobrarByCuenta(Long cuenta) {
		List<Object> facturas = restTemplate
				.getForObject(URL_FACTURA + "sincobro/cuenta?cuenta=" + cuenta, List.class);
		return facturas;
	}

	public List<Object> sinCobrarByCliente(Long idcliente) {
		List<Object> facturas = restTemplate
				.getForObject(URL_FACTURA + "sincobro/cliente?idcliente=" + idcliente, List.class);
		return facturas;
	}

	public Object testConnection(Long idusuario) {
		Object conection = restTemplate
				.getForObject(URL_CAJA + "test_connection?user=" + idusuario, Object.class);

		// .exchange(URL_CAJA+"test_connection?user=" + idusuario,HttpMethod.GET,
		// null,Object.class);

		return conection;
	}

	public Object sinInCaja(String username, String password) {
		Object singIn = restTemplate.exchange(URL_CAJA + "singin?username=" + username + "&password=" + password,
				HttpMethod.GET, null, Object.class);
		return singIn;
	}
	public Object sinOutCaja(String username) {
		System.out.println(username);
		Object singIn = restTemplate.exchange(URL_CAJA + "singout?username=" + username ,
				HttpMethod.PUT, null, Object.class);
		return singIn;
	}
	public Object cobrar(Object obj) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(obj, headers);
			ResponseEntity<Object> responseEntity = restTemplate.exchange(
					URL_FACTURA + "cobrar",
					HttpMethod.PUT,
					requestEntity,
					Object.class);
			return responseEntity.getBody();
		} catch (RestClientException e) {
			// Handle specific exceptions and log errors
			System.out.printf("Error while making PUT request to {}", URL_FACTURA, e);
			throw new RuntimeException("Error collecting payment: " + e.getMessage());
		}
	}
}
