package com.mercaplus.pedidos.infraestructura.persistencia;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido_v2")
public class PedidoJpaEntity {

    @Id
    private String id;
    private String clienteId;
    private Instant creadoEn;
    private BigDecimal totalAPagar;
    private String estado;
    private String referenciaPago;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pedido_v2_linea", joinColumns = @JoinColumn(name = "pedido_id"))
    private List<LineaEmbeddable> lineas = new ArrayList<>();

    @Embeddable
    public static class LineaEmbeddable {
        public String sku;
        public String categoria;
        public int cantidad;
        public BigDecimal precioUnitario;
        public boolean promo2x1;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
    public BigDecimal getTotalAPagar() { return totalAPagar; }
    public void setTotalAPagar(BigDecimal totalAPagar) { this.totalAPagar = totalAPagar; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }
    public List<LineaEmbeddable> getLineas() { return lineas; }
}
