ALTER TABLE guia_arrecadacao ADD COLUMN status_pix VARCHAR(40)
    CHECK (status_pix IN ('ATIVA','CONCLUIDA','EM_PROCESSAMENTO','NAO_REALIZADO',
                          'DEVOLVIDO','REMOVIDA_PELO_USUARIO_RECEBEDOR',
                          'REMOVIDA_PELO_PSP','ATUALIZACAO_MANUAL'));
ALTER TABLE guia_arrecadacao ADD COLUMN pix_qrcode_payload TEXT;
ALTER TABLE guia_arrecadacao ADD COLUMN pix_link TEXT;
ALTER TABLE guia_arrecadacao ADD COLUMN pix_end_to_end_id VARCHAR(40);
ALTER TABLE guia_arrecadacao ADD COLUMN pix_solicitado_em TIMESTAMPTZ;
CREATE INDEX idx_guia_pix_txid ON guia_arrecadacao(pix_txid) WHERE pix_txid IS NOT NULL;
