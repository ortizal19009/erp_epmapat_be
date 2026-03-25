package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.config.AESUtil;
import com.epmapat.erp_epmapat.interfaces.CVClientes;
import com.epmapat.erp_epmapat.DTO.ClientesAuditDTO;
import com.epmapat.erp_epmapat.interfaces.ClienteDuplicadoGrupoView;
import com.epmapat.erp_epmapat.interfaces.ClienteDuplicadoView;
import com.epmapat.erp_epmapat.interfaces.mobile.ClientesMobile;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.repositorio.ClientesR;

@Service
public class ClienteServicio {

	@Autowired
	private ClientesR dao;

	@Autowired
	private AuditoriaGenericaService auditoriaService;

	// Campos: id y nombre
	public List<Map<String, Object>> obtenerCampos() {
		return dao.findAllClientsFields();
	}

	// Buscar Clientes por Nombre o Identificacion
	public List<Clientes> findByNombreIdentifi(String nombreIdentifi) {
		return dao.findByNombreIdentifi(nombreIdentifi);
	}

	// Buscar Clientes por Identificacion
	public List<Clientes> findByIdentificacion(String identificacion) {
		return dao.findByIdentificacion(identificacion);
	}

	// Buscar Clientes por Nombre
	public List<Clientes> findByNombre(String nombre) {
		return dao.findByNombre(nombre);
	}

	// Valida Identificacion
	public boolean valIdentificacion(String nombre) {
		return dao.valIdentificacion(nombre);
	}

	// Valida Nombre
	public boolean valNombre(String nombre) {
		return dao.valNombre(nombre);
	}

	public <S extends Clientes> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Clientes> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteByIdQ(id);
	}

	public List<Clientes> used(Long id) {
		return dao.used(id);
	}

	public Long totalclientes() {
		return dao.totalClientes();
	}

	public List<CVClientes> getCVByCliente(LocalDate fecha) {
		return dao.getCVByCliente(fecha);
	}

	public Page<CVClientes> getCVOfClientes(LocalDate fecha, String name, int page, int size) {
		// Validación defensiva para evitar índices negativos
		if (page < 0) {
			page = 0;
		}

		Pageable pageable = PageRequest.of(page, size);

		if (name == null || name.isEmpty()) {
			return dao.getCVOfClientes(fecha, pageable);
		} else {
			return dao.getCVOfNCliente(fecha, name, pageable);
		}
	}

	public void actualizarCredenciales(Long idcliente, String username, String password) throws Exception {
		Clientes c = dao.findById(idcliente)
				.orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

		c.setUsername(username);
		c.setPassword(AESUtil.cifrar(password)); // usando tu AESUtil

		dao.save(c);
	}

	public Page<ClienteDuplicadoView> listarDuplicados(Pageable pageable) {
		return dao.findDuplicados(pageable);
	}

	public Page<ClienteDuplicadoGrupoView> findDuplicadosAgrupados(Pageable pageable) {
		return dao.findDuplicadosAgrupados(pageable);
	}

	public Clientes actualizarClienteConAuditoria(Clientes cliente, Long usumodi, String observacion, String tipo) {
		if (cliente.getIdcliente() == null) {
			throw new IllegalArgumentException("El idcliente es requerido");
		}

		Clientes clienteOriginal = dao.findById(cliente.getIdcliente())
				.orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + cliente.getIdcliente()));

		// Generar DTO de auditoría
		ClientesAuditDTO auditDTO = new ClientesAuditDTO(
				clienteOriginal.getIdcliente(),
				clienteOriginal.getCedula(),
				clienteOriginal.getIdtpidentifica_tpidentifica() == null ? null : clienteOriginal.getIdtpidentifica_tpidentifica().getIdtpidentifica(),
				clienteOriginal.getNombre(),
				clienteOriginal.getDireccion(),
				clienteOriginal.getTelefono(),
				clienteOriginal.getFechanacimiento(),
				clienteOriginal.getDiscapacitado(),
				clienteOriginal.getPorcdiscapacidad(),
				clienteOriginal.getPorcexonera(),
				clienteOriginal.getEstado(),
				clienteOriginal.getEmail(),
				clienteOriginal.getUsucrea(),
				clienteOriginal.getIdnacionalidad_nacionalidad() == null ? null : clienteOriginal.getIdnacionalidad_nacionalidad().getIdnacionalidad(),
				clienteOriginal.getFeccrea(),
				clienteOriginal.getUsumodi(),
				clienteOriginal.getFecmodi(),
				clienteOriginal.getIdpjuridica_personeriajuridica() == null ? null : clienteOriginal.getIdpjuridica_personeriajuridica().getIdpjuridica(),
				clienteOriginal.getUsername(),
				clienteOriginal.getActivo(),
				clienteOriginal.getPassword(),
				clienteOriginal.getRol());

		auditoriaService.saveAudit("clientes", clienteOriginal.getIdcliente(), auditDTO, usumodi, observacion, tipo);

		// Actualizar campos
		if (cliente.getCedula() != null) clienteOriginal.setCedula(cliente.getCedula());
		if (cliente.getNombre() != null) clienteOriginal.setNombre(cliente.getNombre());
		if (cliente.getDireccion() != null) clienteOriginal.setDireccion(cliente.getDireccion());
		if (cliente.getTelefono() != null) clienteOriginal.setTelefono(cliente.getTelefono());
		if (cliente.getFechanacimiento() != null) clienteOriginal.setFechanacimiento(cliente.getFechanacimiento());
		if (cliente.getDiscapacitado() != null) clienteOriginal.setDiscapacitado(cliente.getDiscapacitado());
		if (cliente.getPorcdiscapacidad() != null) clienteOriginal.setPorcdiscapacidad(cliente.getPorcdiscapacidad());
		if (cliente.getPorcexonera() != null) clienteOriginal.setPorcexonera(cliente.getPorcexonera());
		if (cliente.getEstado() != null) clienteOriginal.setEstado(cliente.getEstado());
		if (cliente.getEmail() != null) clienteOriginal.setEmail(cliente.getEmail());
		if (cliente.getUsucrea() != null) clienteOriginal.setUsucrea(cliente.getUsucrea());
		if (cliente.getIdnacionalidad_nacionalidad() != null) clienteOriginal.setIdnacionalidad_nacionalidad(cliente.getIdnacionalidad_nacionalidad());
		if (cliente.getUsumodi() != null) clienteOriginal.setUsumodi(cliente.getUsumodi());
		if (cliente.getFecmodi() != null) clienteOriginal.setFecmodi(cliente.getFecmodi());
		if (cliente.getIdpjuridica_personeriajuridica() != null) clienteOriginal.setIdpjuridica_personeriajuridica(cliente.getIdpjuridica_personeriajuridica());
		if (cliente.getUsername() != null) clienteOriginal.setUsername(cliente.getUsername());
		if (cliente.getActivo() != null) clienteOriginal.setActivo(cliente.getActivo());
		if (cliente.getPassword() != null) clienteOriginal.setPassword(cliente.getPassword());
		if (cliente.getRol() != null) clienteOriginal.setRol(cliente.getRol());

		return dao.save(clienteOriginal);
	}


	public Page<ClienteDuplicadoGrupoView> listarDuplicadosFiltrados(String q, int page, int size) {

		// normaliza
		String filtro = (q == null) ? "" : q.trim();

		// pageable
		Pageable pageable = PageRequest.of(
				Math.max(page, 0),
				Math.max(size, 1),
				Sort.by(Sort.Direction.ASC, "cedula"));

		return dao.findDuplicadosAgrupadosFiltrado(filtro, pageable);
	}

	public List<Clientes> findAll() {
		return dao.findAll();
	}
	public List<ClientesMobile> getAllClientesMobile() {
		return dao.findAllBy();
	}

}
