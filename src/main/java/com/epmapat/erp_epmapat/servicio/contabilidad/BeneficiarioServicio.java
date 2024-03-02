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
      

   }

   @Override
   public <S extends Beneficiarios> S saveAndFlush(S entity) {
      return null;
   }

   @Override
   public <S extends Beneficiarios> List<S> saveAllAndFlush(Iterable<S> entities) {
      
      return null;
   }

   @Override
   public void deleteAllInBatch(Iterable<Beneficiarios> entities) {
      

   }

   @Override
   public void deleteAllByIdInBatch(Iterable<Long> ids) {
      

   }

   @Override
   public void deleteAllInBatch() {
      

   }

   @Override
   public Beneficiarios getOne(Long id) {
      
      return null;
   }

   @Override
   public Beneficiarios getById(Long id) {
      
      return null;
   }

   @Override
   public Beneficiarios getReferenceById(Long id) {
      
      return null;
   }

   @Override
   public <S extends Beneficiarios> List<S> findAll(Example<S> example) {
      
      return null;
   }

   @Override
   public <S extends Beneficiarios> List<S> findAll(Example<S> example, Sort sort) {
      
      return null;
   }

   @Override
   public Page<Beneficiarios> findAll(Pageable pageable) {
      
      return null;
   }

   @Override
   public <S extends Beneficiarios> S save(S entity) {
      
      return null;
   }

   @Override
   public Optional<Beneficiarios> findById(Long id) {
      
      return beneficiariosR.findById(id);
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
   public void deleteById(Long id) {
      

   }

   @Override
   public void delete(Beneficiarios entity) {
      

   }

   @Override
   public void deleteAllById(Iterable<? extends Long> ids) {
      

   }

   @Override
   public void deleteAll(Iterable<? extends Beneficiarios> entities) {
      

   }

   @Override
   public void deleteAll() {
      

   }

   @Override
   public <S extends Beneficiarios> Optional<S> findOne(Example<S> example) {
      
      return Optional.empty();
   }

   @Override
   public <S extends Beneficiarios> Page<S> findAll(Example<S> example, Pageable pageable) {
      
      return null;
   }

   @Override
   public <S extends Beneficiarios> long count(Example<S> example) {
      
      return 0;
   }

   @Override
   public <S extends Beneficiarios> boolean exists(Example<S> example) {
      
      return false;
   }

   @Override
   public <S extends Beneficiarios, R> R findBy(Example<S> example,
         Function<FetchableFluentQuery<S>, R> queryFunction) {
      
      return null;
   }

}
