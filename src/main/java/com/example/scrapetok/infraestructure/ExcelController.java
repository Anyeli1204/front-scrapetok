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

    @GetMapping("/excel/test")
    public ResponseEntity<String> testExcelEndpoint() {
        System.out.println("🧪 Excel test endpoint called");
        return ResponseEntity.ok("✅ Excel endpoint is working!");
    }

    @PostMapping("/excel/download")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody List<Map<String, Object>> request) {
        System.out.println("🚀 Excel download endpoint called");
        System.out.println("📊 Request data size: " + (request != null ? request.size() : "null"));
        
        if (request == null || request.isEmpty()) {
            System.out.println("⚠️ Request is null or empty");
            return ResponseEntity.badRequest().body("⚠️ No hay datos para exportar a Excel.".getBytes());
        }
        
        try {
            System.out.println("🔄 Starting Excel generation...");
            
            // Agregar timeout para evitar que se cuelgue
            long startTime = System.currentTimeMillis();
            byte[] excelFile = generateExcelService.downloadExcel(request);
            long endTime = System.currentTimeMillis();
            
            System.out.println("⏱️ Excel generation took: " + (endTime - startTime) + "ms");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "tiktok_metrics.xlsx");
            
            System.out.println("✅ Excel download request processed successfully");
            System.out.println("📁 File size: " + excelFile.length + " bytes");
            
            return ResponseEntity.ok().headers(headers).body(excelFile);
        } catch (IOException e) {
            System.err.println("❌ Error generating Excel file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error al generar el archivo Excel.".getBytes());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error generating Excel file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error inesperado al generar el archivo Excel.".getBytes());
        }
    }

}


