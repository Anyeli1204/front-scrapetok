package com.example.scrapetok.application.excelService;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class GenerateExcelService {
    public byte[] downloadExcel(List<Map<String, Object>> data) throws IOException {
            System.out.println("📊 ExcelService - Datos recibidos: " + data.size() + " registros");
            
            // Verificar que hay datos
            if (data == null || data.isEmpty()) {
                throw new IOException("No hay datos para generar el Excel");
            }
            
            // Usa Apache POI para crear y llenar un archivo Excel
            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                // Crear una hoja llamada "Tiktok Métricas"
                XSSFSheet sheet = workbook.createSheet("Tiktok Métricas");
                // Obtiene los nombres de las columnas a partir del primer elemento
                List<String> columns = new ArrayList<>(data.get(0).keySet());
                // === Estilo de cabecera ===
                CellStyle cabeceraStyle = workbook.createCellStyle();
                cabeceraStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                cabeceraStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                Font cabeceraFont = workbook.createFont();
                cabeceraFont.setBold(true);
                cabeceraStyle.setFont(cabeceraFont);

                cabeceraStyle.setAlignment(HorizontalAlignment.CENTER);
                cabeceraStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                cabeceraStyle.setWrapText(true);

                // Borde grueso en cabeceras
                cabeceraStyle.setBorderTop(BorderStyle.THICK);
                cabeceraStyle.setBorderBottom(BorderStyle.THICK);
                cabeceraStyle.setBorderLeft(BorderStyle.THICK);
                cabeceraStyle.setBorderRight(BorderStyle.THICK);

                // === Estilo para celdas de contenido ===
                CellStyle contenidoStyle = workbook.createCellStyle();
                contenidoStyle.setAlignment(HorizontalAlignment.CENTER);
                contenidoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                contenidoStyle.setWrapText(true);

                // === Estilo para celdas de fecha ===
                CellStyle dateStyle = workbook.createCellStyle();
                dateStyle.setAlignment(HorizontalAlignment.CENTER);
                dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Formato de fecha "dd/MM/yyyy"
                CreationHelper createHelper = workbook.getCreationHelper();
                dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

                // Agregar cabeceras
                Row headerRow = sheet.createRow(0);
                for (int col = 0; col < columns.size(); col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(columns.get(col)); //Establece el nombre de la columna
                    cell.setCellStyle(cabeceraStyle);  // Aplica estilo a la cabecera
                }

                // === Estilo NUMERIC
                CellStyle numericStyle = workbook.createCellStyle();
                DataFormat format = workbook.createDataFormat();
                numericStyle.setDataFormat(format.getFormat("0")); // o "0" si no quieres decimales
                numericStyle.setAlignment(HorizontalAlignment.CENTER);
                numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Añadir los datos al Excel
                int rowIndex = 1; // La fila 0 es para cabeceras
                System.out.println("🔄 Procesando " + data.size() + " filas de datos...");
                
                for (int i = 0; i < data.size(); i++) {
                    Map<String, Object> rowData = data.get(i);
                    Row row = sheet.createRow(rowIndex++);
                    
                    // Log de progreso cada 5 filas
                    if (i % 5 == 0) {
                        System.out.println("📝 Procesando fila " + (i + 1) + " de " + data.size());
                    }
                    
                    for (int col = 0; col < columns.size(); col++) {
                        try {
                            Cell cell = row.createCell(col);
                            String columnName = columns.get(col);
                            Object value = rowData.get(columns.get(col));
                            
                            if (value != null) {
                                if ("views".equalsIgnoreCase(columnName) || "likes".equalsIgnoreCase(columnName) || "comments".equalsIgnoreCase(columnName) || "reposted".equalsIgnoreCase(columnName) || "saves".equalsIgnoreCase(columnName) || "Engagement rate".equalsIgnoreCase(columnName) || "interactions".equalsIgnoreCase(columnName) || "numberOfHashtags".equalsIgnoreCase(columnName)) {
                                    if (value instanceof Number) {
                                        cell.setCellValue(((Number) value).doubleValue());
                                        cell.setCellStyle(numericStyle);
                                    } else {
                                        cell.setCellValue(value.toString());
                                        cell.setCellStyle(contenidoStyle);
                                    }
                                }
                                // Si la columna es "Date posted" o "Tracking date"
                                else if ("datePosted".equalsIgnoreCase(columnName) || "trackingDate".equalsIgnoreCase(columnName) || "trackingTime".equalsIgnoreCase(columnName) || "timePosted".equalsIgnoreCase(columnName) ) {
                                    if (value instanceof Date) {
                                        cell.setCellValue((Date) value);
                                    }
                                    else if (value instanceof String) {
                                        try {
                                            LocalDate parsedDate = LocalDate.parse(value.toString());
                                            cell.setCellValue(java.sql.Date.valueOf(parsedDate));
                                        } catch (Exception e) {
                                            // Si falla el parseo, lo guardamos como texto
                                            cell.setCellValue(value.toString());
                                        }
                                    }
                                    else {
                                        // Si el tipo de dato no es compatible, lo guardamos como texto
                                        cell.setCellValue(value.toString());
                                    }
                                    cell.setCellStyle(dateStyle); // Aplicar formato de fecha
                                } else {
                                    // Otras columnas (texto)
                                    cell.setCellValue(value.toString());
                                    cell.setCellStyle(contenidoStyle);
                                }
                            } else {
                                cell.setCellValue("");
                                cell.setCellStyle(contenidoStyle);
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error procesando celda en fila " + i + ", columna " + col + ": " + e.getMessage());
                            // Continuar con la siguiente celda
                        }
                    }
                }

                // Ajustar automáticamente el ancho de las columnas
                final int MAX_COLUMN_WIDTH = 65280;
                for (int col = 0; col < columns.size(); col++) {
                    sheet.autoSizeColumn(col);

                    // Obtenemos el ancho calculado
                    int currentWidth = sheet.getColumnWidth(col);
                    // Sumamos un poco para que se vea más espacioso
                    int extraWidth = 2000;  // Ajusta según tu preferencia
                    int newWidth = currentWidth + extraWidth;
                    // Evitamos superar el límite máximo
                    if (newWidth > MAX_COLUMN_WIDTH) {
                        newWidth = MAX_COLUMN_WIDTH;
                    }
                    // Establecemos el nuevo ancho
                    sheet.setColumnWidth(col, newWidth);
                }

                // Ajustar el alto de las filas (por defecto, desde la 1 hasta la última)
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        // Ajusta la altura a 50 puntos (puedes cambiarlo)
                        row.setHeightInPoints(45);
                    }
                }

                System.out.println("🔄 Escribiendo archivo Excel...");
                
                // Obtener timestamp para generar nombre único del archivo
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                String fileName = "tiktok_videos_" + timestamp + ".xlsx";

                // Configurar los encabezados HTTP para la descarga
                workbook.write(outputStream);
                
                byte[] result = outputStream.toByteArray();
                System.out.println("✅ Excel generado exitosamente: " + fileName);
                System.out.println("📁 Tamaño del archivo: " + result.length + " bytes");
                
                return result;
            }
        }
}

