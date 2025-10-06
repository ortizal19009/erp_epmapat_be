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

import com.epmapat.erp_epmapat.modelo.Catalogoitems;
import com.epmapat.erp_epmapat.repositorio.CatalogoitemsR;

@Service
public class CatalogoitemServicio implements CatalogoitemsR {

   @Autowired
   CatalogoitemsR dao;

   @Override
   public List<Catalogoitems> findProductos(Long idmodulo1, Long idmodulo2, String descripcion) {
      return dao.findProductos(idmodulo1, idmodulo2, descripcion);
   }

   @Override
   public List<Catalogoitems> findAll() {
      return dao.findAll();
   }

   @Override
   public List<Catalogoitems> findByIdrubro(Long idrubro) {
      return dao.findByIdrubro(idrubro);
   }

   @Override
   public List<Catalogoitems> findByIdusoitems(Long idusoitems) {
      return dao.findByIdusoitems(idusoitems);
   }

   @Override
   public Optional<Catalogoitems> findById(Long id) {
      return dao.findById(id);
   }

   @Override
   public List<Catalogoitems> findByNombre(Long idusoitems, String descripcion) {
      return dao.findByNombre(idusoitems, descripcion);
   }

   @Override
   public <S extends Catalogoitems> S save(S entity) {
      return dao.save(entity);
  }

   // =========================================================
   
   @Override
   public void deleteAllByIdInBatch(Iterable<Long> ids) {
      
      throw new UnsupportedOperationException("Unimplemented method 'deleteAllByIdInBatch'");
   }

   @Override
   public void deleteAllInBatch() {
      
      throw new UnsupportedOperationException("Unimplemented method 'deleteAllInBatch'");
   }

   @Override
   public void deleteAllInBatch(Iterable<Catalogoitems> entities) {
      
      throw new UnsupportedOperationException("Unimplemented method 'deleteAllInBatch'");
   }

   @Override
   public List<Catalogoitems> findAll(Sort sort) {
      
      throw new UnsupportedOperationException("Unimplemented method 'findAll'");
   }

   @Override
   public <S extends Catalogoitems> List<S> findAll(Example<S> example) {
      
      throw new UnsupportedOperationException("Unimplemented method 'findAll'");
   }

   @Override
   public <S extends Catalogoitems> List<S> findAll(Example<S> example, Sort sort) {
      
      throw new UnsupportedOperationException("Unimplemented method 'findAll'");
   }

   @Override
   public List<Catalogoitems> findAllById(Iterable<Long> ids) {
      
      throw new UnsupportedOperationException("Unimplemented method 'findAllById'");
   }

   @Override
   public void flush() {
      
      throw new UnsupportedOperationException("Unimplemented method 'flush'");
   }

   @Override
   public Catalogoitems getById(Long id) {
      
      throw new UnsupportedOperationException("Unimplemented method 'getById'");
   }

   @Override
   public Catalogoitems getOne(Long id) {
      
      throw new UnsupportedOperationException("Unimplemented method 'getOne'");
   }

   @Override
   public Catalogoitems getReferenceById(Long id) {
      
      throw new UnsupportedOperationException("Unimplemented method 'getReferenceById'");
   }

   @Override
   public <S extends Catalogoitems> List<S> saveAll(Iterable<S> entities) {
      
      throw new UnsupportedOperationException("Unimplemented method 'saveAll'");
   }

   @Override
   public <S extends Catalogoitems> List<S> saveAllAndFlush(Iterable<S> entities) {
      
      throw new UnsupportedOperationException("Unimplemented method 'saveAllAndFlush'");
   }

   @Override
   public <S extends Catalogoitems> S saveAndFlush(S entity) {
      
      throw new UnsupportedOperationException("Unimplemented method 'saveAndFlush'");
   }

   @Override
   public Page<Catalogoitems> findAll(Pageable pageable) {
      
      throw new UnsupportedOperationException("Unimplemented method 'findAll'");
   }

   @Override
   public long count() {
      
      throw new UnsupportedOperationException("Unimplemented method 'count'");
   }

   @Override
   public void delete(Catalogoitems entity) {
      
      throw new UnsupportedOperationException("Unimplemented method 'delete'");
   }

   @Override
   public void deleteAll() {
      
      throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
   }

   @Override
   public void deleteAll(Iterable<? extends Catalogoitems> entities) {
      
      throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
   }

   @Override
   public void deleteAllById(Iterable<? extends Long> ids) {
      
      throw new UnsupportedOperationException("Unimplemented method 'deleteAllById'");
   }

   @Override
   public void deleteById(Long id) {
      
      throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
   }

   @Override
   public boolean existsById(Long id) {
      
      throw new UnsupportedOperationException("Unimplemented method 'existsById'");
   }

   @Override
   public <S extends Catalogoitems> long count(Example<S> example) {
      
      throw new UnsupportedOperationException("Unimplemented method 'count'");
   }

   @Override
   public <S extends Catalogoitems> boolean exists(Example<S> example) {
      
      throw new UnsupportedOperationException("Unimplemented method 'exists'");
   }

   @Override
   public <S extends Catalogoitems> Page<S> findAll(Example<S> example, Pageable pageable) {
      
      throw new UnsupportedOperationException("Unimplemented method 'findAll'");
   }

   @Override
   public <S extends Catalogoitems, R> R findBy(Example<S> example,
         Function<FetchableFluentQuery<S>, R> queryFunction) {
      
      throw new UnsupportedOperationException("Unimplemented method 'findBy'");
   }

   @Override
   public <S extends Catalogoitems> Optional<S> findOne(Example<S> example) {
      
      throw new UnsupportedOperationException("Unimplemented method 'findOne'");
   }
   
}
