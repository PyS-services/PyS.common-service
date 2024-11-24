package com.pys.common.java.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pys.common.java.service.*;
import com.pys.common.java.service.facade.status.ProcessStatus;
import com.pys.common.kotlin.exception.CotizacionException;
import com.pys.common.kotlin.model.Articulo;
import com.pys.common.kotlin.model.ArticuloImportado;
import com.pys.common.kotlin.model.Cotizacion;
import com.pys.common.kotlin.model.Importacion;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public ImportListaIvecoService(ImportacionService importacionService, CotizacionService cotizacionService, ArticuloService articuloService, ProveedorService proveedorService, ArticuloImportadoService articuloImportadoService) {
        this.importacionService = importacionService;
        this.cotizacionService = cotizacionService;
        this.articuloService = articuloService;
        this.proveedorService = proveedorService;
        this.articuloImportadoService = articuloImportadoService;
    }

    public String generateProcessId() {
        return UUID.randomUUID().toString();
    }

    public ProcessStatus getStatus(String processId) {
        return processingStatus.get(processId);
    }

    @Async
    @Transactional
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
            log.debug("Procesando archivo con cotización: " + cotizacionDolar);

            var importacion = new Importacion.Builder()
                    .fechaImportacion(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS))
                    .build();

            importacion = importacionService.add(importacion);

            try {
                log.debug("Importacion -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(importacion));
            } catch (JsonProcessingException e) {
                log.error("Importacion -> {}", e.getMessage());
            }

            Cotizacion cotizacion = null;
            try {
                cotizacion = cotizacionService.findByFecha(importacion.getFechaImportacion());
            } catch (CotizacionException e) {
                cotizacion = new Cotizacion.Builder()
                        .fecha(importacion.getFechaImportacion())
                        .build();
            }

            cotizacion.setUsdVenta(cotizacionDolar);
            cotizacion = cotizacionService.save(cotizacion);
            try {
                log.debug("Cotizacion -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cotizacion));
            } catch (JsonProcessingException e) {
                log.error("Cotizacion -> {}", e.getMessage());
            }

            var proveedor = proveedorService.findByProveedorId(1L);
            try {
                log.debug("Proveedor -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedor));
            } catch (JsonProcessingException e) {
                log.error("Proveedor -> {}", e.getMessage());
            }

            // Procesar el archivo Excel
            InputStream inputStream = new ByteArrayInputStream(fileContent);
            List<Articulo> articulosFile = processExcelFile(inputStream, cotizacionDolar);
            List<String> codigoArticulos = articulosFile.stream().map(Articulo::getCodigoArticulo).toList();
            Map<String, Articulo> articulos = articuloService.findAllByCodigos(codigoArticulos).stream().collect(Collectors.toMap(Articulo::getCodigoArticulo, articulo -> articulo));

            Map<String, Articulo> articulosToSave = new HashMap<>();
            List<ArticuloImportado> articulosImported = new ArrayList<>();

            int totalArticulos = articulosFile.size();
            int articulosProcesados = 0;
            for (Articulo articuloFile : articulosFile) {
                Articulo articulo = null;
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
                }
                recalcularPrecios(articulo);

                if (!articulosToSave.containsKey(articulo.getCodigoArticulo())) {
                    articulosToSave.put(articulo.getCodigoArticulo(), articulo);
                    var articuloImported = new ArticuloImportado.Builder()
                            .articuloId(articulo.getArticuloId())
                            .fecha(importacion.getFechaImportacion())
                            .codigoArticulo(articulo.getCodigoArticulo())
                            .descripcion(articulo.getDescripcion())
                            .precioListaSinIva(articulo.getPrecioListaSinIva())
                            .origen(articulo.getOrigen())
                            .descuento(articulo.getDescuento())
                            .fechaActualizacion(articulo.getFechaActualizacion())
                            .cotizacionId(cotizacion.getCotizacionId())
                            .valorUsd(cotizacionDolar)
                            .importacionId(importacion.getImportacionId())
                            .build();
                    articulosImported.add(articuloImported);
                }
                // Actualizar progreso
                articulosProcesados++;
                int progress = (int) ((articulosProcesados / (double) totalArticulos) * 100);
                log.debug("Progreso: {}", progress);
                processingStatus.get(processId).setProgress(progress);
            }

            articuloService.saveAll(articulosToSave.values().stream().toList());
            articuloImportadoService.saveAll(articulosImported);

            // Actualiza el estado a 'Completado' después de procesar
            processingStatus.get(processId).setStatus("Completado");
            processingStatus.get(processId).setProgress(100);
        } catch (Exception e) {
            log.error("Error al procesar el archivo", e);
            // Actualiza el estado a 'Error' en caso de fallo
            processingStatus.get(processId).setStatus("Error");
        }
    }

    private void recalcularPrecios(Articulo articulo) {

        // Cálculo de precios iniciales
        BigDecimal precioListaConIva = a2Decimales(articulo.getPrecioListaSinIva().multiply(BigDecimal.valueOf(1.21)));
        articulo.setPrecioListaConIva(precioListaConIva);

        articulo.setPrecioVentaConIva(a2Decimales(precioListaConIva));
        articulo.setPrecioVentaSinIva(a2Decimales(articulo.getPrecioListaSinIva()));

        // Determina precio de compra
        BigDecimal porcentajeDescuento = BigDecimal.valueOf(0.25);
        String descuento = articulo.getDescuento();

        if ("A".equals(descuento) || "9".equals(descuento)) {
            porcentajeDescuento = BigDecimal.valueOf(0.05);
        } else if ("S".equals(descuento) || "7".equals(descuento)) {
            porcentajeDescuento = BigDecimal.valueOf(0.10);
        } else if ("C".equals(descuento) || "6".equals(descuento)) {
            porcentajeDescuento = BigDecimal.valueOf(0.12);
        }

        BigDecimal precioCompraSinIva = a2Decimales(
                articulo.getPrecioVentaSinIva().multiply(BigDecimal.ONE.subtract(porcentajeDescuento))
        );
        articulo.setPrecioCompraSinIva(precioCompraSinIva);

        // Determina precio de venta
        porcentajeDescuento = BigDecimal.valueOf(0.10);

        if ("A".equals(descuento) || "9".equals(descuento) ||
                "S".equals(descuento) || "7".equals(descuento) ||
                "C".equals(descuento) || "6".equals(descuento)) {
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

    private List<Articulo> processExcelFile(InputStream inputStream, BigDecimal cotizacionDolar) throws Exception {
        List<Articulo> articulos = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        int headerRowIndex = 4; // Índice de la fila que contiene la cabecera (fila 5 en Excel)
        int startingRow = 5;    // Índice de la fila desde donde comenzar a procesar (fila 6 en Excel)

        // Obtener la fila de la cabecera
        Row headerRow = sheet.getRow(headerRowIndex);
        if (headerRow == null) {
            throw new Exception("La fila de la cabecera no se encontró en el índice especificado.");
        }

        Map<String, Integer> columnIndexMap = new HashMap<>();

        // Leer la cabecera para obtener los índices de las columnas
        for (Cell cell : headerRow) {
            String cellValue = getCellValueAsString(cell);
            log.debug("cellValue: {}", cellValue);
            switch (cellValue) {
                case "Catálogo":
                    columnIndexMap.put("catalogo", cell.getColumnIndex());
                    break;
                case "0":
                    columnIndexMap.put("origen", cell.getColumnIndex());
                    break;
                case "s/IVA":
                    columnIndexMap.put("sinIva", cell.getColumnIndex());
                    break;
                case "D":
                    columnIndexMap.put("descuento", cell.getColumnIndex());
                    break;
                case "Denominación":
                    columnIndexMap.put("denominacion", cell.getColumnIndex());
                    break;
                case "Fecha":
                    columnIndexMap.put("fecha", cell.getColumnIndex());
                    break;
                case "Modelo":
                    columnIndexMap.put("modelo", cell.getColumnIndex());
                    break;
                default:
                    // Ignorar otras columnas
                    break;
            }
        }

        // Validar que se hayan encontrado todas las columnas necesarias
        List<String> requiredColumns = Arrays.asList("catalogo", "origen", "sinIva", "descuento", "denominacion", "fecha", "modelo");
        if (!columnIndexMap.keySet().containsAll(requiredColumns)) {
            throw new Exception("El archivo Excel no contiene todas las columnas necesarias.");
        }
        // Obtener el número total de filas
        int totalRows = sheet.getLastRowNum() + 1;

        // Procesar desde la fila de inicio hasta el final
        for (int rowIndex = startingRow; rowIndex < totalRows; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue; // Si la fila está vacía, saltarla
            }

            Articulo articulo;

            try {
                BigDecimal sinIvaUsd = getCellValueAsBigDecimal(row.getCell(columnIndexMap.get("sinIva")));
                BigDecimal precioListaSinIva = sinIvaUsd.multiply(cotizacionDolar).setScale(2, RoundingMode.HALF_UP);

                // Leer y asignar los valores a las propiedades del artículo
                articulo = new Articulo.Builder()
                        .codigoArticulo(getCellValueAsString(row.getCell(columnIndexMap.get("catalogo"))).trim() + ".I")
                        .descripcion(cleanGarbage(getCellValueAsString(row.getCell(columnIndexMap.get("denominacion"))).trim()))
                        .precioListaSinIvaUsd(sinIvaUsd)
                        .precioListaSinIva(precioListaSinIva)
                        .origen(getCellValueAsString(row.getCell(columnIndexMap.get("origen"))))
                        .descuento(getCellValueAsString(row.getCell(columnIndexMap.get("descuento"))).trim())
                        .fechaActualizacion(getCellValueAsOffsetDateTime(row.getCell(columnIndexMap.get("fecha"))))
                        .modeloCamion(getCellValueAsString(row.getCell(columnIndexMap.get("modelo"))).trim())
                        .build();

                // Añadir el artículo a la lista
                articulos.add(articulo);

//                try {
//                    log.debug("Articulo Procesado: {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(common));
//                } catch (JsonProcessingException e) {
//                    log.error("Error al procesar el artículo: {}", e.getMessage());
//                }

            } catch (Exception e) {
                // Manejar errores en el procesamiento de la fila
                log.warn("Error al procesar la fila " + (rowIndex + 1) + ": " + e.getMessage());
                // Puedes decidir si continuar o detener el procesamiento
            }
        }

        // Cerrar el workbook
        workbook.close();

        return articulos;
    }

    // Métodos de utilidad para obtener los valores de las celdas

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Formatear fecha a string si es necesario
                    return cell.getDateCellValue().toString();
                } else {
                    return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                // Evaluar la fórmula si es necesario
                return cell.getCellFormula();
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return "";
        }
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                String value = cell.getStringCellValue().replace(",", ".").trim();
                try {
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO;
                }
            default:
                return BigDecimal.ZERO;
        }
    }

    private OffsetDateTime getCellValueAsOffsetDateTime(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atOffset(ZoneOffset.UTC);
        }
        return null;
    }

    // Método para limpiar caracteres no deseados de una cadena
    private String cleanGarbage(String input) {
        // Implementa la lógica de limpieza según tus necesidades
        return input.replaceAll("[^\\p{Print}]", "");
    }
}
