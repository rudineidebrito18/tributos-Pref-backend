package br.com.tributos.financeiro.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.tributos.financeiro.domain.FormaPagamento;
import br.com.tributos.financeiro.domain.FormaPagamentoRepository;

@Component
public class FormaPagamentoRepositoryAdapter implements FormaPagamentoRepository {

    private final FormaPagamentoJpaRepository jpaRepository;

    public FormaPagamentoRepositoryAdapter(FormaPagamentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<FormaPagamento> buscarPorCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo).map(this::paraDominio);
    }

    @Override
    public Optional<FormaPagamento> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    private FormaPagamento paraDominio(FormaPagamentoJpaEntity e) {
        return new FormaPagamento(e.getId(), e.getCodigo(), e.getNome());
    }
}
