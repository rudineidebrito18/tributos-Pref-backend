package br.com.tributos.identity.application;

import org.springframework.stereotype.Service;

import br.com.tributos.identity.domain.AmbientePixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBb;
import br.com.tributos.identity.domain.ConfiguracaoPixBbRepository;
import br.com.tributos.kernel.exception.NotFoundException;
import br.com.tributos.kernel.pixbb.CredenciaisPixBb;
import br.com.tributos.kernel.pixbb.PixBbOAuthPort;
import br.com.tributos.kernel.pixbb.ResultadoTesteConexaoPixBb;
import br.com.tributos.kernel.tenancy.TenantContext;

@Service
public class TestarConexaoPixBbService {

    private final ConfiguracaoPixBbRepository configuracaoRepository;
    private final PixBbOAuthPort pixBbOAuthPort;

    public TestarConexaoPixBbService(
        ConfiguracaoPixBbRepository configuracaoRepository,
        PixBbOAuthPort pixBbOAuthPort
    ) {
        this.configuracaoRepository = configuracaoRepository;
        this.pixBbOAuthPort = pixBbOAuthPort;
    }

    public ResultadoTesteConexaoPixBb executar(AmbientePixBb ambiente) {
        var tenantId = TenantContext.getObrigatorio();
        ConfiguracaoPixBb configuracao = configuracaoRepository.buscarPorTenantEAmbiente(tenantId, ambiente)
            .orElseThrow(() -> new NotFoundException(
                "Configuração PIX do ambiente \"" + ambiente.name() + "\" não encontrada."
            ));
        return pixBbOAuthPort.testarConexao(mapearCredenciais(configuracao));
    }

    static CredenciaisPixBb mapearCredenciais(ConfiguracaoPixBb configuracao) {
        return new CredenciaisPixBb(
            configuracao.getTenantId(),
            configuracao.getAmbiente().name(),
            configuracao.getClientId(),
            configuracao.getClientSecret(),
            configuracao.getEscopos(),
            configuracao.getCertificadoPath(),
            configuracao.getCertificadoSenha()
        );
    }
}
