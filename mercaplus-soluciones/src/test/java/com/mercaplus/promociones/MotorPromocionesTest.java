package com.mercaplus.promociones;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * SOLUCION LAB 3 - una prueba por regla, en el orden en que se escribieron con TDD.
 */
class MotorPromocionesTest {

    private static final BigDecimal SIN_CUPON = BigDecimal.ZERO;
    private final MotorPromociones sinDescuentos = new MotorPromociones(Map.of());
    private final MotorPromociones tecnologia20 = new MotorPromociones(Map.of("TECNOLOGIA", new BigDecimal("0.20")));

    private static Item item(String categoria, String precio, int cantidad, boolean promo2x1) {
        return new Item("SKU", categoria, new BigDecimal(precio), cantidad, promo2x1);
    }

    @Test
    void regla1_sinPromociones_sumaPrecioPorCantidad() {
        var carrito = List.of(item("MERCADO", "21500", 2, false), item("HOGAR", "249000", 1, false));
        assertThat(sinDescuentos.totalAPagar(carrito, SIN_CUPON)).isEqualByComparingTo("292000");
    }

    @Nested
    class Regla2_DosPorUno {
        @Test
        void dosUnidades_seCobraUna() {
            assertThat(sinDescuentos.totalAPagar(List.of(item("MERCADO", "32900", 2, true)), SIN_CUPON))
                    .isEqualByComparingTo("32900");
        }

        @Test
        void unidadImpar_seCobra() {
            assertThat(sinDescuentos.totalAPagar(List.of(item("MERCADO", "32900", 3, true)), SIN_CUPON))
                    .isEqualByComparingTo("65800");
        }

        @Test
        void unaSolaUnidad_noTieneDescuento() {
            assertThat(sinDescuentos.totalAPagar(List.of(item("MERCADO", "32900", 1, true)), SIN_CUPON))
                    .isEqualByComparingTo("32900");
        }
    }

    @Test
    void regla3_descuentoPorCategoria() {
        assertThat(tecnologia20.totalAPagar(List.of(item("TECNOLOGIA", "2199000", 1, false)), SIN_CUPON))
                .isEqualByComparingTo("1759200");
    }

    @Test
    void regla3_categoriaSinDescuento_pagaPrecioCompleto() {
        assertThat(tecnologia20.totalAPagar(List.of(item("HOGAR", "249000", 1, false)), SIN_CUPON))
                .isEqualByComparingTo("249000");
    }

    @Test
    void regla4_noAcumulables_ganaLaQueMasAhorra() {
        // 2x1 ahorra 189.900; 20 % ahorra 75.960 -> gana 2x1
        assertThat(tecnologia20.totalAPagar(List.of(item("TECNOLOGIA", "189900", 2, true)), SIN_CUPON))
                .isEqualByComparingTo("189900");
    }

    @Test
    void regla4_noAcumulables_ganaCategoriaSiAhorraMas() {
        // 1 unidad con 2x1: el 2x1 ahorra 0 y el 20 % ahorra 20.000 -> gana categoria
        assertThat(tecnologia20.totalAPagar(List.of(item("TECNOLOGIA", "100000", 1, true)), SIN_CUPON))
                .isEqualByComparingTo("80000");
    }

    @Test
    void regla5_cuponDescuentaDelTotal() {
        assertThat(sinDescuentos.totalAPagar(List.of(item("HOGAR", "100000", 1, false)), new BigDecimal("20000")))
                .isEqualByComparingTo("80000");
    }

    @Test
    void regla5_totalNuncaNegativo_yRegla6_topeDel50() {
        assertThat(sinDescuentos.totalAPagar(List.of(item("HOGAR", "50000", 1, false)), new BigDecimal("80000")))
                .isEqualByComparingTo("25000");
    }

    @Test
    void regla6_ahorroTotalTopadoAl50PorCiento() {
        // 2x1 ahorra 50.000 + cupon 20.000 = 70.000 > 50 % de 100.000
        assertThat(sinDescuentos.totalAPagar(List.of(item("MERCADO", "50000", 2, true)), new BigDecimal("20000")))
                .isEqualByComparingTo("50000");
    }

    @Test
    void regla7_redondeaAPesosHalfUp() {
        assertThat(sinDescuentos.totalAPagar(List.of(item("MERCADO", "10000.5", 1, false)), SIN_CUPON))
                .isEqualByComparingTo("10001");
    }

    @Test
    void regla8_cantidadNoPositiva_esInvalida() {
        assertThatThrownBy(() -> sinDescuentos.totalAPagar(List.of(item("MERCADO", "1000", 0, false)), SIN_CUPON))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void regla8_precioNegativo_esInvalido() {
        assertThatThrownBy(() -> sinDescuentos.totalAPagar(List.of(item("MERCADO", "-1", 1, false)), SIN_CUPON))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
