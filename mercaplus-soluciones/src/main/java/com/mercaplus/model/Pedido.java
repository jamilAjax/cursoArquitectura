package com.mercaplus.model;

import com.mercaplus.util.PasarelaPagosClient;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clienteId;
    private LocalDateTime fecha = LocalDateTime.now();
    private String estado = "NUEVO";
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private String referenciaPago;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LineaPedido> lineas = new ArrayList<>();

    public void pagar() {
        String ref = PasarelaPagosClient.cobrar(clienteId, total);
        if (ref != null) {
            this.referenciaPago = ref;
            this.estado = "PAGADO";
        } else {
            this.estado = "PAGO_RECHAZADO";
        }
    }

    public void agregarLinea(LineaPedido linea) {
        linea.setPedido(this);
        lineas.add(linea);
    }

    public Long getId() { return id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public String getReferenciaPago() { return referenciaPago; }
    public List<LineaPedido> getLineas() { return lineas; }
}
