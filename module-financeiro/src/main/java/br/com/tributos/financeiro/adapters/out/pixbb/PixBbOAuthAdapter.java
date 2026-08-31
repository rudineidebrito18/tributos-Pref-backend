package br.com.tributos.financeiro.adapters.out.pixbb;

import org.springframework.stereotype.Component;

import br.com.tributos.kernel.pixbb.CredenciaisPixBb;
import br.com.tributos.kernel.pixbb.PixBbOAuthPort;
import br.com.tributos.kernel.pixbb.ResultadoTesteConexaoPixBb;
import br.com.tributos.kernel.pixbb.ResultadoTokenPixBb;

@Component
public class PixBbOAuthAdapter implements PixBbOAuthPort {

    private final BbOAuthClient bbOAuthClient;

    public PixBbOAuthAdapter(BbOAuthClient bbOAuthClient) {
        this.bbOAuthClient = bbOAuthClient;
    }

    @Override
    public ResultadoTokenPixBb obterToken(CredenciaisPixBb credenciais) {
        return bbOAuthClient.obterToken(credenciais);
    }

    @Override
    public ResultadoTesteConexaoPixBb testarConexao(CredenciaisPixBb credenciais) {
        try {
            ResultadoTokenPixBb token = bbOAuthClient.obterToken(credenciais);
            return new ResultadoTesteConexaoPixBb(true, token.expiresIn(), token.scope(), null);
        } catch (BbOAuthFalhaException ex) {
            return new ResultadoTesteConexaoPixBb(false, null, null, ex.getMessage());
        }
    }
}
