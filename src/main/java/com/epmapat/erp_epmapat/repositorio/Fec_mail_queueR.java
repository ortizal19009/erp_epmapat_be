package com.epmapat.erp_epmapat.repositorio;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.Fec_mail_queue;

public interface Fec_mail_queueR extends JpaRepository<Fec_mail_queue, Long> {

   @Query("""
         select q from Fec_mail_queue q
         where q.idfactura = :idfactura
           and q.estado in :estados
         order by q.fechaCrea desc
         """)
   List<Fec_mail_queue> findActivasByFactura(@Param("idfactura") Long idfactura, @Param("estados") Collection<String> estados);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   @Query("""
         select q from Fec_mail_queue q
         where q.estado in :estados
         order by q.prioridad desc, q.fechaCrea asc, q.id asc
         """)
   List<Fec_mail_queue> lockNextByEstados(@Param("estados") Collection<String> estados, Pageable pageable);

   Optional<Fec_mail_queue> findFirstByCorrelationIdOrderByFechaCreaDesc(String correlationId);
}
