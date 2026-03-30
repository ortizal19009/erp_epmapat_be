/* package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.contabilidad.CuentasReporte;
import com.epmapat.erp_epmapat.DTO.contabilidad.SaldoCuentasDTO;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.jasperReports.DTO.JasperBeanDTO;
import com.epmapat.erp_epmapat.jasperReports.DTO.JasperDatasetDTO;
import com.epmapat.erp_epmapat.jasperReports.DTO.ReportParameterDTO;
import com.epmapat.erp_epmapat.jasperReports.services.ReporteBeanService;
import com.epmapat.erp_epmapat.jasperReports.services.ReporteDatasetService;
import com.epmapat.erp_epmapat.jasperReports.services.ReporteExportService;
import com.epmapat.erp_epmapat.modelo.contabilidad.Cuentas;
import com.epmapat.erp_epmapat.servicio.contabilidad.CuentaServicio;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperPrint;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor

public class CuentasApi {

	private final CuentaServicio cueServicio;
	private final ReporteDatasetService reporteDatasetService;
	private final ReporteBeanService reporteBeanService;
	private final ReporteExportService exportService;

	@GetMapping
	public List<Cuentas> getAllLista(
			@Param(value = "nomcue") String nomcue,
			@Param(value = "codcue") String codcue,
			@Param(value = "grucue") String grucue,
			@Param(value = "asohaber") String asohaber,
			@Param(value = "asodebe") String asodebe) {

		if (codcue != null) {
			return cueServicio.findByCodcue(codcue);
		} else {
			if (nomcue != null) {
				return cueServicio.findByNomcue(nomcue.toLowerCase());
			} else {
				if (grucue != null) {
					return cueServicio.findByGrucue(grucue);
				} else {
					if (asohaber != null) {
						return cueServicio.findByAsohaber(asohaber);
					} else {
						if (asodebe != null) {
							return cueServicio.findByAsodebe(asodebe);
						} else
							return cueServicio.findAll();
					}
				}
			}
		}
	}

	// Lista de cuentas por codigo y/o nombre
	@GetMapping("/lista")
	public List<Cuentas> getAllLista(@Param(value = "codcue") String codcue,
			@Param(value = "nomcue") String nomcue) {
		if (codcue != null && nomcue != null) {
			return cueServicio.findByCodigoyNombre(codcue, nomcue.toLowerCase());
		} else
			return null;
	}

	@GetMapping("/bancos")
	public ResponseEntity<List<Cuentas>> getBancos() {
		return ResponseEntity.ok(cueServicio.findBancos());
	}

	// Cuentas por Tiptran para los DataList de Cuentas
	@GetMapping("/porTiptran")
	public List<Cuentas> getByTiptran(@Param(value = "tiptran") Integer tiptran,
			@Param(value = "codcue") String codcue) {
		return cueServicio.findByTiptran(tiptran, codcue + '%');
	}

	@GetMapping("/nombre/{codcue}")
	public Object[] getNombre(@PathVariable("codcue") String codcue) {
		return cueServicio.getNomCueByCodcue(codcue);
	}

	// Valida codcue
	@GetMapping("/valcodcue")
	public ResponseEntity<Boolean> valCodcue(@Param(value = "codcue") String codcue) {
		boolean rtn = cueServicio.valCodcue(codcue);
		return ResponseEntity.ok(rtn);
	}

	// Valida el nombre de la Cuenta
	@GetMapping("/valnomcue")
	public ResponseEntity<Boolean> valNomcue(@Param(value = "nomcue") String nomcue) {
		boolean rtn = cueServicio.valNomcue(nomcue.toLowerCase());
		return ResponseEntity.ok(rtn);
	}

	// Verifica si tiene Desagregación
	@GetMapping("/desagrega")
	public ResponseEntity<Boolean> valDesagrega(@Param(value = "codcue") String codcue,
			@Param(value = "nivcue") Integer nivcue) {
		boolean rtn = cueServicio.valDesagrega(codcue, nivcue);
		return ResponseEntity.ok(rtn);
	}

	// Busca una cuenta por codcue
	@GetMapping("/detalle")
	public Cuentas getDetalle(@Param(value = "codcue") String codcue) {
		return cueServicio.findCuentasByCodcue(codcue);
	}

	// Cuentas de costos
	@GetMapping("/cuecostos")
	public List<Cuentas> findCuecostos() {
		return cueServicio.findCuecostos();
	}

	@GetMapping("/{idcuenta}")
	public ResponseEntity<Cuentas> getByIddocumento(@PathVariable Long idcuenta) {
		Cuentas documento = cueServicio.findById(idcuenta)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe el Cuenta con Id: " + idcuenta)));
		return ResponseEntity.ok(documento);
	}

	// Cuentas asociadas a una partida

	@PostMapping
	public ResponseEntity<Cuentas> save(@RequestBody Cuentas x) {
		return ResponseEntity.ok(cueServicio.save(x));
	}

	@PutMapping("/{idcuenta}")
	public ResponseEntity<Cuentas> update(@PathVariable Long idcuenta, @RequestBody Cuentas x) {
		Cuentas y = cueServicio.findById(idcuenta)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe Cuenta con Id: " + idcuenta)));
		y.setCodcue(x.getCodcue());
		y.setNomcue(x.getNomcue());
		y.setGrucue(x.getGrucue());
		// y.setIdnivel(x.getIdnivel());
		y.setMovcue(x.getMovcue());
		y.setAsodebe(x.getAsodebe());
		y.setAsohaber(x.getAsohaber());
		y.setDebito(x.getDebito());
		y.setCredito(x.getCredito());
		y.setSaldo(x.getSaldo());
		y.setBalance(x.getBalance());
		y.setIntgrupo(x.getIntgrupo());
		y.setSigef(y.getSigef());
		y.setTiptran(x.getTiptran());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());
		y.setGrufluefec(x.getGrufluefec());
		y.setResulcostos(x.getResulcostos());
		y.setBalancostos(x.getBalancostos());

		Cuentas actualizar = cueServicio.save(y);
		return ResponseEntity.ok(actualizar);
	}

	@DeleteMapping("/{idcuenta}")
	private ResponseEntity<Boolean> deleteCuenta(@PathVariable("idcuenta") Long idcuenta) {
		cueServicio.deleteById(idcuenta);
		return ResponseEntity.ok(!(cueServicio.findById(idcuenta) != null));
	}

	// === REPORTES ===

	@PostMapping("/dataset")
	public ResponseEntity<byte[]> generaCuentas(@RequestBody JasperDatasetDTO<CuentasReporte> dto) {
		// System.out.println("Pasa");
		return generar(dto);
	}

	private <T> ResponseEntity<byte[]> generar(JasperDatasetDTO<T> dto) {
		try {
			byte[] reporteBytes = reporteDatasetService.generarReporte(dto);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(getMediaType(dto.getExtension()));
			headers.setContentDisposition(ContentDisposition.builder("inline")
					.filename(dto.getReportName() + "." + dto.getExtension())
					.build());

			return new ResponseEntity<>(reporteBytes, headers, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Reporte con método Coleccion de Beans
	@PostMapping("/reporte")
	public ResponseEntity<byte[]> generarReporte(@RequestBody JasperBeanDTO dto) {
		try {
			// Obtiene directamente del DTO
			String reportName = dto.getReportName();
			String extension = dto.getExtension() != null ? dto.getExtension() : "pdf";

			// Parámetros
			String codcue = "1";
			LocalDate Desde = LocalDate.now();
			LocalDate Hasta = LocalDate.now();

			for (ReportParameterDTO p : dto.getParameters()) {
				switch (p.getName()) {
					case "codcue":
						codcue = (String) p.getValue();
						break;
					case "Desde":
						Desde = LocalDate.parse((String) p.getValue());
						break;
					case "Hasta":
						Hasta = LocalDate.parse((String) p.getValue());
						break;
				}
			}
			// Convertir LocalDate → java.util.Date
			Date desdeDate = Date.from(Desde.atStartOfDay(ZoneId.systemDefault()).toInstant());
			Date hastaDate = Date.from(Hasta.atStartOfDay(ZoneId.systemDefault()).toInstant());

			dto.setParameter("Desde", desdeDate);
			dto.setParameter("Hasta", hastaDate);

			System.out.println("============== codcue en API: " + codcue);
			System.out.println("============== dto: " + dto);
			// System.out.println("Desde: " + Desde);

			List<SaldoCuentasDTO> lista = cueServicio.calcularSaldosPorCodcue(codcue, Desde, Hasta);
			dto.setBeanCollection(lista);
			JasperPrint jasperPrint = reporteBeanService.fillBeanReport(dto);
			ByteArrayOutputStream out = exportService.export(extension, jasperPrint);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(getMediaType(extension));
			headers.setContentDisposition(ContentDisposition.builder("inline")
					.filename(reportName + "." + extension)
					.build());

			return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private MediaType getMediaType(String extension) {
		if (extension == null)
			return MediaType.APPLICATION_OCTET_STREAM;

		switch (extension.toLowerCase()) {
			case "pdf":
				return MediaType.APPLICATION_PDF;
			case "xlsx":
				// application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
				return MediaType.parseMediaType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			case "csv":
				return MediaType.parseMediaType("text/csv");
			default:
				// Fallback genérico
				return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

}
 */