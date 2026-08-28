package br.com.tributos.iss.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_regime_tributario")
public class RegimeTributarioJpaEntity extends CatalogoIssJpaEntityBase {
}
