package com.epmapat.erp_epmapat.servicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.EstadisticasAbonadosDTO;
import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.interfaces.AbonadoI;
import com.epmapat.erp_epmapat.interfaces.EstadisticasAbonados;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
// import com.epmapat.erp_epmapat.repositorio.ClientesR;

@Service
public class AbonadoServicio {

	@Autowired
	private AbonadosR dao;
	@Autowired
	@Lazy
	private FacturaServicio facturaServicio;

	private static final Map<Integer, String> estados = new HashMap<>();

	static {
		estados.put(0, "Eliminado");
		estados.put(1, "Activo");
		estados.put(2, "Suspendido");
		estados.put(3, "Suspendido y retirado");
	}

	public List<Abonados> findAll(String c, Sort sort) {
		if (c != null) {
			return dao.findAll(c);
		} else {
			return dao.findAll(sort);
		}
	}

	// Todos los Abonados, Campos específicos
	public List<Map<String, Object>> allAbonadosCampos() {
		return dao.allAbonadosCampos();
	}

	// Temporal Todos
	public List<Abonados> tmpTodos() {
		return dao.tmpTodos();
	}

	// Abonados por Cliente (Cuentas de un Cliente)
	public List<Abonados> findByIdcliente(Long idcliente) {
		return dao.findByIdcliente(idcliente);
	}

	public List<Abonados> findByNombreCliente(String nombreCliente) {
		return dao.findByNombreCliente(nombreCliente);
	}

	public List<Abonados> findByidentIficacionCliente(String identificacionCliente) {
		return dao.findByidentIficacionCliente(identificacionCliente);
	}

	public List<Abonados> getAbonadoByid(Long idabonado) {
		return dao.getAbonadoByid(idabonado);
	}

	// Busca Abonado por idabonado con parametro (para recaudacion)
	public List<Abonados> getByIdabonado(Long idabonado) {
		return dao.getByIdabonado(idabonado);
	}

	// Abonados por Ruta
	public List<Abonados> findByIdruta(Long idruta) {
		return dao.findByIdruta(idruta);
	}

	public List<Abonados> findByIdCliente(Long idcliente) {
		return dao.findByIdCliente(idcliente);
	}

	public <S extends Abonados> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Abonados> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public void delete(Abonados entity) {
		dao.delete(entity);
	}

	// Verifica si un Cliente tiene Abonados
	public boolean clienteTieneAbonados(Long idcliente) {
		return dao.existsByIdcliente_clientes(idcliente);
	}

	public Abonados findOne(Long idabonado) {
		return dao.findOne(idabonado);
	}

	public List<AbonadoI> getAbonadoInterface(Long idabonado) {
		return dao.getAbonadoInterface(idabonado);
	}

	public List<AbonadoI> getAbonadoInterfaceNombre(String nombre) {
		return dao.getAbonadoInterfaceNombre(nombre);
	}

	public List<AbonadoI> getAbonadoInterfaceIdentificacion(String identificacion) {
		return dao.getAbonadoInterfaceIdentificacion(identificacion);
	}

	public List<AbonadoI> getAbonadoInterfaceIdCliente(Long idcliente) {
		return dao.getAbonadoInterfaceIdCliente(idcliente);
	}

	// Un Abonado
	public Abonados unAbonado(Long idabonado) {
		return dao.findByIdabonado(idabonado);
	}

	public List<ValorFactDTO> getCuentasByRutas(Long idruta) {
		// Obtener la lista de abonados por ruta
		List<Abonados> abonados = dao.getCuentasByRutas(idruta);

		// Procesar cada abonado y obtener los totales
		List<ValorFactDTO> totales = abonados.stream()
				.map(item -> {
					return facturaServicio.getTotalesByAbonadoDatos(item.getIdabonado());
				}) // Obtener los totales para cada abonado
				.collect(Collectors.toList()); // Recopilar los resultados en una lista

		// Devolver la lista de totales
		return totales;
	}

	public List<EstadisticasAbonados> getCuentasByCategoria() {
		return dao.getCuentasByCategoria();
	}

	public List<EstadisticasAbonadosDTO> getCuentasByEstado() {
		List<EstadisticasAbonados> estadisticas = dao.getCuentasByEstado();

		// 1) Inicializar la lista de DTOs antes de iterar
		List<EstadisticasAbonadosDTO> estadisticasDTO = new ArrayList<>();

		for (EstadisticasAbonados ea : estadisticas) {
			EstadisticasAbonadosDTO dto = new EstadisticasAbonadosDTO();

			// 2) Obtener el código de estado del entity. Aquí asumimos que ea.getEstado()
			// devuelve un Integer.
			Integer codigo = ea.getEstado();

			// 3) Convertir a Long para poder buscar en el mapa estático (que está declarado
			// como Map<Long,String>).
			/* Long codigoLong = Long.valueOf(codigoInt); */

			// 4) Obtener la descripción del estado desde el mapa. Si no existe, se marca
			// como "Desconocido".
			String descripcion = estados.get(codigo);
			if (descripcion == null) {
				descripcion = "Desconocido";
			}

			// 5) Rellenar el DTO
			dto.setEstado(codigo);
			dto.setDescripcion(descripcion);
			dto.setNcuentas(ea.getNcuentas());

			// 6) Agregar el DTO a la lista (usando add en lugar de push)
			estadisticasDTO.add(dto);
		}

		return estadisticasDTO;
	}

	public List<Abonados> findByEstado(Long estado) {
		return dao.findByEstado(estado);
	}

}
