package com.example.scrapetok.infraestructure;


import com.example.scrapetok.application.excelService.GenerateExcelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
public class ExcelController {
    @Autowired
    private GenerateExcelService generateExcelService;

    @PostMapping("/excel/download")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody List<Map<String, Object>> request) {
        if (request == null || request.isEmpty()) {
            return ResponseEntity.badRequest().body("⚠️ No hay datos para exportar a Excel.".getBytes());
        }
        try {
            byte[] excelFile = generateExcelService.downloadExcel(request);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "tiktok_metrics.xlsx");
            // Agregar headers específicos para CORS y archivos binarios
            headers.set("Access-Control-Expose-Headers", "Content-Disposition");
            headers.set("Access-Control-Allow-Origin", "*");
            headers.set("Access-Control-Allow-Methods", "POST, OPTIONS");
            headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
            return ResponseEntity.ok().headers(headers).body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("❌ Error al generar el archivo Excel.".getBytes());
        }
    }

    @RequestMapping(value = "/excel/download", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        headers.set("Access-Control-Max-Age", "3600");
        return ResponseEntity.ok().headers(headers).build();
    }

}


