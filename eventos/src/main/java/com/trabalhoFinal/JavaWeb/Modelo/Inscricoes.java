package com.trabalhoFinal.JavaWeb.Modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inscricoes")
public class Inscricoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Eventos eventos;

    @ManyToOne
    @JoinColumn(name = "participante_id", nullable = false)
    private Participantes participantes;

}
