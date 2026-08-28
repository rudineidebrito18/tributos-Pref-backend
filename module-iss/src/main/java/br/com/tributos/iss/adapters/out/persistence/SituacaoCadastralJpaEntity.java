package br.com.tributos.iss.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "iss_situacao_cadastral")
public class SituacaoCadastralJpaEntity extends CatalogoIssJpaEntityBase {
}
