package br.com.tributos.identity.domain;

import java.util.UUID;

import br.com.tributos.kernel.exception.ValidationException;

/**
 * Usuário de acesso à plataforma, sempre vinculado a um tenant (não confundir com
 * "Pessoa" do Cadastro Único — este é o ator que loga no sistema, aquele é o
 * contribuinte/imóvel). Carrega a lógica de habilitação de MFA como comportamento de
 * domínio (não CRUD anêmico): o segredo TOTP só passa a valer para autenticação depois de
 * {@link #confirmarHabilitacaoMfa()}, para não travar o usuário fora da conta se ele
 * escanear o QR code mas nunca confirmar o primeiro código.
 */
public final class Usuario {

    private final UUID id;
    private final UUID tenantId;
    private String nome;
    private String login;
    private String email;
    private UUID fotoDocumentoId;
    private String senhaHash;
    private boolean mfaHabilitado;
    private TipoMfa mfaTipo;
    private String mfaSecret;
    private boolean ativo;

    public Usuario(
        UUID id,
        UUID tenantId,
        String nome,
        String login,
        String email,
        UUID fotoDocumentoId,
        String senhaHash,
        boolean mfaHabilitado,
        TipoMfa mfaTipo,
        String mfaSecret,
        boolean ativo
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.nome = nome;
        this.login = login;
        this.email = email;
        this.fotoDocumentoId = fotoDocumentoId;
        this.senhaHash = senhaHash;
        this.mfaHabilitado = mfaHabilitado;
        this.mfaTipo = mfaTipo;
        this.mfaSecret = mfaSecret;
        this.ativo = ativo;
    }

    /** Guarda o segredo gerado para o usuário escanear o QR code — MFA ainda não está ativo. */
    public void iniciarHabilitacaoMfa(String segredoTotp) {
        if (mfaHabilitado) {
            throw new ValidationException("MFA já está habilitado para este usuário — desabilite antes de reconfigurar.");
        }
        this.mfaTipo = TipoMfa.TOTP;
        this.mfaSecret = segredoTotp;
    }

    /** Chamado só depois que o usuário prova, com um código válido, que configurou o app corretamente. */
    public void confirmarHabilitacaoMfa() {
        if (mfaSecret == null) {
            throw new ValidationException("Nenhuma habilitação de MFA em andamento para confirmar.");
        }
        this.mfaHabilitado = true;
    }

    public void desabilitarMfa() {
        this.mfaHabilitado = false;
        this.mfaTipo = TipoMfa.NENHUM;
        this.mfaSecret = null;
    }

    public void atualizarPerfil(String nome, String login, String email) {
        this.nome = nome;
        this.login = login;
        this.email = email;
    }

    public void definirFotoDocumentoId(UUID fotoDocumentoId) {
        this.fotoDocumentoId = fotoDocumentoId;
    }

    public void trocarSenha(String novoHash) {
        this.senhaHash = novoHash;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public UUID getFotoDocumentoId() {
        return fotoDocumentoId;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public boolean isMfaHabilitado() {
        return mfaHabilitado;
    }

    public TipoMfa getMfaTipo() {
        return mfaTipo;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
