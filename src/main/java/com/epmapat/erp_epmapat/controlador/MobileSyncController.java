package com.epmapat.erp_epmapat.controlador;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.LecturaDto;
import com.epmapat.erp_epmapat.DTO.SmartSyncRequestDto;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mobile/sync")
public class MobileSyncController {

    private final LecturasR lecturasR;
    private final AbonadosR abonadosR;
    private final ClientesR clientesR;
    private final RutasR rutasR;
    private final CategoriaR categoriaR;
    private final NovedadR novedadR;
    private final NacionalidadR nacionalidadR;
    private final Pliego24R pliego24R;

    @PostMapping("/download")
    public ResponseEntity<SmartSyncResponseDto> download(@RequestBody SmartSyncRequestDto request) {
        log.info("Iniciando Smart Sync para usuario: {} y emision: {}", request.getIdusuario(), request.getIdemision());

        List<String> modulos = request.getModulos() == null
                ? List.of()
                : request.getModulos().stream()
                        .filter(module -> module != null && !module.isBlank())
                        .map(String::toUpperCase)
                        .collect(Collectors.toList());

        List<Lecturas> assignedLecturas = lecturasR.findByUsuarioEmision(request.getIdusuario(), request.getIdemision());
        List<LecturaDto> lecturasDto = assignedLecturas.stream()
                .map(LecturaMapper::toDto)
                .collect(Collectors.toList());

        List<Abonados> abonados;
        if (modulos.contains("ABONADOS") || modulos.contains("ABONADO")) {
            abonados = abonadosR.findAll();
        } else {
            Set<Long> assignedAbonadoIds = assignedLecturas.stream()
                    .filter(l -> l.getIdabonado_abonados() != null)
                    .map(l -> l.getIdabonado_abonados().getIdabonado())
                    .collect(Collectors.toSet());
            abonados = assignedAbonadoIds.isEmpty() ? List.of() : abonadosR.findAllById(assignedAbonadoIds);
        }

        List<Clientes> clientes;
        if (modulos.contains("CLIENTES") || modulos.contains("CLIENTE")) {
            clientes = clientesR.findAll();
        } else {
            Set<Long> assignedClienteIds = new HashSet<>();
            for (Abonados abonado : abonados) {
                if (abonado.getIdcliente_clientes() != null) {
                    assignedClienteIds.add(abonado.getIdcliente_clientes().getIdcliente());
                }
                if (abonado.getIdresponsable() != null) {
                    assignedClienteIds.add(abonado.getIdresponsable().getIdcliente());
                }
            }
            clientes = assignedClienteIds.isEmpty() ? List.of() : clientesR.findAllById(assignedClienteIds);
        }

        List<Rutas> rutas;
        if (modulos.contains("RUTAS") || modulos.contains("RUTA")) {
            rutas = rutasR.findAll();
        } else {
            Set<Long> assignedRutaIds = assignedLecturas.stream()
                    .filter(l -> l.getIdrutaxemision_rutasxemision() != null
                            && l.getIdrutaxemision_rutasxemision().getIdruta_rutas() != null)
                    .map(l -> l.getIdrutaxemision_rutasxemision().getIdruta_rutas().getIdruta())
                    .collect(Collectors.toSet());
            rutas = assignedRutaIds.isEmpty() ? List.of() : rutasR.findAllById(assignedRutaIds);
        }

        SmartSyncResponseDto response = SmartSyncResponseDto.builder()
                .lecturas(lecturasDto)
                .abonados(abonados)
                .clientes(clientes)
                .rutas(rutas)
                .categorias(categoriaR.findAll())
                .novedades(novedadR.getNovedadesToMobile())
                .nacionalidades(nacionalidadR.findAll())
                .pliegos(pliego24R.findAll())
                .build();

        log.info("Smart Sync completado para usuario {}: {} lecturas, {} abonados, {} clientes, {} rutas",
                request.getIdusuario(), lecturasDto.size(), abonados.size(), clientes.size(), rutas.size());

        return ResponseEntity.ok(response);
    }
}
