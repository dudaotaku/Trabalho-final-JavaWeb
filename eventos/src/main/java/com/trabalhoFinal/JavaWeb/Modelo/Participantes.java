package com.trabalhoFinal.JavaWeb.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participantes")
public class Participantes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome obrigatório")
    private String nome;

    @Email
    @NotBlank(message = "E-mail obrigatório")
    private String email;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"participante", "senha", "authorities", "password", "username"})
    private Usuario usuario;
}
