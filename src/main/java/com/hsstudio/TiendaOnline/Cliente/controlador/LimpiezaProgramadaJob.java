package com.hsstudio.TiendaOnline.Cliente.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LimpiezaProgramadaJob {
    private static final Logger logger = LoggerFactory.getLogger(LimpiezaProgramadaJob.class);

    @Autowired
    private LimpiezaDatosService limpiezaDatosService;

    @Scheduled(cron = "0 * * * * ?") // Se ejecuta cada minuto
    public void ejecutarLimpiezaProgramada() {
        logger.info("Tarea de limpieza programada iniciada");
        try {
            limpiezaDatosService.limpiarDatosAntiguos();
            logger.info("Tarea de limpieza programada completada con éxito");
        } catch (Exception e) {
            logger.error("Error durante la ejecución de la tarea de limpieza programada", e);
        }
    }
}