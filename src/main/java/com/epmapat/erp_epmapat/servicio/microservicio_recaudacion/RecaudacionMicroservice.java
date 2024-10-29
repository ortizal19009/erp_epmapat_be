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

	public Object cobrar(Object obj) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> requestEntity = new HttpEntity<>(obj,headers);

            ResponseEntity<Object> responseEntity = restTemplate.exchange(
                    URL_FACTURA,
                    HttpMethod.PUT,
                    requestEntity,
                    Object.class
            );

            return responseEntity.getBody();
        } catch (RestClientException e) {
            // Handle specific exceptions and log errors
            System.out.printf("Error while making PUT request to {}", URL_FACTURA, e);
            throw new RuntimeException("Error collecting payment: " + e.getMessage());
        }
    }
}
