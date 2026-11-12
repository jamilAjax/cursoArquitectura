package com.mercaplus.pedidos.infraestructura.promociones;

import com.mercaplus.pedidos.aplicacion.puerto.salida.PromocionesPort;
import com.mercaplus.pedidos.dominio.LineaPedido;
import com.mercaplus.promociones.Item;
import com.mercaplus.promociones.MotorPromociones;

import java.math.BigDecimal;
import java.util.List;

/** Adaptador que conecta el puerto con el motor de promociones construido con TDD en el Lab 3. */
public class MotorPromocionesAdapter implements PromocionesPort {

    private final MotorPromociones motor;

    public MotorPromocionesAdapter(MotorPromociones motor) {
        this.motor = motor;
    }

    @Override
    public BigDecimal totalConPromociones(List<LineaPedido> lineas, BigDecimal cupon) {
        List<Item> items = lineas.stream()
                .map(l -> new Item(l.sku(), l.categoria(), l.precioUnitario(), l.cantidad(), l.promo2x1()))
                .toList();
        return motor.totalAPagar(items, cupon);
    }
}
