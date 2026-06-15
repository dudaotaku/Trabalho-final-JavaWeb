package com.trabalhoFinal.JavaWeb.Repository;


import com.trabalhoFinal.JavaWeb.Modelo.Eventos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventosRepository extends JpaRepository<Eventos, Long> {
}
