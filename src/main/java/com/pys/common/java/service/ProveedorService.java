package com.pys.common.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pys.common.kotlin.exception.ProveedorException;
import com.pys.common.kotlin.model.Proveedor;
import com.pys.common.kotlin.repository.ProveedorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import static com.pys.common.java.specification.ProveedorSpecifications.searchByCadena;

@Service
@Slf4j
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    public List<Proveedor> findAll() {
        return repository.findAllByOrderByRazonSocial();
    }

    public Page<Proveedor> findAllPaginated(Pageable pageable) {
        return repository.findAllByOrderByRazonSocial(pageable);
    }

    public Page<Proveedor> findAllPaginatedContains(String razonSocial, Pageable pageable) {
        return repository.findAllByRazonSocialContainsIgnoreCaseOrderByRazonSocial(razonSocial, pageable);
    }

    public List<Proveedor> searchProveedores(String searchString) {
        return repository.findAll(
            searchByCadena(searchString), 
            PageRequest.of(0, 50, Sort.by("razonSocial").ascending())
        ).getContent();
    }

    public Proveedor findByProveedorId(UUID proveedorId) {
        return Objects.requireNonNull(repository.findByProveedorId(proveedorId)).orElseThrow(() -> new ProveedorException(proveedorId));
    }

    public Proveedor findByProveedorIdNegocio(Long proveedorIdNegocio) {
        return Objects.requireNonNull(repository.findByProveedorIdNegocio(proveedorIdNegocio)).orElseThrow(() -> new ProveedorException(proveedorIdNegocio));
    }

    public Proveedor findByCuit(String cuit) {
        return Objects.requireNonNull(repository.findByCuit(cuit)).orElseThrow(() -> new ProveedorException(cuit));
    }

    public Proveedor findLastByProveedorIdNegocio() {
        return Objects.requireNonNull(repository.findTop1ByOrderByProveedorIdNegocioDesc()).orElseThrow(ProveedorException::new);
    }

    public Proveedor add(Proveedor proveedor) {
        log.debug("Processing proveedor for add");
        if (proveedor.getProveedorIdNegocio() == null || proveedor.getProveedorIdNegocio() == 0) {
            proveedor.setProveedorIdNegocio(findLastByProveedorIdNegocio().getProveedorIdNegocio() + 1);
        }
        proveedor = repository.save(proveedor);
        logProveedor(proveedor);
        return proveedor;
    }

    public Proveedor updateByProveedorId(Proveedor proveedor, UUID proveedorId) {
        log.debug("Processing proveedor for update");
        return repository.findByProveedorId(proveedorId).map(existingProveedor -> {
            existingProveedor = new Proveedor.Builder()
                    .proveedorId(existingProveedor.getProveedorId())
                    .proveedorIdNegocio(proveedor.getProveedorIdNegocio())
                    .razonSocial(proveedor.getRazonSocial())
                    .nombreFantasia(proveedor.getNombreFantasia())
                    .cuit(proveedor.getCuit())
                    .domicilio(proveedor.getDomicilio())
                    .localidad(proveedor.getLocalidad())
                    .provincia(proveedor.getProvincia())
                    .telefono(proveedor.getTelefono())
                    .fax(proveedor.getFax())
                    .email(proveedor.getEmail())
                    .posicionIva(proveedor.getPosicionIva())
                    .celular(proveedor.getCelular())
                    .ingresosBrutos(proveedor.getIngresosBrutos())
                    .contacto(proveedor.getContacto())
                    .observaciones(proveedor.getObservaciones())
                    .build();
            existingProveedor = repository.save(existingProveedor);
            logProveedor(existingProveedor);
            return existingProveedor;
        }).orElseThrow(() -> new ProveedorException(proveedorId));
    }

    public Proveedor updateByProveedorIdNegocio(Proveedor proveedor, Long proveedorIdNegocio) {
        log.debug("Processing proveedor for update");
        return repository.findByProveedorIdNegocio(proveedorIdNegocio).map(existingProveedor -> {
            existingProveedor = new Proveedor.Builder()
                    .proveedorId(existingProveedor.getProveedorId())
                    .proveedorIdNegocio(proveedor.getProveedorIdNegocio())
                    .razonSocial(proveedor.getRazonSocial())
                    .nombreFantasia(proveedor.getNombreFantasia())
                    .cuit(proveedor.getCuit())
                    .domicilio(proveedor.getDomicilio())
                    .localidad(proveedor.getLocalidad())
                    .provincia(proveedor.getProvincia())
                    .telefono(proveedor.getTelefono())
                    .fax(proveedor.getFax())
                    .email(proveedor.getEmail())
                    .posicionIva(proveedor.getPosicionIva())
                    .celular(proveedor.getCelular())
                    .ingresosBrutos(proveedor.getIngresosBrutos())
                    .contacto(proveedor.getContacto())
                    .observaciones(proveedor.getObservaciones())
                    .build();
            existingProveedor = repository.save(existingProveedor);
            logProveedor(existingProveedor);
            return existingProveedor;
        }).orElseThrow(() -> new ProveedorException(proveedorIdNegocio));
    }

    public Proveedor addOrUpdate(Proveedor proveedor) {
        log.debug("Processing proveedor for addOrUpdate");
        return repository.findByProveedorIdNegocio(Objects.requireNonNull(proveedor.getProveedorIdNegocio())).map(existingProveedor -> updateByProveedorIdNegocio(proveedor, proveedor.getProveedorIdNegocio()))
                .orElseGet(() -> add(proveedor));
    }

    private void logProveedor(Proveedor proveedor) {
        try {
            log.debug("Proveedor -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedor));
        } catch (JsonProcessingException e) {
            log.error("Proveedor jsonify error -> {}", e.getMessage());
        }
    }

    @Transactional
    public void deleteByProveedorId(UUID proveedorId) {
        repository.deleteByProveedorId(proveedorId);
    }

}
