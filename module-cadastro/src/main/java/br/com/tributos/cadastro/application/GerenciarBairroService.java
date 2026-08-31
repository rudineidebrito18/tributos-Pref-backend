package br.com.tributos.cadastro.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.Bairro;
import br.com.tributos.cadastro.domain.BairroRepository;
import br.com.tributos.cadastro.domain.TerritorioRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarBairroService {

    private final BairroRepository bairroRepository;
    private final TerritorioRepository territorioRepository;

    public GerenciarBairroService(BairroRepository bairroRepository, TerritorioRepository territorioRepository) {
        this.bairroRepository = bairroRepository;
        this.territorioRepository = territorioRepository;
    }

    @Transactional(readOnly = true)
    public List<Bairro> listar(UUID cidadeId) {
        validarCidadeExiste(cidadeId);
        return bairroRepository.listar(cidadeId);
    }

    @Transactional(readOnly = true)
    public Bairro buscar(UUID id) {
        return bairroRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Bairro não encontrado."));
    }

    @Transactional
    public Bairro criar(UUID cidadeId, String nome, UUID zonaFiscalId, BigDecimal valorTerreno) {
        validarCidadeExiste(cidadeId);
        validarNome(nome);
        String nomeNormalizado = nome.trim();
        if (bairroRepository.existePorNome(cidadeId, nomeNormalizado, null)) {
            throw new ValidationException("Já existe um bairro com este nome nesta cidade.");
        }
        UUID tenantId = TenantContext.getObrigatorio();
        Bairro bairro = new Bairro(
            UUID.randomUUID(), tenantId, cidadeId, nomeNormalizado, zonaFiscalId, valorTerreno, null
        );
        return bairroRepository.salvar(bairro);
    }

    @Transactional
    public Bairro atualizar(UUID id, String nome, UUID zonaFiscalId, BigDecimal valorTerreno) {
        validarNome(nome);
        Bairro existente = buscar(id);
        String nomeNormalizado = nome.trim();
        if (bairroRepository.existePorNome(existente.cidadeId(), nomeNormalizado, id)) {
            throw new ValidationException("Já existe um bairro com este nome nesta cidade.");
        }
        Bairro atualizado = new Bairro(
            existente.id(), existente.tenantId(), existente.cidadeId(),
            nomeNormalizado, zonaFiscalId, valorTerreno, existente.criadoEm()
        );
        return bairroRepository.salvar(atualizado);
    }

    @Transactional
    public void excluir(UUID id) {
        if (bairroRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Bairro não encontrado.");
        }
        bairroRepository.excluir(id);
    }

    private void validarCidadeExiste(UUID cidadeId) {
        if (territorioRepository.buscarCidadePorId(cidadeId).isEmpty()) {
            throw new ValidationException("Cidade informada não existe.");
        }
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe a descrição do bairro.");
        }
    }
}
