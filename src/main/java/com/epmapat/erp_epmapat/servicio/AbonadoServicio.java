package com.epmapat.erp_epmapat.servicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.AbonadoGeoPreviewDto;
import com.epmapat.erp_epmapat.DTO.AbonadoGeoUploadItemDto;
import com.epmapat.erp_epmapat.DTO.AbonadoGeoUploadItemResultDto;
import com.epmapat.erp_epmapat.DTO.AbonadoGeoUploadResultDto;
import com.epmapat.erp_epmapat.DTO.AbonadosAuditDTO;
import com.epmapat.erp_epmapat.DTO.EstadisticasAbonadosDTO;
import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.interfaces.AbonadoI;
import com.epmapat.erp_epmapat.interfaces.EstadisticasAbonados;
import com.epmapat.erp_epmapat.interfaces.mobile.AbonadosMobile;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
// import com.epmapat.erp_epmapat.repositorio.ClientesR;

@Service
public class AbonadoServicio {

	@Autowired
	private AbonadosR dao;
	@Autowired
	private AuditoriaGenericaService auditoriaService;
	@Autowired
	@Lazy
	private FacturaServicio facturaServicio;
	@Autowired
	private EmisionesR emisionesR;
	@Autowired
	private LecturaServicio lecturaServicio;

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

	public Page<Abonados> findByIdruta(Long idruta, Pageable pageable) {
		return hydratePage(dao.findByIdruta(idruta, pageable));
	}

	// Abonados por Categoría
	public List<Abonados> findByIdcategoria(Long idcategoria) {
		return dao.findByIdcategoria(idcategoria);
	}

	public Page<Abonados> findByIdcategoria(Long idcategoria, Pageable pageable) {
		return hydratePage(dao.findByIdcategoria(idcategoria, pageable));
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

	public List<AbonadoGeoPreviewDto> findGeoPreviewByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}

		return dao.findPreviewByIds(ids).stream()
				.map(abonado -> new AbonadoGeoPreviewDto(
						abonado.getIdabonado(),
						abonado.getIdresponsable() != null ? abonado.getIdresponsable().getNombre()
								: abonado.getIdcliente_clientes() != null ? abonado.getIdcliente_clientes().getNombre()
								: "",
						abonado.getGeolocalizacion()))
				.collect(Collectors.toList());
	}

	public AbonadoGeoUploadResultDto aplicarGeoUploadMasivo(
			List<AbonadoGeoUploadItemDto> items,
			Long usumodi,
			String observacion,
			String tipo) {
		if (items == null || items.isEmpty()) {
			return new AbonadoGeoUploadResultDto(0, List.of(), List.of());
		}

		int actualizados = 0;
		List<String> errores = new ArrayList<>();
		List<AbonadoGeoUploadItemResultDto> detalles = new ArrayList<>();

		for (AbonadoGeoUploadItemDto item : items) {
			if (item == null || item.getIdabonado() == null) {
				String mensaje = "Cuenta desconocida: registro invalido.";
				errores.add(mensaje);
				detalles.add(new AbonadoGeoUploadItemResultDto(null, false, mensaje, null));
				continue;
			}

			try {
				actualizarGeolocalizacionConAuditoria(
						item.getIdabonado(),
						item.getGeolocalizacion(),
						usumodi,
						observacion,
						tipo);
				actualizados++;
				detalles.add(new AbonadoGeoUploadItemResultDto(
						item.getIdabonado(),
						true,
						"Actualizado correctamente.",
						item.getGeolocalizacion()));
			} catch (RuntimeException ex) {
				String mensaje = "Cuenta " + item.getIdabonado() + ": " + ex.getMessage();
				errores.add(mensaje);
				detalles.add(new AbonadoGeoUploadItemResultDto(
						item.getIdabonado(),
						false,
						mensaje,
						item.getGeolocalizacion()));
			} catch (Exception ex) {
				String mensaje = "Cuenta " + item.getIdabonado() + ": no se pudo actualizar.";
				errores.add(mensaje);
				detalles.add(new AbonadoGeoUploadItemResultDto(
						item.getIdabonado(),
						false,
						mensaje,
						item.getGeolocalizacion()));
			}
		}

		return new AbonadoGeoUploadResultDto(actualizados, errores, detalles);
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

	public Page<Abonados> findByEstado(Long estado, Pageable pageable) {
		return hydratePage(dao.findByEstado(estado, pageable));
	}

	public Page<Abonados> buscar(
			Long idruta,
			String responsable,
			Long estado,
			String cedula,
			Long cuenta,
			String ruta,
			Pageable pageable) {
		return hydratePage(dao.buscarConFiltros(
				idruta,
				responsable,
				estado,
				cedula,
				cuenta,
				ruta,
				pageable));
	}

	private Page<Abonados> hydratePage(Page<Abonados> page) {
		return page.map(abonado -> dao.findById(abonado.getIdabonado()).orElse(abonado));
	}

	public Abonados actualizarGeolocalizacionConAuditoria(Long idabonado, String geolocalizacion, Long usumodi, String observacion, String tipo) {
		Abonados abonadoOriginal = dao.findById(idabonado)
				.orElseThrow(() -> new RuntimeException("Abonado no encontrado: " + idabonado));

		AbonadosAuditDTO auditDTO = buildAuditDTO(abonadoOriginal);

		auditoriaService.saveAudit("abonados", abonadoOriginal.getIdabonado(), auditDTO, usumodi, observacion, tipo);

		abonadoOriginal.setGeolocalizacion(geolocalizacion);
		return dao.save(abonadoOriginal);
	}

	public Abonados actualizarAbonadoConAuditoria(Long idabonado, Abonados abonadosM, Long usumodi, String observacion, String tipo) {
		Abonados abonadoOriginal = dao.findById(idabonado)
				.orElseThrow(() -> new RuntimeException("Abonado no encontrado: " + idabonado));
		boolean cambioMedidor = !Objects.equals(abonadoOriginal.getNromedidor(), abonadosM.getNromedidor());
		boolean reiniciaLecturaInicial = abonadosM.getLecturainicial() != null && abonadosM.getLecturainicial() == 0;

		AbonadosAuditDTO auditDTO = buildAuditDTO(abonadoOriginal);

		auditoriaService.saveAudit("abonados", abonadoOriginal.getIdabonado(), auditDTO, usumodi, observacion, tipo);

		abonadoOriginal.setNromedidor(abonadosM.getNromedidor());
		abonadoOriginal.setLecturainicial(abonadosM.getLecturainicial());
		abonadoOriginal.setEstado(abonadosM.getEstado());
		abonadoOriginal.setFechainstalacion(abonadosM.getFechainstalacion());
		abonadoOriginal.setMarca(abonadosM.getMarca());
		abonadoOriginal.setSecuencia(abonadosM.getSecuencia());
		abonadoOriginal.setDireccionubicacion(abonadosM.getDireccionubicacion());
		abonadoOriginal.setLocalizacion(abonadosM.getLocalizacion());
		abonadoOriginal.setObservacion(abonadosM.getObservacion());
		abonadoOriginal.setDepartamento(abonadosM.getDepartamento());
		abonadoOriginal.setPiso(abonadosM.getPiso());
		abonadoOriginal.setIdresponsable(abonadosM.getIdresponsable());
		abonadoOriginal.setIdcategoria_categorias(abonadosM.getIdcategoria_categorias());
		abonadoOriginal.setIdruta_rutas(abonadosM.getIdruta_rutas());
		abonadoOriginal.setIdcliente_clientes(abonadosM.getIdcliente_clientes());
		abonadoOriginal.setIdubicacionm_ubicacionm(abonadosM.getIdubicacionm_ubicacionm());
		abonadoOriginal.setIdtipopago_tipopago(abonadosM.getIdtipopago_tipopago());
		abonadoOriginal.setIdestadom_estadom(abonadosM.getIdestadom_estadom());
		abonadoOriginal.setMedidorprincipal(abonadosM.getMedidorprincipal());
		abonadoOriginal.setUsucrea(abonadosM.getUsucrea());
		abonadoOriginal.setFeccrea(abonadosM.getFeccrea());
		abonadoOriginal.setUsumodi(usumodi);
		abonadoOriginal.setFecmodi(abonadosM.getFecmodi());
		abonadoOriginal.setAdultomayor(abonadosM.getAdultomayor());
		abonadoOriginal.setMunicipio(abonadosM.getMunicipio());
		abonadoOriginal.setSwalcantarillado(abonadosM.getSwalcantarillado());
		abonadoOriginal.setPromedio(abonadosM.getPromedio());
		abonadoOriginal.setGeolocalizacion(abonadosM.getGeolocalizacion());
		abonadoOriginal.setSwbasura(abonadosM.getSwbasura());
		abonadoOriginal.setFotocasaPath(abonadosM.getFotocasaPath());
		abonadoOriginal.setFotomedidorPath(abonadosM.getFotomedidorPath());

		Abonados abonadoActualizado = dao.save(abonadoOriginal);

		if (cambioMedidor && reiniciaLecturaInicial) {
			reiniciarLecturaAnteriorEnEmisionAbierta(abonadoActualizado, usumodi, observacion, tipo);
		}

		return abonadoActualizado;
	}

	private void reiniciarLecturaAnteriorEnEmisionAbierta(Abonados abonado, Long usumodi, String observacion, String tipo) {
		Emisiones emisionAbierta = emisionesR.findFirstByEstadoOrderByEmisionDesc(0);
		if (emisionAbierta == null || abonado.getIdabonado() == null) {
			return;
		}

		List<Lecturas> lecturas = lecturaServicio.findByIdemisionIdAbonado(emisionAbierta.getIdemision(), abonado.getIdabonado());
		if (lecturas == null || lecturas.isEmpty()) {
			return;
		}

		Lecturas lecturaActual = lecturas.stream()
				.filter(lectura -> lectura != null && lectura.getEstado() != null && lectura.getEstado() == 0)
				.findFirst()
				.orElse(null);
		if (lecturaActual == null) {
			return;
		}

		lecturaActual.setLecturaanterior(0F);
		String observacionLectura = (observacion == null || observacion.isBlank())
				? "CAMBIO DE MEDIDOR - REINICIO LECTURA ANTERIOR EN EMISION ABIERTA"
				: observacion;
		String tipoAuditoria = (tipo == null || tipo.isBlank()) ? "MODIFICACION" : tipo;
		lecturaServicio.actualizarLecturaConAuditoria(
				lecturaActual.getIdlectura(),
				lecturaActual,
				usumodi,
				observacionLectura,
				tipoAuditoria);
	}

	public Abonados actualizarFotosAbonadoConAuditoria(Long idabonado, String fotocasaPath, String fotomedidorPath,
			Long usumodi, String observacion, String tipo) {
		Abonados abonadoOriginal = dao.findById(idabonado)
				.orElseThrow(() -> new RuntimeException("Abonado no encontrado: " + idabonado));

		AbonadosAuditDTO auditDTO = buildAuditDTO(abonadoOriginal);
		auditoriaService.saveAudit("abonados", abonadoOriginal.getIdabonado(), auditDTO, usumodi, observacion, tipo);

		if (fotocasaPath != null) {
			abonadoOriginal.setFotocasaPath(fotocasaPath);
		}
		if (fotomedidorPath != null) {
			abonadoOriginal.setFotomedidorPath(fotomedidorPath);
		}
		abonadoOriginal.setUsumodi(usumodi);
		return dao.save(abonadoOriginal);
	}

	private AbonadosAuditDTO buildAuditDTO(Abonados abonadoOriginal) {
		return new AbonadosAuditDTO(
				abonadoOriginal.getIdabonado(),
				abonadoOriginal.getNromedidor(),
				abonadoOriginal.getLecturainicial(),
				abonadoOriginal.getEstado(),
				abonadoOriginal.getFechainstalacion(),
				abonadoOriginal.getMarca(),
				abonadoOriginal.getSecuencia(),
				abonadoOriginal.getDireccionubicacion(),
				abonadoOriginal.getLocalizacion(),
				abonadoOriginal.getObservacion(),
				abonadoOriginal.getDepartamento(),
				abonadoOriginal.getPiso(),
				abonadoOriginal.getIdresponsable() == null ? null : abonadoOriginal.getIdresponsable().getIdcliente(),
				abonadoOriginal.getIdcategoria_categorias() == null ? null : abonadoOriginal.getIdcategoria_categorias().getIdcategoria(),
				abonadoOriginal.getIdruta_rutas() == null ? null : abonadoOriginal.getIdruta_rutas().getIdruta(),
				abonadoOriginal.getIdcliente_clientes() == null ? null : abonadoOriginal.getIdcliente_clientes().getIdcliente(),
				abonadoOriginal.getIdubicacionm_ubicacionm() == null ? null : abonadoOriginal.getIdubicacionm_ubicacionm().getIdubicacionm(),
				abonadoOriginal.getIdtipopago_tipopago() == null ? null : abonadoOriginal.getIdtipopago_tipopago().getIdtipopago(),
				abonadoOriginal.getIdestadom_estadom() == null ? null : abonadoOriginal.getIdestadom_estadom().getIdestadom(),
				abonadoOriginal.getMedidorprincipal(),
				abonadoOriginal.getUsucrea(),
				abonadoOriginal.getFeccrea(),
				abonadoOriginal.getUsumodi(),
				abonadoOriginal.getFecmodi(),
				abonadoOriginal.getAdultomayor(),
				abonadoOriginal.getMunicipio(),
				abonadoOriginal.getSwalcantarillado(),
				abonadoOriginal.getPromedio(),
				abonadoOriginal.getGeolocalizacion(),
				abonadoOriginal.getSwbasura(),
				abonadoOriginal.getFotocasaPath(),
				abonadoOriginal.getFotomedidorPath());
	}

	/*
	 * =============================================================
	 * SERVICIOS PARA MOBILE
	 * =============================================================
	 */
	public List<AbonadosMobile> getAllAbonadosMobile() {
		return dao.getAllAbonadosMobile();
	}

	public List<AbonadosMobile> getAbonadosMobileByRutas(List<Long> idrutas) {
		if (idrutas == null || idrutas.isEmpty()) {
			return java.util.Collections.emptyList();
		}
		return dao.getAbonadosMobileByRutas(idrutas);
	}
}
