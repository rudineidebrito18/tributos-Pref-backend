package br.com.tributos.iptu.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "imovel_destinacao")
public class ImovelDestinacaoJpaEntity extends CatalogoIptuJpaEntityBase {
}
