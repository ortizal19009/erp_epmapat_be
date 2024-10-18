package com.epmapat.erp_epmapat.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.FacIntereses;
import com.epmapat.erp_epmapat.modelo.Intereses;
import com.epmapat.erp_epmapat.repositorio.InteresesR;

import java.time.*;

@Service
public class InteresServicio {

	@Autowired
	private InteresesR dao;
	@Autowired
	private FacturaServicio s_factura;
	@Autowired
	private LecturaServicio s_lectura;

	public List<Intereses> findAll() {
		return dao.findAll();
	}

	public List<Intereses> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	public List<Intereses> findByAnioMes(Number anio, Number mes) {
		return dao.findByAnioMes(anio, mes);
	}

	public List<Intereses> findUltimo() {
		return dao.findUltimo();
	}

	public <S extends Intereses> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Intereses> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public void delete(Intereses entity) {
		dao.delete(entity);
	}

	public Object _facturaid(Long idfactura) {
		List<FacIntereses> factura = s_lectura.getForIntereses(idfactura);

		if (factura.isEmpty()) {
			return s_factura.getForIntereses(idfactura);
		} else {
			final double[] totalInteres = { 0.0 };
			factura.stream().forEach(_factura -> {
				LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
				LocalDate fecFinal = LocalDate.now();
				int anioI = fecInicio.getYear();
				int anioF = fecFinal.getYear();
				List<Float> porcentaje;
				if (anioI < anioF) {
					int mesI = fecInicio.getMonthValue();
					while (anioI <= anioF) {
						if (anioI < anioF) {
							porcentaje = dao.porcentajes(anioI,
									mesI, 12);
						} else if (anioI == anioF) {
							porcentaje = dao.porcentajes(anioF, 1,
									fecFinal.getMonthValue());
						}
						mesI = 1;
						anioI++;
					}

				} else {
					porcentaje = dao.porcentajes(fecFinal.getYear(),
							fecInicio.getMonthValue(), fecFinal.getMonthValue());
				}
				// Obtener la lista de intereses aplicables

				// Calcular el interés total para esta factura
				/*
				 * intereses.length.forEach(interes -> { System.out.println(interes); double
				 * interesCalculado = (interes * (factura.getSuma() + totalInteres[0])) / 100;
				 * totalInteres[0] += interesCalculado; // Sumar al interés total });
				 */
			});

			// Retornar el interés total
			return totalInteres[0];
		}
	}

	public Object facturaid(Long idfactura) {
		List<FacIntereses> factura = s_lectura.getForIntereses(idfactura);

		if (factura.isEmpty()) {
			factura = s_factura.getForIntereses(idfactura);
		}
		// Variable para almacenar el interés total de todas las facturas
		final double[] totalInteres = { 0.0 };
		// Uso de Java Streams para mapear la lista
		factura.stream().forEach(_factura -> {
			// Convertir la fecha de creación a LocalDate
			LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
			LocalDate fecFinal = LocalDate.now();
			int anioI = fecInicio.getYear();
			int anioF = fecFinal.getYear();
			List<Float> todosPorcentajes = new ArrayList<>();
			if (anioI < anioF) {
				int mesI = fecInicio.getMonthValue();
				while (anioI <= anioF) {
					if (anioI < anioF) {
						List<Float> porcentaje = dao.porcentajes(anioI, mesI, 12);
						todosPorcentajes.addAll(porcentaje); // Añadir los porcentajes a la lista total
					} else if (anioI == anioF ) {
						List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
						if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
							porcentaje.add(0.00f);
						} else {
							porcentaje = dao.porcentajes(anioF, 1, fecFinal.getMonthValue() - 2);
							todosPorcentajes.addAll(porcentaje);
						} 
					}
					mesI = 1;
					anioI++;
				}
			} else if(anioF < anioI){
			} else {
				List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
				if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
					porcentaje.add(0.00f);
				} else {

					porcentaje = dao.porcentajes(fecFinal.getYear(), fecInicio.getMonthValue(),
							fecFinal.getMonthValue() - 2);
					todosPorcentajes.addAll(porcentaje);
				}
			}
			todosPorcentajes.forEach(interes -> {
				double interesCalculado = (interes * (_factura.getSuma() + totalInteres[0])) / 100;
				totalInteres[0] += interesCalculado; // Sumar al interés total
			});
		});
		return totalInteres[0];
	}

}
