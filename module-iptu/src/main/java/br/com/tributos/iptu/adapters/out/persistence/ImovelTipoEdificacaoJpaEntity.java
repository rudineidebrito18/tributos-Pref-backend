package br.com.tributos.iptu.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_tipo_edificacao")
public class ImovelTipoEdificacaoJpaEntity extends CatalogoIptuJpaEntityBase {
}
