/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda_v1.service.service.impl;

import com.tienda_v1.service.ReporteService;
import jakarta.mail.Quota;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.web.servlet.function.RequestPredicates.headers;

@Service
public class ReporteServiceImpl implements ReporteService{

    
    @Autowired
    private DataSource dataSource;
        
    @Override
    public ResponseEntity<Resource> generaReporte (
            String reporte,
            Map<String, Object> parametros,
            String tipo) throws IOException{
        
        try {
            String estilo = tipo.equalsIgnoreCase("vPdf") ? "inline; " : "attachment; ";
            
            String reportePath="reportes";
            
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            
            ClassPathResource fuente = new ClassPathResource(
                    
                    reportePath+
                            File.separator+
                            reporte+
                            ".jasper");
            
            InputStream elReporte = fuente.getInputStream();
            
            var reporteJasper = JasperFillManager.fillReport(elReporte, parametros, dataSource.getConnection());
            
            MediaType mediaType = null;
            String archivoSalida="";
            
            switch (tipo){
                case "Pdf", "vPdf" -> {
                    JasperExportManager
                            .exportReportToPdfStream(elReporte, salida);
                    mediaType = MediaType.APPLICATION_PDF;
                    archivoSalida=reporte+".pdf";     
                }
                case "Xls" ->{
                    JRXlsxExporter paraExcel = new JRXlsxExporter();
                    paraExcel.setExporterInput(
                            new SimpleExporterInput(reporteJasper));
                    
                    paraExcel.setExporterOutput(
                            new SimpleOutputStreamExporterOutput(salida));
                    
                    SimpleXlsxReportConfiguration configuracion=
                            new SimpleXlsxReportConfiguration();
                    configuracion.setDetectCellType(true);
                    configuracion.setCollapseRowSpan(true);
                    
                    paraExcel.setConfiguration(configuracion);
                    paraExcel.exportReport();

                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    archivoSalida = reporte + ".xlsx"; 
                }
                
                case "Cvs" ->{
                    JRCsvExporter paraCsv = new JRCsvExporter();
                    paraCsv.setExporterInput(
                            new SimpleExporterInput(reporteJasper));

                    paraCsv.setExporterOutput(
                            new SimpleWriterExporterOutput(salida));
                    paraCsv.exportReport();

                    mediaType = MediaType.TEXT_PLAIN;
                    archivoSalida = reporte + ".cvs";
                }
            }
            
byte[] data = salida.toByteArray();
HttpHeaders headers = new HttpHeaders();
headers.set("Content-Disposition", estilo + "filename=\"" + archivoSalida + "\"");

return ResponseEntity
        .ok()
        .headers(headers)
        .contentLength(data.length)
        .contentType(mediaType)
        .body(new InputStreamResource(new ByteArrayInputStream(data)));

          
        } catch (SQLException | JRException ex) {
           ex.printStackTrace();
        }
          return null;
    }

    
}


