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
public class RubroServicio implements RubrosR {

   @Autowired
   private RubrosR dao;

   @Override
   public List<Rubros> findByIdmodulo(Long idmodulo) {
      return dao.findByIdmodulo(idmodulo);
   }

   @Override
   public List<Rubros> findEmision() {
      return dao.findEmision();
   }

   @Override
   public List<Rubros> findAll() {
      return dao.findAll();
   }

   @Override
   public List<Rubros> findByNombre(Long idmodulo, String descripcion) {
      return dao.findByNombre(idmodulo, descripcion);
   }

   @Override
   public List<Rubros> findByModulo(Long idmodulo, String descripcion) {
      return dao.findByModulo(idmodulo, descripcion);
   }

   @Override
   public Optional<Rubros> findById(Long id) {
      return dao.findById(id);
   }

   @Override
   public <S extends Rubros> S save(S entity) {
      return dao.save(entity);
   }

   @Override
   public List<Rubros> findAll(Sort sort) {
      return null;
   }

   @Override
   public List<Rubros> findAllById(Iterable<Long> ids) {
      return null;
   }

   @Override
   public <S extends Rubros> List<S> saveAll(Iterable<S> entities) {
      return null;
   }

   @Override
   public void flush() {
   }

   @Override
   public <S extends Rubros> S saveAndFlush(S entity) {
      return null;
   }

   @Override
   public <S extends Rubros> List<S> saveAllAndFlush(Iterable<S> entities) {
      return null;
   }

   @Override
   public void deleteAllInBatch(Iterable<Rubros> entities) {
   }

   @Override
   public void deleteAllByIdInBatch(Iterable<Long> ids) {
   }

   @Override
   public void deleteAllInBatch() {
   }

   @Override
   public Rubros getOne(Long id) {
      return null;
   }

   @SuppressWarnings("null")
   @Override
   public Rubros getById(Long id) {
      return null;
   }

   @Override
   public Rubros getReferenceById(Long id) {
      return null;
   }

   @Override
   public <S extends Rubros> List<S> findAll(Example<S> example) {

      return null;
   }

   @Override
   public <S extends Rubros> List<S> findAll(Example<S> example, Sort sort) {

      return null;
   }

   @Override
   public Page<Rubros> findAll(Pageable pageable) {

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
   public void deleteById(Long id) {

   }

   @Override
   public void delete(Rubros entity) {

   }

   @Override
   public void deleteAllById(Iterable<? extends Long> ids) {

   }

   @Override
   public void deleteAll(Iterable<? extends Rubros> entities) {

   }

   @Override
   public void deleteAll() {

   }

   @Override
   public <S extends Rubros> Optional<S> findOne(Example<S> example) {

      return Optional.empty();
   }

   @Override
   public <S extends Rubros> Page<S> findAll(Example<S> example, Pageable pageable) {

      return null;
   }

   @Override
   public <S extends Rubros> long count(Example<S> example) {

      return 0;
   }

   @Override
   public <S extends Rubros> boolean exists(Example<S> example) {

      return false;
   }

   @Override
   public <S extends Rubros, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {

      return null;
   }

   @Override
   public Rubros findByIdRubro(Long idrubro) {
      return dao.findByIdRubro(idrubro);
   }
}
