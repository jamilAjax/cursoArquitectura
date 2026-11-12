package com.mercaplus.controller;

import com.mercaplus.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** SOLUCION LAB 4: la prueba que demuestra que el ataque del auditor ya no funciona. */
@WebMvcTest(ProductoController.class)
class ProductoControllerSeguridadTest {

    @Autowired MockMvc mvc;
    @MockitoBean ProductoRepository repositorio;

    @Test
    void inyeccionSqlEsRechazadaConError400() throws Exception {
        mvc.perform(get("/api/productos/buscar").param("nombre", "' OR '1'='1"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(repositorio);
    }

    @Test
    void busquedaLegitimaFunciona() throws Exception {
        when(repositorio.findTop50ByNombreContainingIgnoreCase("cafe")).thenReturn(List.of());
        mvc.perform(get("/api/productos/buscar").param("nombre", "cafe"))
                .andExpect(status().isOk());
    }
}
