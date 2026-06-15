package com.trabalhoFinal.JavaWeb.Repository;

import com.trabalhoFinal.JavaWeb.Modelo.Participantes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantesRepository extends JpaRepository<Participantes, Long> {

    Optional<Participantes> findByUsuario_Id(Long usuarioId);

}
