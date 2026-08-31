package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_habitese_tipo")
public class ImovelHabiteseTipoJpaEntity extends CatalogoIptuJpaEntityBase {

    @Column(length = 200)
    private String titulo;

    @Column(name = "permite_desconto", nullable = false)
    private boolean permiteDesconto;

    @Column(name = "habilita_calculo_valor", nullable = false)
    private boolean habilitaCalculoValor;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor = BigDecimal.ZERO;

    @Column(length = 200)
    private String secretaria;

    @Column(length = 200)
    private String cargo;

    @Column(name = "assinatura_documento_id")
    private UUID assinaturaDocumentoId;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isPermiteDesconto() {
        return permiteDesconto;
    }

    public void setPermiteDesconto(boolean permiteDesconto) {
        this.permiteDesconto = permiteDesconto;
    }

    public boolean isHabilitaCalculoValor() {
        return habilitaCalculoValor;
    }

    public void setHabilitaCalculoValor(boolean habilitaCalculoValor) {
        this.habilitaCalculoValor = habilitaCalculoValor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public UUID getAssinaturaDocumentoId() {
        return assinaturaDocumentoId;
    }

    public void setAssinaturaDocumentoId(UUID assinaturaDocumentoId) {
        this.assinaturaDocumentoId = assinaturaDocumentoId;
    }
}
