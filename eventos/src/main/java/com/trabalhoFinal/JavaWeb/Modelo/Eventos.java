package com.trabalhoFinal.JavaWeb.Modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "eventos")
public class Eventos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome obrigatório")
    private String nome;

    private String descricao;

    @NotBlank(message = "Data obrigatório")
    private Date data;

    @NotBlank(message = "Local obrigatório")
    private String local;

    @NotBlank(message = "Quantidade de vagas maior que zero")
    private int vagas;
}
