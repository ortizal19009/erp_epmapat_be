package com.epmapat.erp_epmapat.controlador;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.DTO.ClienteMergeRequest;
import com.epmapat.erp_epmapat.DTO.CredencialesRequest;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.CVClientes;
import com.epmapat.erp_epmapat.interfaces.ClienteDuplicadoGrupoView;
import com.epmapat.erp_epmapat.interfaces.ClienteDuplicadoView;
import com.epmapat.erp_epmapat.interfaces.mobile.ClientesMobile;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.servicio.ClienteMergeService;
import com.epmapat.erp_epmapat.servicio.ClienteServicio;

@RestController
@RequestMapping("/clientes")
public class ClientesApi {

	@Autowired
	private ClienteServicio cliServicio;
	@Autowired
	private ClienteMergeService cliMergeServicio;

	@GetMapping
	public List<Clientes> getAllClientes(@Param(value = "identificacion") String identificacion,
			@Param(value = "nombre") String nombre, @Param(value = "nombreIdentifi") String nombreIdentifi,
			@Param(value = "idused") Long idused) {
		if (nombreIdentifi != null) {
			return cliServicio.findByNombreIdentifi(nombreIdentifi.toLowerCase());
		}
		if (identificacion != null) {
			return cliServicio.findByIdentificacion(identificacion);
		}
		if (nombre != null) {
			return cliServicio.findByNombre(nombre.toLowerCase());
		}
		if (idused != null) {
			return cliServicio.used(idused);
		} else
			return null;
	}

	@GetMapping("/one")
	public ResponseEntity<Clientes> getByIdCliente(@RequestParam Long idcliente) {
		Clientes clienteM = cliServicio.findById(idcliente)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe ese cliente con ese Id: " + idcliente)));
		return ResponseEntity.ok(clienteM);
	}

	// Valida Identificación del Cliente
	@GetMapping("/valIdentificacion")
	public ResponseEntity<Boolean> valIdentificacion(@Param(value = "identificacion") String identificacion) {
		boolean rtn = cliServicio.valIdentificacion(identificacion);
		return ResponseEntity.ok(rtn);
	}

	// Valida nomre de Cliente
	@GetMapping("/valNombre")
	public ResponseEntity<Boolean> valNombre(@Param(value = "nombre") String nombre) {
		boolean rtn = cliServicio.valNombre(nombre.toLowerCase());
		return ResponseEntity.ok(rtn);
	}

	@GetMapping("/campos")
	public List<Map<String, Object>> obtenerTodosLosCampos() {
		return cliServicio.obtenerCampos();
	}

	@PostMapping("/mobile/upload")
	public ResponseEntity<Map<String, Integer>> uploadClientesMobile(
			@RequestBody List<Clientes> items,
			@RequestParam Long usumodi,
			@RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
			@RequestParam(required = false, defaultValue = "Upload Clientes") String observacion) {
		int ok = 0;
		int err = 0;

		for (Clientes item : items) {
			try {
				if (item.getIdcliente() == null) {
					throw new IllegalArgumentException("idcliente requerido");
				}
				cliServicio.actualizarClienteConAuditoria(item, usumodi, observacion, tipo);
				ok++;
			} catch (Exception e) {
				err++;
			}
		}

		Map<String, Integer> out = new java.util.HashMap<>();
		out.put("ok", ok);
		out.put("error", err);
		out.put("total", items.size());
		return ResponseEntity.ok(out);
	}

	@PostMapping
	public ResponseEntity<Clientes> saveClientes(@RequestBody Clientes clienteM) {
		boolean resp = validadorDeCedula(clienteM.getCedula());
		if (resp == true) {
			return ResponseEntity.ok(cliServicio.save(clienteM));
		} else {
			if (clienteM.getCedula().length() <= 13) {
				return ResponseEntity.ok(cliServicio.save(clienteM));
			} else {
				return ResponseEntity.notFound().build();
			}
		}
	}

	@PutMapping(value = "/{idcliente}")
	public ResponseEntity<Clientes> updateCliente(
			@PathVariable Long idcliente,
			@RequestBody Clientes clientem,
			@RequestParam Long usumodi,
			@RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
			@RequestParam(required = false, defaultValue = "Actualización de cliente") String observacion) {

		if (!idcliente.equals(clientem.getIdcliente())) {
			throw new IllegalArgumentException("El idcliente de la ruta y del body deben coincidir");
		}

		Clientes updated = cliServicio.actualizarClienteConAuditoria(clientem, usumodi, observacion, tipo);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping(value = "/{idcliente}")
	public ResponseEntity<Object> deleteCliente(@PathVariable("idcliente") Long idcliente) {
		cliServicio.deleteById(idcliente);
		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping("/total")
	public ResponseEntity<Long> getTotalClientes() {
		return ResponseEntity.ok(cliServicio.totalclientes());
	}

	public boolean validadorDeCedula(String cedula) {
		boolean cedulaCorrecta = false;

		try {

			if (cedula.length() == 10) // ConstantesApp.LongitudCedula
			{
				int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
				if (tercerDigito < 6) {
					// Coeficientes de validación cédula
					// El decimo digito se lo considera dígito verificador
					int[] coefValCedula = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
					int verificador = Integer.parseInt(cedula.substring(9, 10));
					int suma = 0;
					int digito = 0;
					for (int i = 0; i < (cedula.length() - 1); i++) {
						digito = Integer.parseInt(cedula.substring(i, i + 1)) * coefValCedula[i];
						suma += ((digito % 10) + (digito / 10));
					}

					if ((suma % 10 == 0) && (suma % 10 == verificador)) {
						cedulaCorrecta = true;
					} else if ((10 - (suma % 10)) == verificador) {
						cedulaCorrecta = true;
					} else {
						cedulaCorrecta = false;
					}
				} else {
					cedulaCorrecta = false;
				}
			} else {
				cedulaCorrecta = false;
			}
		} catch (NumberFormatException nfe) {
			cedulaCorrecta = false;
		} catch (Exception err) {
			cedulaCorrecta = false;
		}

		if (!cedulaCorrecta) {
		}
		return cedulaCorrecta;
	}

	@GetMapping("/reportes/carteravencida")
	ResponseEntity<List<CVClientes>> getCVByCliente(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
		return ResponseEntity.ok(cliServicio.getCVByCliente(fecha));
	}

	@GetMapping("/cartera/clientes")
	public ResponseEntity<Page<CVClientes>> getCVOfClientes(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
			@RequestParam String name,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		if (page < 0)
			page = 0; // 👈 asegúrate que no sea negativo
		Page<CVClientes> result = cliServicio.getCVOfClientes(fecha, name.toLowerCase(), page, size);
		return ResponseEntity.ok(result);
	}

	@PutMapping("/{id}/credenciales")
	public ResponseEntity<Void> actualizarCredenciales(
			@PathVariable("id") Long id,
			@RequestBody CredencialesRequest req) throws Exception {

		cliServicio.actualizarCredenciales(id, req.getUsername(), req.getPassword());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/duplicados")
	public Page<ClienteDuplicadoView> listarDuplicados(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return cliServicio.listarDuplicados(PageRequest.of(page, size));
	}

	// @GetMapping("/duplicados-agrupado")
	public Page<ClienteDuplicadoGrupoView> listarGrupos(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return cliServicio.findDuplicadosAgrupados(PageRequest.of(page, size));
	}

	@GetMapping("/duplicados-agrupado")
	public ResponseEntity<Page<ClienteDuplicadoGrupoView>> duplicadosFiltrados(
			@RequestParam(defaultValue = "") String q,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		if (q.isEmpty()) {
			return ResponseEntity.ok(cliServicio.findDuplicadosAgrupados(PageRequest.of(page, size)));
		}

		return ResponseEntity.ok(cliServicio.listarDuplicadosFiltrados(q, page, size));
	}

	@PostMapping("/merge")
	public ResponseEntity<Void> mergeClientes(
			@RequestBody ClienteMergeRequest req,
			Authentication auth) {

		// Long usuario = auth.getUsername();
		cliMergeServicio.merge(
				req.getMasterId(),
				req.getDuplicateIds(),
				req.getUsuario());

		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<List<Clientes>> getAllClientes() {
		return ResponseEntity.ok(cliServicio.findAll());
	}
	@GetMapping("/all-mobile")
	public ResponseEntity<List<ClientesMobile>> getAllClientesMobile() {
		return ResponseEntity.ok(cliServicio.getAllClientesMobile());
	}
}
