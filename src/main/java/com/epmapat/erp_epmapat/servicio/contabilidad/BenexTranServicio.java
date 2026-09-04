package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.BenexTran;
import com.epmapat.erp_epmapat.repositorio.administracion.DocumentosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.BeneficiariosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.BenexTranR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PagoscobrosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TransaciR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BenexTranServicio {

	private final BenexTranR dao;
	private final TransaciR daoTransaci;
	private final BeneficiariosR daoBeneficiarios;
	private final DocumentosR daoDocumentos;
	private final PagoscobrosR daoPagoscobros;

	public List<BenexTran> findAll() {
		return dao.findAll();
	}

	public List<BenexTran> getEgresos(String codcue) {
		return dao.getEgresos(codcue);
	}

	public List<BenexTran> getByIdBene(Long idbene, LocalDate desde, LocalDate hasta) {
		return dao.getByIdBeneDesdeHasta(idbene, desde, hasta);
	}

	public List<BenexTran> getCxP() {
		return dao.getCxP();
	}

	// ACFP sin liquidar
	public List<BenexTran> getACFP(LocalDate hasta, String nomben, Integer tiptran, String codcue) {
		return dao.getACFP(hasta, nomben, tiptran, codcue);
	}

	// Verifica si un Beneficiario tiene benextran
	public boolean existeByIdbene(Long idbene) {
		return dao.existeByIdbene(idbene);
	}

	// BenexTran por idbenxtra
	public Optional<BenexTran> findById(Long id) {
		return dao.findById(id);
	}

	// BenexTran de una transaci.inttra
	public List<BenexTran> obtenerPorInttra(Long inttra) {
		return dao.findByInttra_Inttra(inttra);
	}

	// Cuenta los Benextran de una transaci.inttra
	public short countByInttra(Long inttra) {
		return dao.countByInttra_Inttra(inttra);
	}

	public <S extends BenexTran> S save(S entity) {
		return dao.save(entity);
	}

	// Guarda lote de registros
	@Transactional
	public List<BenexTran> guardarLote(List<BenexTran> lista) {
		List<BenexTran> registros = lista.stream().map(b -> {
			b.setInttra(daoTransaci.getReferenceById(b.getInttra().getInttra()));
			b.setIdbene(daoBeneficiarios.getReferenceById(b.getIdbene().getIdbene()));
			b.setIntdoc(daoDocumentos.getReferenceById(b.getIntdoc().getIntdoc()));
			return b;
		}).toList();
		return dao.saveAll(registros);
	}

	// Actualiza benextran.totpagcob
	public void actualizaTotPagcob(Long idbenxtra) {
		BigDecimal total = daoPagoscobros.totalPagosPorBenxtra(idbenxtra);
		BenexTran benxtra = dao.getReferenceById(idbenxtra);
		benxtra.setTotpagcob(total);
		dao.save(benxtra);
	}

	// Actualiza (solo los campos modificados)
	@Transactional
	public BenexTran updateBenextran(Long idbenxtra, BenexTran data) {
		BenexTran benextran = findById(idbenxtra)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						"No se encuentra este Id " + idbenxtra));
		// Coloca campos (solo los modificados)
		if (data.getIdbene() != null)
			benextran.setIdbene(data.getIdbene());
		if (data.getIntdoc() != null)
			benextran.setIntdoc(data.getIntdoc());
		if (data.getNumdoc() != null)
			benextran.setNumdoc(data.getNumdoc());
		if (data.getValor() != null) {
			benextran.setValor(data.getValor());
		}
		return dao.save(benextran);
	}

	// Antes de eliminar busca (otro usuario pudo eliminar)
	@Transactional
	public boolean deleteById(Long idbenxtra) {
		Optional<BenexTran> opt = dao.findById(idbenxtra);
		if (opt.isEmpty()) {
			return false; // No existe → 204 No Content
		}
		// Si existe, intenta eliminar
		dao.delete(opt.get());
		return true; // Eliminado → 200 OK
	}

}
