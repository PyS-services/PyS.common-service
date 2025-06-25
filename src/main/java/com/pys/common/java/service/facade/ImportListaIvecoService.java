package com.pys.common.java.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pys.common.java.service.*;
import com.pys.common.java.service.excel.ExcelProcessor;
import com.pys.common.java.service.facade.status.ProcessStatus;
import com.pys.common.kotlin.exception.CotizacionException;
import com.pys.common.kotlin.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ImportListaIvecoService {

    // Mapa para almacenar el estado del procesamiento
    private ConcurrentHashMap<String, ProcessStatus> processingStatus = new ConcurrentHashMap<>();

    private final ImportacionService importacionService;
    private final CotizacionService cotizacionService;
    private final ArticuloService articuloService;
    private final ProveedorService proveedorService;
    private final ArticuloImportadoService articuloImportadoService;
    private final ExcelProcessor excelProcessor;

    // Al inicio de la clase, define un JsonMapper thread-safe
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
        .findAndAddModules()
        .build();

    private static final int BATCH_SIZE = 1000;  // Aumentado de 500 a 1000

    private static final Map<String, BigDecimal> PRECIO_VENTA_DESCUENTOS = Map.of(
        "A", BigDecimal.ZERO,
        "9", BigDecimal.ZERO,
        "S", BigDecimal.ZERO,
        "7", BigDecimal.ZERO,
        "C", BigDecimal.ZERO,
        "6", BigDecimal.ZERO,
        "DEFAULT", BigDecimal.valueOf(0.10)
    );

    public ImportListaIvecoService(ImportacionService importacionService, CotizacionService cotizacionService, ArticuloService articuloService, ProveedorService proveedorService, ArticuloImportadoService articuloImportadoService, ExcelProcessor excelProcessor) {
        this.importacionService = importacionService;
        this.cotizacionService = cotizacionService;
        this.articuloService = articuloService;
        this.proveedorService = proveedorService;
        this.articuloImportadoService = articuloImportadoService;
        this.excelProcessor = excelProcessor;
    }

    public String generateProcessId() {
        return UUID.randomUUID().toString();
    }

    public ProcessStatus getStatus(String processId) {
        return processingStatus.get(processId);
    }

    @Async
    public void processFileAsync(byte[] fileContent, String processId, String cotizacionDolarString) {
        try {
            // Almacena el estado inicial del procesamiento
            processingStatus.put(processId, new ProcessStatus("En Progreso", 0));

            // Validación y conversión de la cotización
            BigDecimal cotizacionDolar;
            try {
                cotizacionDolarString = cotizacionDolarString.replace(",", ".").trim();
                cotizacionDolar = new BigDecimal(cotizacionDolarString);

                if (cotizacionDolar.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("La cotización del dólar debe ser mayor a cero");
                }
            } catch (NumberFormatException | NullPointerException e) {
                throw new IllegalArgumentException("Cotización del dólar inválida", e);
            }

            // Registro de inicio de procesamiento
            log.debug("Procesando archivo con cotización: {}", cotizacionDolar);

            var importacion = new Importacion.Builder()
                    .fechaImportacion(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS))
                    .build();

            importacion = importacionService.add(importacion);
            logImportacion(importacion);

            Optional<Cotizacion> cotizacionOptional = findOrCreateCotizacion(importacion.getFechaImportacion(), cotizacionDolar);
            Cotizacion cotizacion = cotizacionOptional.orElseThrow();
            logCotizacion(cotizacion);

            var proveedor = proveedorService.findByProveedorIdNegocio(1L);
            logProveedor(proveedor);

            // Procesar el archivo Excel
            InputStream inputStream = new ByteArrayInputStream(fileContent);
            List<Articulo> articulosFile = excelProcessor.processExcelFile(inputStream, cotizacionDolar);
            List<String> codigoArticulos = articulosFile.stream().map(Articulo::getCodigoArticulo).toList();
            Map<String, Articulo> articulos = articuloService.findAllByCodigos(codigoArticulos).stream().collect(Collectors.toMap(Articulo::getCodigoArticulo, articulo -> articulo));

            // Usar AtomicInteger para el seguimiento del progreso de forma thread-safe
            AtomicInteger articulosProcesados = new AtomicInteger(0);
            int totalArticulos = articulosFile.size();
            
            // Optimizar el procesamiento por lotes usando partitioningBy
            List<List<Articulo>> batches = IntStream.range(0, articulosFile.size())
                .boxed()
                .collect(Collectors.groupingBy(index -> index / BATCH_SIZE))
                .values()
                .stream()
                .map(indices -> indices.stream()
                    .map(articulosFile::get)
                    .collect(Collectors.toList()))
                .toList();

            // Procesar los lotes en paralelo
            for (int counter = 0; counter < batches.size(); counter++) {
                List<Articulo> batch = batches.get(counter);
                List<Articulo> articulosSaved = new ArrayList<>();
                List<ArticuloImportado> articulosImportadosToSave = new ArrayList<>();
                
                // Procesar el lote
                Importacion finalImportacion = importacion;
                batch.forEach(articuloFile -> {
                    try {
                        Articulo articulo = null;
                        var isArticuloNew = false;
                        
                        if (articulos.containsKey(articuloFile.getCodigoArticulo())) {
                            articulo = articulos.get(articuloFile.getCodigoArticulo());
                            articulo.setDescripcion(articuloFile.getDescripcion());
                            articulo.setPrecioListaSinIva(articuloFile.getPrecioListaSinIva());
                            articulo.setPrecioListaSinIvaUsd(articuloFile.getPrecioListaSinIvaUsd());
                            articulo.setOrigen(articuloFile.getOrigen());
                            articulo.setDescuento(articuloFile.getDescuento());
                            articulo.setFechaActualizacion(articuloFile.getFechaActualizacion());
                            articulo.setModeloCamion(articuloFile.getModeloCamion());
                            articulo.setCotizacionId(cotizacion.getCotizacionId());
                            articulo.setProveedorId(proveedor.getProveedorId());
                            log.debug("Articulo actualizado");
                        } else {
                            articulo = new Articulo.Builder()
                                    .codigoArticulo(articuloFile.getCodigoArticulo().toUpperCase())
                                    .descripcion(articuloFile.getDescripcion())
                                    .precioListaSinIva(articuloFile.getPrecioListaSinIva())
                                    .precioListaSinIvaUsd(articuloFile.getPrecioListaSinIvaUsd())
                                    .origen(articuloFile.getOrigen())
                                    .descuento(articuloFile.getDescuento())
                                    .fechaActualizacion(articuloFile.getFechaActualizacion())
                                    .modeloCamion(articuloFile.getModeloCamion())
                                    .cotizacionId(cotizacion.getCotizacionId())
                                    .proveedorId(proveedor.getProveedorId())
                                    .build();
                            isArticuloNew = true;
                            log.debug("Articulo creado");
                        }

                        // Recalcular precios
                        recalcularPrecios(articulo);
                        articulo = isArticuloNew ? articuloService.add(articulo) : articuloService.update(articulo, articulo.getCodigoArticulo());
                        articulosSaved.add(articulo);

                    } catch (Exception e) {
                        log.error("Error procesando artículo en lote: {}", e.getMessage());
                    }
                });

                // Crear y guardar todos los ArticuloImportado del batch
                articulosSaved.forEach(articulo -> {
                    var articuloImported = createArticuloImportado(articulo, finalImportacion, cotizacion, cotizacionDolar);
                    articulosImportadosToSave.add(articuloImported);
                });
                
                articuloImportadoService.saveAll(articulosImportadosToSave);

                // Actualizar progreso
                int articulosProcesadosEnBatch = batch.size();
                synchronized(processingStatus) {
                    int totalProcesados = articulosProcesados.addAndGet(articulosProcesadosEnBatch);
                    int progress = (int) ((totalProcesados / (double) totalArticulos) * 100);
                    ProcessStatus currentStatus = processingStatus.get(processId);
                    if (currentStatus != null) {
                        currentStatus.setStatus("En Progreso");
                        currentStatus.setProgress(progress);
                        log.debug("Procesado lote {}/{}, progreso: {}%", 
                                counter + 1,
                                batches.size(), 
                                progress);
                    }
                }

                // Opcional: agregar una pequeña pausa entre lotes
                Thread.sleep(50);
            }

            // Actualiza el estado a 'Completado' después de procesar
            log.debug("Actualizando estado a 'Completado' para el proceso {}", processId);
            processingStatus.get(processId).setStatus("Completado");
            processingStatus.get(processId).setProgress(100);
        } catch (Exception e) {
            log.error("Error al procesar el archivo", e);
            // Actualiza el estado a 'Error' en caso de fallo
            processingStatus.get(processId).setStatus("Error");
        }
    }

    private void logArticulo(Articulo articulo) {
        try {
            // Usa el mapper thread-safe
            log.debug("Articulo -> {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(articulo));
        } catch (JsonProcessingException e) {
            log.error("Articulo jsonify error -> {}", e.getMessage());
        }
    }

    private void logArticuloImportado(ArticuloImportado articuloImported) {
        try {
            log.debug("ArticuloImportado -> {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(articuloImported));
        } catch (JsonProcessingException e) {
            log.error("ArticuloImportado jsonify error -> {}", e.getMessage());
        }
    }

    private void logProveedor(Proveedor proveedor) {
        try {
            log.debug("Proveedor -> {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(proveedor));
        } catch (JsonProcessingException e) {
            log.error("Proveedor jsonify error -> {}", e.getMessage());
        }
    }

    private void logCotizacion(Cotizacion cotizacion) {
        try {
            log.debug("Cotizacion -> {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(cotizacion));
        } catch (JsonProcessingException e) {
            log.error("Cotizacion jsonify error -> {}", e.getMessage());
        }
    }

    private void logImportacion(Importacion importacion) {
        try {
            log.debug("Importacion -> {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(importacion));
        } catch (JsonProcessingException e) {
            log.error("Importacion jsonify error -> {}", e.getMessage());
        }
    }

    private static final Map<String, BigDecimal> DESCUENTOS_MAP = Map.of(
            "A", Constants.DESCUENTO_NIVEL_1,
            "9", Constants.DESCUENTO_NIVEL_1,
            "S", Constants.DESCUENTO_NIVEL_2,
            "7", Constants.DESCUENTO_NIVEL_2,
            "C", Constants.DESCUENTO_NIVEL_3,
            "6", Constants.DESCUENTO_NIVEL_3
    );

    private void recalcularPrecios(Articulo articulo) {
        // Cálculo de precios iniciales
        BigDecimal precioListaConIva = a2Decimales(articulo.getPrecioListaSinIva().multiply(Constants.IVA));
        articulo.setPrecioListaConIva(precioListaConIva);

        // Determina precio de compra
        BigDecimal porcentajeDescuento = DESCUENTOS_MAP.getOrDefault(articulo.getDescuento(), Constants.DESCUENTO_DEFAULT);
        BigDecimal precioCompraSinIva = a2Decimales(
                articulo.getPrecioVentaSinIva().multiply(BigDecimal.ONE.subtract(porcentajeDescuento))
        );
        articulo.setPrecioCompraSinIva(precioCompraSinIva);

        // Determina precio de venta
        porcentajeDescuento = BigDecimal.valueOf(0.10);

        if ("A".equals(articulo.getDescuento()) || "9".equals(articulo.getDescuento()) ||
                "S".equals(articulo.getDescuento()) || "7".equals(articulo.getDescuento()) ||
                "C".equals(articulo.getDescuento()) || "6".equals(articulo.getDescuento())) {
            porcentajeDescuento = BigDecimal.ZERO;
        }

        BigDecimal precioVentaConIva = a2Decimales(
                articulo.getPrecioListaConIva().multiply(BigDecimal.ONE.subtract(porcentajeDescuento))
        );
        articulo.setPrecioVentaConIva(precioVentaConIva);

        BigDecimal precioVentaSinIva = a2Decimales(
                articulo.getPrecioListaSinIva().multiply(BigDecimal.ONE.subtract(porcentajeDescuento))
        );
        articulo.setPrecioVentaSinIva(precioVentaSinIva);
    }

    private BigDecimal a2Decimales(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static final class Constants {
        static final BigDecimal IVA = BigDecimal.valueOf(1.21);
        static final BigDecimal DESCUENTO_DEFAULT = BigDecimal.valueOf(0.25);
        static final BigDecimal DESCUENTO_NIVEL_1 = BigDecimal.valueOf(0.05);
        static final BigDecimal DESCUENTO_NIVEL_2 = BigDecimal.valueOf(0.10);
        static final BigDecimal DESCUENTO_NIVEL_3 = BigDecimal.valueOf(0.12);

        static final List<String> DESCUENTOS_ESPECIALES = Arrays.asList("A", "9", "S", "7", "C", "6");
    }

    private ArticuloImportado createArticuloImportado(Articulo articulo, Importacion importacion, Cotizacion cotizacion, BigDecimal cotizacionDolar) {
        return new ArticuloImportado.Builder()
                .articuloId(articulo.getArticuloId())
                .fecha(importacion.getFechaImportacion())
                .codigoArticulo(articulo.getCodigoArticulo())
                .descripcion(articulo.getDescripcion())
                .precioListaSinIva(articulo.getPrecioListaSinIva())
                .origen(articulo.getOrigen())
                .descuento(articulo.getDescuento())
                .fechaActualizacion(Objects.requireNonNull(articulo.getFechaActualizacion()))
                .cotizacionId(cotizacion.getCotizacionId())
                .valorUsd(cotizacionDolar)
                .importacionId(importacion.getImportacionId())
                .build();
    }

    private Optional<Cotizacion> findOrCreateCotizacion(OffsetDateTime fecha, BigDecimal cotizacionDolar) {
        try {
            return Optional.of(cotizacionService.findByFecha(fecha));
        } catch (CotizacionException e) {
            Cotizacion nuevaCotizacion = new Cotizacion.Builder()
                    .fecha(fecha)
                    .usdVenta(cotizacionDolar)
                    .build();
            return Optional.of(cotizacionService.add(nuevaCotizacion));
        }
    }

    @Transactional
    public void processBatch(List<Articulo> batch, Importacion importacion, Cotizacion cotizacion, 
                            BigDecimal cotizacionDolar, Proveedor proveedor) {
        // Procesar el lote en una única transacción
    }
}
