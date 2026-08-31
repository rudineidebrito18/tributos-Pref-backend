package br.com.tributos.iss.adapters.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

public interface AtividadeServicoViewProjection {

    UUID getId();

    String getCnae();

    String getCodigo();

    String getServico();

    BigDecimal getAliquota();

    boolean getTributavel();

    boolean getDeducao();

    boolean getRetencao();

    String getIncidencia();
}
