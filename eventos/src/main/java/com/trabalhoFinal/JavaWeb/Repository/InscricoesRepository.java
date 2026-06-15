package com.trabalhoFinal.JavaWeb.Repository;

import com.trabalhoFinal.JavaWeb.Modelo.Inscricoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscricoesRepository extends JpaRepository<Inscricoes, Long> {

    List<Inscricoes> findByEventos_Id(Long eventoId);

    List<Inscricoes> findByParticipantes_Id(Long participanteId);

    long countByEventos_Id(Long eventoId);

    boolean existsByEventos_IdAndParticipantes_Id(Long eventoId, Long participanteId);

}
