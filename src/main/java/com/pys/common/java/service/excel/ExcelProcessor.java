package com.pys.common.java.service.excel;

import com.pys.common.kotlin.model.Articulo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Component
public class ExcelProcessor {
    private static final int HEADER_ROW_INDEX = 4;
    private static final int STARTING_ROW = 5;
    private static final List<String> REQUIRED_COLUMNS = Arrays.asList(
            "catalogo", "origen", "sinIva", "descuento", 
            "denominacion", "fecha", "modelo"
    );

    public List<Articulo> processExcelFile(InputStream inputStream, BigDecimal cotizacionDolar) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> columnMap = getColumnIndexMap(sheet);
            validateRequiredColumns(columnMap);
            return processRows(sheet, columnMap, cotizacionDolar);
        }
    }

    private Map<String, Integer> getColumnIndexMap(Sheet sheet) {
        Row headerRow = Optional.ofNullable(sheet.getRow(HEADER_ROW_INDEX))
                .orElseThrow(() -> new IllegalStateException("Header row not found"));

        Map<String, Integer> columnMap = new HashMap<>();
        headerRow.forEach(cell -> {
            String cellValue = getCellValueAsString(cell);
            mapColumnHeader(cellValue, cell.getColumnIndex(), columnMap);
        });

        return columnMap;
    }

    private void mapColumnHeader(String cellValue, int columnIndex, Map<String, Integer> columnMap) {
        switch (cellValue) {
            case "Catálogo" -> columnMap.put("catalogo", columnIndex);
            case "0" -> columnMap.put("origen", columnIndex);
            case "s/IVA" -> columnMap.put("sinIva", columnIndex);
            case "D" -> columnMap.put("descuento", columnIndex);
            case "Denominación" -> columnMap.put("denominacion", columnIndex);
            case "Fecha" -> columnMap.put("fecha", columnIndex);
            case "Modelo" -> columnMap.put("modelo", columnIndex);
        }
    }

    private void validateRequiredColumns(Map<String, Integer> columnMap) {
        Set<String> missingColumns = new HashSet<>();
        REQUIRED_COLUMNS.forEach(column -> {
            if (!columnMap.containsKey(column)) {
                missingColumns.add(column);
            }
        });

        if (!missingColumns.isEmpty()) {
            throw new IllegalStateException("Missing required columns: " + missingColumns);
        }
    }

    private List<Articulo> processRows(Sheet sheet, Map<String, Integer> columnMap, BigDecimal cotizacionDolar) {
        List<Articulo> articulos = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();

        for (int rowIndex = STARTING_ROW; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (isRowEmpty(row)) continue;

            try {
                Articulo articulo = processRow(row, columnMap, cotizacionDolar);
                articulos.add(articulo);
            } catch (Exception e) {
                log.warn("Error processing row {}: {}", rowIndex + 1, e.getMessage());
            }
        }

        return articulos;
    }

    private boolean isRowEmpty(Row row) {
        return row == null || row.getPhysicalNumberOfCells() == 0;
    }

    private Articulo processRow(Row row, Map<String, Integer> columnMap, BigDecimal cotizacionDolar) {
        BigDecimal sinIvaUsd = getCellValueAsBigDecimal(row.getCell(columnMap.get("sinIva")));
        BigDecimal precioListaSinIva = sinIvaUsd.multiply(cotizacionDolar)
                .setScale(2, RoundingMode.HALF_UP);

        return new Articulo.Builder()
                .codigoArticulo(getCellValueAsString(row.getCell(columnMap.get("catalogo"))).trim() + ".I")
                .descripcion(cleanGarbage(getCellValueAsString(row.getCell(columnMap.get("denominacion"))).trim()))
                .precioListaSinIvaUsd(sinIvaUsd)
                .precioListaSinIva(precioListaSinIva)
                .origen(getCellValueAsString(row.getCell(columnMap.get("origen"))))
                .descuento(getCellValueAsString(row.getCell(columnMap.get("descuento"))).trim())
                .fechaActualizacion(getCellValueAsOffsetDateTime(row.getCell(columnMap.get("fecha"))))
                .modeloCamion(getCellValueAsString(row.getCell(columnMap.get("modelo"))).trim())
                .build();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            }
            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        
        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                try {
                    yield new BigDecimal(cell.getStringCellValue().replace(",", ".").trim());
                } catch (NumberFormatException e) {
                    yield BigDecimal.ZERO;
                }
            }
            default -> BigDecimal.ZERO;
        };
    }

    private OffsetDateTime getCellValueAsOffsetDateTime(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atOffset(ZoneOffset.UTC);
        }
        return null;
    }

    private String cleanGarbage(String input) {
        return input.replaceAll("[^\\p{Print}]", "");
    }
} 