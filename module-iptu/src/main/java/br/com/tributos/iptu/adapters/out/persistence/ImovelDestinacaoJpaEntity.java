package br.com.tributos.iptu.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_destinacao")
public class ImovelDestinacaoJpaEntity extends CatalogoIptuJpaEntityBase {

    @Column(name = "tipo_imovel_id")
    private UUID tipoImovelId;

    @Column(name = "aliquota_iptu", nullable = false, precision = 10, scale = 6)
    private BigDecimal aliquotaIptu = BigDecimal.ZERO;

    public UUID getTipoImovelId() {
        return tipoImovelId;
    }

    public void setTipoImovelId(UUID tipoImovelId) {
        this.tipoImovelId = tipoImovelId;
    }

    public BigDecimal getAliquotaIptu() {
        return aliquotaIptu;
    }

    public void setAliquotaIptu(BigDecimal aliquotaIptu) {
        this.aliquotaIptu = aliquotaIptu;
    }
}
