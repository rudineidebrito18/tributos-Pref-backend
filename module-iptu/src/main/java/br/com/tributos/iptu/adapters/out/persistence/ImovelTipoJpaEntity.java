package br.com.tributos.iptu.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_tipo")
public class ImovelTipoJpaEntity extends CatalogoIptuJpaEntityBase {
}
