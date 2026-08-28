package br.com.tributos.iss.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_tipo_contribuinte")
public class TipoContribuinteJpaEntity extends CatalogoIssJpaEntityBase {
}
