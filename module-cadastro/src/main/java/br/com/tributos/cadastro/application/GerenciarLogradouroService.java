package br.com.tributos.cadastro.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tributos.cadastro.domain.BairroRepository;
import br.com.tributos.cadastro.domain.Logradouro;
import br.com.tributos.cadastro.domain.LogradouroRepository;
import br.com.tributos.cadastro.domain.TerritorioRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.exception.ValidationException;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class GerenciarLogradouroService {

    private final LogradouroRepository logradouroRepository;
    private final TerritorioRepository territorioRepository;
    private final BairroRepository bairroRepository;

    public GerenciarLogradouroService(
        LogradouroRepository logradouroRepository,
        TerritorioRepository territorioRepository,
        BairroRepository bairroRepository
    ) {
        this.logradouroRepository = logradouroRepository;
        this.territorioRepository = territorioRepository;
        this.bairroRepository = bairroRepository;
    }

    @Transactional(readOnly = true)
    public List<Logradouro> listar(UUID cidadeId, UUID bairroId) {
        validarCidadeExiste(cidadeId);
        return logradouroRepository.listar(cidadeId, bairroId);
    }

    @Transactional(readOnly = true)
    public Logradouro buscar(UUID id) {
        return logradouroRepository.buscarPorId(id)
            .orElseThrow(() -> new NotFoundException("Logradouro não encontrado."));
    }

    @Transactional
    public Logradouro criar(UUID cidadeId, UUID bairroId, String tipo, String nome, String cep) {
        validarCidadeExiste(cidadeId);
        validarBairroOpcional(bairroId, cidadeId);
        validarNome(nome);
        UUID tenantId = TenantContext.getObrigatorio();
        Logradouro logradouro = new Logradouro(
            UUID.randomUUID(), tenantId, cidadeId, bairroId,
            normalizar(tipo), nome.trim(), normalizarCep(cep), null
        );
        return logradouroRepository.salvar(logradouro);
    }

    @Transactional
    public Logradouro atualizar(UUID id, UUID bairroId, String tipo, String nome, String cep) {
        validarNome(nome);
        Logradouro existente = buscar(id);
        validarBairroOpcional(bairroId, existente.cidadeId());
        Logradouro atualizado = new Logradouro(
            existente.id(), existente.tenantId(), existente.cidadeId(), bairroId,
            normalizar(tipo), nome.trim(), normalizarCep(cep), existente.criadoEm()
        );
        return logradouroRepository.salvar(atualizado);
    }

    @Transactional
    public void excluir(UUID id) {
        if (logradouroRepository.buscarPorId(id).isEmpty()) {
            throw new NotFoundException("Logradouro não encontrado.");
        }
        logradouroRepository.excluir(id);
    }

    private void validarCidadeExiste(UUID cidadeId) {
        if (territorioRepository.buscarCidadePorId(cidadeId).isEmpty()) {
            throw new ValidationException("Cidade informada não existe.");
        }
    }

    private void validarBairroOpcional(UUID bairroId, UUID cidadeId) {
        if (bairroId == null) {
            return;
        }
        bairroRepository.buscarPorId(bairroId)
            .filter(b -> b.cidadeId().equals(cidadeId))
            .orElseThrow(() -> new ValidationException("Bairro informado não pertence à cidade."));
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidationException("Informe a descrição do logradouro.");
        }
    }

    private static String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private static String normalizarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return null;
        }
        return cep.replaceAll("\\D", "");
    }
}
