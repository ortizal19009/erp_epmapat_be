package com.epmapat.erp_epmapat.nc;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
/** The operation flag must never restore already transferred money to a legacy balance. */
@Component
public class NcSchemaState {
 private final JdbcTemplate db;
 public NcSchemaState(JdbcTemplate db){this.db=db;}
 public boolean migrated(){return Boolean.TRUE.equals(db.queryForObject("SELECT to_regclass('public.nc_saldos') IS NOT NULL",Boolean.class));}
}
