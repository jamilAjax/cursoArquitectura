package com.mercaplus.promociones;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LAB 3 - Kata TDD. Quita @Disabled, ve la prueba en ROJO, hazla pasar con el codigo minimo
 * y refactoriza. Luego escribe la siguiente prueba (una por regla de docs/kata-promociones.md).
 */
class MotorPromocionesTest {

    private final MotorPromociones motor = new MotorPromociones(Map.of());

    //@Disabled("Quitar al iniciar el laboratorio")
    @Test
    void regla1_sinPromociones_elTotalEsLaSumaDePrecioPorCantidad() {
        List<Item> carrito = List.of(
                new Item("ARR-5K", "MERCADO", new BigDecimal("21500"), 2, false),
                new Item("LIC-1L", "HOGAR", new BigDecimal("249000"), 1, false));

        BigDecimal total = motor.totalAPagar(carrito, BigDecimal.ZERO);

        assertThat(total).isEqualByComparingTo("292000");
    }
}
