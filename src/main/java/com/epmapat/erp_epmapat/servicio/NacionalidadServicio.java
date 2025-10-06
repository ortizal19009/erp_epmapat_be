package com.epmapat.erp_epmapat.servicio;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.epmapat.erp_epmapat.modelo.Nacionalidad;
import com.epmapat.erp_epmapat.repositorio.NacionalidadR;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class NacionalidadServicio implements NacionalidadR {

	@Autowired
	private NacionalidadR dao;

	@Override
	public List<Nacionalidad> findAll() {
		return dao.findAll();
	}

	@Override
	public List<Nacionalidad> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	@Override
	public <S extends Nacionalidad> S save(S entity) {
		return dao.save(entity);
	}

	@Override
	public Optional<Nacionalidad> findById(Long id) {
		return dao.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	@Override
	public List<Nacionalidad> findAllById(Iterable<Long> ids) {

		return null;
	}

	@Override
	public <S extends Nacionalidad> List<S> saveAll(Iterable<S> entities) {

		return null;
	}

	@Override
	public <S extends Nacionalidad> S saveAndFlush(S entity) {

		return null;
	}

	@Override
	public <S extends Nacionalidad> List<S> saveAllAndFlush(Iterable<S> entities) {

		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Nacionalidad> entities) {

	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {

	}

	@Override
	public void deleteAllInBatch() {

	}

	@Override
	public Nacionalidad getOne(Long id) {

		return null;
	}

	@Override
	public Nacionalidad getById(Long id) {

		return null;
	}

	@Override
	public Nacionalidad getReferenceById(Long id) {

		return null;
	}

	@Override
	public <S extends Nacionalidad> List<S> findAll(Example<S> example) {

		return null;
	}

	@Override
	public <S extends Nacionalidad> List<S> findAll(Example<S> example, Sort sort) {

		return null;
	}

	@Override
	public Page<Nacionalidad> findAll(Pageable pageable) {

		return null;
	}

	@Override
	public boolean existsById(Long id) {

		return false;
	}

	@Override
	public long count() {

		return 0;
	}

	@Override
	public void delete(Nacionalidad entity) {

		dao.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {

	}

	@Override
	public void deleteAll(Iterable<? extends Nacionalidad> entities) {

	}

	@Override
	public void deleteAll() {

	}

	@Override
	public <S extends Nacionalidad> Optional<S> findOne(Example<S> example) {

		return null;
	}

	@Override
	public <S extends Nacionalidad> Page<S> findAll(Example<S> example, Pageable pageable) {

		return null;
	}

	@Override
	public <S extends Nacionalidad> long count(Example<S> example) {

		return 0;
	}

	@Override
	public <S extends Nacionalidad> boolean exists(Example<S> example) {

		return false;
	}

	@Override
	public <S extends Nacionalidad, R> R findBy(Example<S> example,
			Function<FetchableFluentQuery<S>, R> queryFunction) {

		return null;
	}

	@Override
	public void flush() {
	}

	@Override
	public void deleteByIdQ(Long id) {

	}

	@Override
	public List<Nacionalidad> used(Long id) {

		return null;
	}

	// public List<NacionalidadM> findByDescription(String descripcion) {
	//
	// return null;
	// }
	@Override
	public List<Nacionalidad> findByDescription(String nombre) {
		return dao.findByDescription(nombre);
	}

	public String exportNacionalidades(String format) throws FileNotFoundException, JRException {
		List<Nacionalidad> nacionalidad = dao.findAll();
		String path = "C://reportes//";
		File file = ResourceUtils.getFile("classpath:nacionalidades.jrxml");
		JasperReport jasper = JasperCompileManager.compileReport(file.getAbsolutePath());
		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(nacionalidad);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("Key_nacionalidades", "knowledge");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, parameters, ds);
		if (format.equalsIgnoreCase("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "//nacionalidades.html");
		}
		if (format.equalsIgnoreCase("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrint, path + "//nacionalidades.pdf");
		}
		return "path: " + path;
	}

}
