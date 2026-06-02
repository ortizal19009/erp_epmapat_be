package com.epmapat.erp_epmapat.servicio;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.LecturaDto;
import com.epmapat.erp_epmapat.DTO.SmartSyncResponseDto;
import com.epmapat.erp_epmapat.mappers.LecturaMapper;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Rutas;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.CategoriaR;
import com.epmapat.erp_epmapat.repositorio.ClientesR;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.repositorio.NacionalidadR;
import com.epmapat.erp_epmapat.repositorio.NovedadR;
import com.epmapat.erp_epmapat.repositorio.Pliego24R;
import com.epmapat.erp_epmapat.repositorio.RutasR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MobileSyncService {

    private final LecturasR lecturasR;
    private final AbonadosR abonadosR;
    private final ClientesR clientesR;
    private final RutasR rutasR;
    private final CategoriaR categoriaR;
    private final NovedadR novedadR;
    private final NacionalidadR nacionalidadR;
    private final Pliego24R pliego24R;

    public SmartSyncResponseDto getSmartSyncData(Long idusuario, Long idemision, List<String> modulos) {
        List<String> upperModulos = modulos == null
                ? List.of()
                : modulos.stream()
                        .filter(module -> module != null && !module.isBlank())
                        .map(String::toUpperCase)
                        .collect(Collectors.toList());

        List<Lecturas> assignedLecturas = lecturasR.findByUsuarioEmision(idusuario, idemision);

        List<Abonados> abonados;
        if (upperModulos.contains("ABONADOS") || upperModulos.contains("ABONADO")) {
            abonados = abonadosR.findAll();
        } else {
            Set<Long> abonadoIds = assignedLecturas.stream()
                    .filter(lectura -> lectura.getIdabonado_abonados() != null)
                    .map(lectura -> lectura.getIdabonado_abonados().getIdabonado())
                    .collect(Collectors.toSet());
            abonados = abonadoIds.isEmpty() ? List.of() : abonadosR.findAllById(abonadoIds);
        }

        List<Clientes> clientes;
        if (upperModulos.contains("CLIENTES") || upperModulos.contains("CLIENTE")) {
            clientes = clientesR.findAll();
        } else {
            Set<Long> clienteIds = new HashSet<>();
            for (Abonados abonado : abonados) {
                if (abonado.getIdcliente_clientes() != null) {
                    clienteIds.add(abonado.getIdcliente_clientes().getIdcliente());
                }
                if (abonado.getIdresponsable() != null) {
                    clienteIds.add(abonado.getIdresponsable().getIdcliente());
                }
            }
            clientes = clienteIds.isEmpty() ? List.of() : clientesR.findAllById(clienteIds);
        }

        List<Rutas> rutas;
        if (upperModulos.contains("RUTAS") || upperModulos.contains("RUTA")) {
            rutas = rutasR.findAll();
        } else {
            Set<Long> rutaIds = assignedLecturas.stream()
                    .filter(lectura -> lectura.getIdrutaxemision_rutasxemision() != null
                            && lectura.getIdrutaxemision_rutasxemision().getIdruta_rutas() != null)
                    .map(lectura -> lectura.getIdrutaxemision_rutasxemision().getIdruta_rutas().getIdruta())
                    .collect(Collectors.toSet());
            rutas = rutaIds.isEmpty() ? List.of() : rutasR.findAllById(rutaIds);
        }

        List<LecturaDto> lecturasDto = assignedLecturas.stream()
                .map(LecturaMapper::toDto)
                .collect(Collectors.toList());

        return SmartSyncResponseDto.builder()
                .lecturas(lecturasDto)
                .abonados(abonados)
                .clientes(clientes)
                .rutas(rutas)
                .categorias(categoriaR.findAll())
                .novedades(novedadR.getNovedadesToMobile())
                .nacionalidades(nacionalidadR.findAll())
                .pliegos(pliego24R.findAll())
                .build();
    }
}
