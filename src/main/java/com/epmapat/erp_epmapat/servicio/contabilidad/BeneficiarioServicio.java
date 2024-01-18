package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;
import com.epmapat.erp_epmapat.repositorio.contabilidad.BeneficiariosR;

@Service
public class BeneficiarioServicio implements BeneficiariosR {

   @Autowired
   private BeneficiariosR beneficiariosR;

   @Override
   public List<Beneficiarios> findAll() {
      return beneficiariosR.findAll();
   }

   @Override
   public List<Beneficiarios> findByName(String name) {
      return beneficiariosR.findByName(name);
   }

   @Override
   public List<Beneficiarios> findByGrupoBene(String name, Long idgrupo) {
      return beneficiariosR.findByGrupoBene(name, idgrupo);
   }

   @Override
   public List<Beneficiarios> findAll(Sort sort) {
      return null;
   }

   @Override
   public List<Beneficiarios> findAllById(Iterable<Long> ids) {
      return null;
   }

   @Override
   public <S extends Beneficiarios> List<S> saveAll(Iterable<S> entities) {
      return null;
   }

   @Override
   public void flush() {
      // TODO Auto-generated method stub

   }

   @Override
   public <S extends Beneficiarios> S saveAndFlush(S entity) {
      return null;
   }

   @Override
   public <S extends Beneficiarios> List<S> saveAllAndFlush(Iterable<S> entities) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void deleteAllInBatch(Iterable<Beneficiarios> entities) {
      // TODO Auto-generated method stub

   }

   @Override
   public void deleteAllByIdInBatch(Iterable<Long> ids) {
      // TODO Auto-generated method stub

   }

   @Override
   public void deleteAllInBatch() {
      // TODO Auto-generated method stub

   }

   @Override
   public Beneficiarios getOne(Long id) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Beneficiarios getById(Long id) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Beneficiarios getReferenceById(Long id) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <S extends Beneficiarios> List<S> findAll(Example<S> example) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <S extends Beneficiarios> List<S> findAll(Example<S> example, Sort sort) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Page<Beneficiarios> findAll(Pageable pageable) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <S extends Beneficiarios> S save(S entity) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Optional<Beneficiarios> findById(Long id) {
      // TODO Auto-generated method stub
      return beneficiariosR.findById(id);
   }

   @Override
   public boolean existsById(Long id) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public long count() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public void deleteById(Long id) {
      // TODO Auto-generated method stub

   }

   @Override
   public void delete(Beneficiarios entity) {
      // TODO Auto-generated method stub

   }

   @Override
   public void deleteAllById(Iterable<? extends Long> ids) {
      // TODO Auto-generated method stub

   }

   @Override
   public void deleteAll(Iterable<? extends Beneficiarios> entities) {
      // TODO Auto-generated method stub

   }

   @Override
   public void deleteAll() {
      // TODO Auto-generated method stub

   }

   @Override
   public <S extends Beneficiarios> Optional<S> findOne(Example<S> example) {
      // TODO Auto-generated method stub
      return Optional.empty();
   }

   @Override
   public <S extends Beneficiarios> Page<S> findAll(Example<S> example, Pageable pageable) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <S extends Beneficiarios> long count(Example<S> example) {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public <S extends Beneficiarios> boolean exists(Example<S> example) {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public <S extends Beneficiarios, R> R findBy(Example<S> example,
         Function<FetchableFluentQuery<S>, R> queryFunction) {
      // TODO Auto-generated method stub
      return null;
   }

}
