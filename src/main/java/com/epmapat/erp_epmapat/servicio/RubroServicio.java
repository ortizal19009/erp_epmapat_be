package com.epmapat.erp_epmapat.servicio;

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

import com.epmapat.erp_epmapat.modelo.Rubros;
import com.epmapat.erp_epmapat.repositorio.RubrosR;

@Service
public class RubroServicio {

   @Autowired
   private RubrosR dao;

   public List<Rubros> findByIdmodulo(Long idmodulo) {
      return dao.findByIdmodulo(idmodulo);
   }

   public List<Rubros> findEmision() {
      return dao.findEmision();
   }

   public List<Rubros> findAll() {
      return dao.findAll();
   }

   public List<Rubros> findByNombre(Long idmodulo, String descripcion) {
      return dao.findByNombre(idmodulo, descripcion);
   }

   public List<Rubros> findByModulo(Long idmodulo, String descripcion) {
      return dao.findByModulo(idmodulo, descripcion);
   }

   public Optional<Rubros> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends Rubros> S save(S entity) {
      return dao.save(entity);
   }

   public List<Rubros> findAll(Sort sort) {
      return null;
   }

   public List<Rubros> findAllById(Iterable<Long> ids) {
      return null;
   }

   public <S extends Rubros> List<S> saveAll(Iterable<S> entities) {
      return null;
   }

   public void flush() {
   }

   public <S extends Rubros> S saveAndFlush(S entity) {
      return null;
   }

   public <S extends Rubros> List<S> saveAllAndFlush(Iterable<S> entities) {
      return null;
   }

   public void deleteAllInBatch(Iterable<Rubros> entities) {
   }

   public void deleteAllByIdInBatch(Iterable<Long> ids) {
   }

   public void deleteAllInBatch() {
   }

   public Rubros getOne(Long id) {
      return null;
   }

   public Rubros getById(Long id) {
      return null;
   }

   public Rubros getReferenceById(Long id) {
      return null;
   }

   public <S extends Rubros> List<S> findAll(Example<S> example) {

      return null;
   }

   public <S extends Rubros> List<S> findAll(Example<S> example, Sort sort) {

      return null;
   }

   public Page<Rubros> findAll(Pageable pageable) {

      return null;
   }

   public boolean existsById(Long id) {

      return false;
   }

   public long count() {

      return 0;
   }

   public void deleteById(Long id) {

   }

   public void delete(Rubros entity) {

   }

   public void deleteAllById(Iterable<? extends Long> ids) {

   }

   public void deleteAll(Iterable<? extends Rubros> entities) {

   }

   public void deleteAll() {

   }

   public <S extends Rubros> Optional<S> findOne(Example<S> example) {

      return Optional.empty();
   }

   public <S extends Rubros> Page<S> findAll(Example<S> example, Pageable pageable) {

      return null;
   }

   public <S extends Rubros> long count(Example<S> example) {

      return 0;
   }

   public <S extends Rubros> boolean exists(Example<S> example) {

      return false;
   }

   public <S extends Rubros, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {

      return null;
   }

   public Rubros findByIdRubro(Long idrubro) {
      return dao.findByIdRubro(idrubro);
   }

   public List<Rubros> findByName(String descripcion) {
      return dao.findByName(descripcion);
   }
}
