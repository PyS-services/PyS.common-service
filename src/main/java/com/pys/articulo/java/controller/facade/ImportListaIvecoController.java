package com.pys.articulo.java.controller.facade;

import com.pys.articulo.java.controller.facade.status.ProcessStatusDto;
import com.pys.articulo.java.service.facade.ImportListaIvecoService;
import com.pys.articulo.java.service.facade.status.ProcessStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/articulo-service/import/listaIveco")
@Slf4j
public class ImportListaIvecoController {

    private final ImportListaIvecoService service;

    public ImportListaIvecoController(ImportListaIvecoService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("cotizacionDolar") String cotizacionDolarString) {
        // Verifica si el archivo está vacío
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha subido ningún archivo");
        }

        // Verifica si el archivo tiene extensión .xlsx
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Por favor, sube un archivo con extensión .xlsx");
        }

        try {
            // Genera un ID único para este procesamiento
            String processId = service.generateProcessId();

            // Leer el contenido del archivo
            byte[] fileContent = file.getBytes();

            // Inicia el procesamiento asíncrono del archivo
            service.processFileAsync(fileContent, processId, cotizacionDolarString);

            // Devuelve el ID del proceso al cliente
            return ResponseEntity.ok(processId);
        } catch (Exception e) {
            log.error("Error al iniciar el procesamiento del archivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al iniciar el procesamiento del archivo");
        }
    }

    @GetMapping("/status/{processId}")
    public ResponseEntity<ProcessStatusDto> getStatus(@PathVariable String processId) {
        ProcessStatus processStatus = service.getStatus(processId);
        if (processStatus == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        ProcessStatusDto dto = new ProcessStatusDto(processStatus.getStatus(), processStatus.getProgress());
        return ResponseEntity.ok(dto);
    }

}
