/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tienda_v1.service;


import java.io.IOException;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author valer
 */
public interface ReporteService {
    //Se define la firma de metodo que genera los reportes,con los siguientes paramentros
    //1. Reporte: Es el nombre del archivo de reporte (.jasper)
    //2. Un mapa que contiene los parametros del reporte
    //3. Es el tipo: El tipo de reporte: vPDF, PDF , Xls , Csv
    
    public ResponseEntity<Resource> generaReporte(
           String reporte,
           Map<String,Object>parametros,
           String tipo) throws IOException;

            
    
}
