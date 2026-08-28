package br.com.tributos.iss.adapters.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContribuinteAtividadeServicoJpaRepository
    extends JpaRepository<ContribuinteAtividadeServicoJpaEntity, UUID> {

    List<ContribuinteAtividadeServicoJpaEntity> findByContribuinteIdOrderByAtividadeIdAscServicoIdAsc(UUID contribuinteId);

    boolean existsByContribuinteIdAndAtividadeIdAndServicoId(
        UUID contribuinteId,
        UUID atividadeId,
        UUID servicoId
    );
}
