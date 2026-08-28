package br.com.tributos.cadastro.domain;

import java.util.List;
import java.util.UUID;

public record Cidade(UUID id, String nome, String uf, String codigoIbge) {
}
