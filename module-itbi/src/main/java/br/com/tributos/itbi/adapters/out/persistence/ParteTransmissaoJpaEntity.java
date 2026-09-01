package br.com.tributos.itbi.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.tributos.itbi.domain.PapelParteTransmissao;

@Entity
@Table(name = "itbi_guia_parte")
public class ParteTransmissaoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "guia_id", nullable = false)
    private UUID guiaId;

    @Column(name = "contribuinte_id", nullable = false)
    private UUID contribuinteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private PapelParteTransmissao papel;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal porcentagem;

    @Column(nullable = false)
    private boolean principal;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public UUID getGuiaId() { return guiaId; }
    public void setGuiaId(UUID guiaId) { this.guiaId = guiaId; }
    public UUID getContribuinteId() { return contribuinteId; }
    public void setContribuinteId(UUID contribuinteId) { this.contribuinteId = contribuinteId; }
    public PapelParteTransmissao getPapel() { return papel; }
    public void setPapel(PapelParteTransmissao papel) { this.papel = papel; }
    public BigDecimal getPorcentagem() { return porcentagem; }
    public void setPorcentagem(BigDecimal porcentagem) { this.porcentagem = porcentagem; }
    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
}
