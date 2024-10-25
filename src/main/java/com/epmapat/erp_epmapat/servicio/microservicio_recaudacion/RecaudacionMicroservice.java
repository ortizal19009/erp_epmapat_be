package com.epmapat.erp_epmapat.servicio.microservicio_recaudacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaudacionMicroservice {
	@Autowired
	private RestTemplate restTemplate;

	/* RECAUDACION MICROSERVICE */
	public List<Object> sinCobrarByCuenta(Long cuenta) {
		List<Object> facturas = restTemplate
				.getForObject("http://localhost:8081/facturas/sincobro/cuenta?cuenta=" + cuenta, List.class);
		return facturas;
	}

	public List<Object> sinCobrarByCliente(Long idcliente) {
		List<Object> facturas = restTemplate
				.getForObject("http://localhost:8081/facturas/sincobro/cliente?idcliente=" + idcliente, List.class);
		return facturas;
	}

	private final String URL_FACTURA = "http://localhost:8081/facturas/cobrar";

	public <T> T cobrar(T obj) {
		HttpEntity<T> requestEntity = new HttpEntity<>(obj);
		ResponseEntity<T> response = (ResponseEntity<T>) restTemplate.exchange(URL_FACTURA, HttpMethod.PUT,
				requestEntity, Object.class);
		return response.getBody();
	}
}
