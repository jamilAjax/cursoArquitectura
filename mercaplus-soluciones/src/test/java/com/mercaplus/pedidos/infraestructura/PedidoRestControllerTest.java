package com.mercaplus.pedidos.infraestructura;

import com.mercaplus.pedidos.aplicacion.puerto.entrada.CrearPedidoUseCase;
import com.mercaplus.pedidos.dominio.PedidoInvalidoException;
import com.mercaplus.pedidos.infraestructura.rest.ManejadorErrores;
import com.mercaplus.pedidos.infraestructura.rest.PedidoRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Prueba de integracion del adaptador REST: valida contrato HTTP y validacion de entrada. */
@WebMvcTest(PedidoRestController.class)
@Import(ManejadorErrores.class)
class PedidoRestControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean CrearPedidoUseCase crearPedido;

    @Test
    void creaPedidoYDevuelve201() throws Exception {
        when(crearPedido.ejecutar(any())).thenReturn(new CrearPedidoUseCase.ResultadoPedido(
                "p-1", "PAGADO", new BigDecimal("65800"), new BigDecimal("32900"), new BigDecimal("32900"), "PY-1"));

        mvc.perform(post("/api/v2/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":\"c-1\",\"items\":[{\"sku\":\"CAF-500\",\"cantidad\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PAGADO"));
    }

    @Test
    void skuConCaracteresPeligrososEsRechazadoAntesDeLlegarAlNucleo() throws Exception {
        mvc.perform(post("/api/v2/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":\"c-1\",\"items\":[{\"sku\":\"' OR '1'='1\",\"cantidad\":1}]}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(crearPedido);
    }

    @Test
    void reglaDeNegocioVioladaDevuelve422() throws Exception {
        when(crearPedido.ejecutar(any())).thenThrow(new PedidoInvalidoException("Sin stock suficiente: CAF-500"));

        mvc.perform(post("/api/v2/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":\"c-1\",\"items\":[{\"sku\":\"CAF-500\",\"cantidad\":99}]}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
