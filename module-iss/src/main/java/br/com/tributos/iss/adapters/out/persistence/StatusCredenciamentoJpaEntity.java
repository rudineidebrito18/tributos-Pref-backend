package br.com.tributos.iss.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_status_credenciamento")
public class StatusCredenciamentoJpaEntity extends CatalogoIssJpaEntityBase {
}
