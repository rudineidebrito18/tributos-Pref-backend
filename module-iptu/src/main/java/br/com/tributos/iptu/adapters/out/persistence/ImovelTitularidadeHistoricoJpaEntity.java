package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.tributos.iptu.domain.TipoRegistroTitularidade;

@Entity
@Table(name = "imovel_titularidade_historico")
public class ImovelTitularidadeHistoricoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "imovel_id", nullable = false)
    private UUID imovelId;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false, length = 20)
    private TipoRegistroTitularidade tipoRegistro;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal porcentagem;

    @Column(name = "data_registro", nullable = false, updatable = false, insertable = false)
    private Instant dataRegistro;

    protected ImovelTitularidadeHistoricoJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getImovelId() {
        return imovelId;
    }

    public void setImovelId(UUID imovelId) {
        this.imovelId = imovelId;
    }

    public UUID getContribuinteId() {
        return contribuinteId;
    }

    public void setContribuinteId(UUID contribuinteId) {
        this.contribuinteId = contribuinteId;
    }

    public TipoRegistroTitularidade getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(TipoRegistroTitularidade tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public BigDecimal getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(BigDecimal porcentagem) {
        this.porcentagem = porcentagem;
    }

    public Instant getDataRegistro() {
        return dataRegistro;
    }
}
