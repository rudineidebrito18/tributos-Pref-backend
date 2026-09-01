package br.com.tributos.itbi.domain;

import java.util.UUID;

public interface ContribuinteReferenciaRepository {

    boolean existe(UUID contribuinteId);
}
