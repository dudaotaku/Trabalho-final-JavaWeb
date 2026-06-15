package com.trabalhoFinal.JavaWeb.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.trabalhoFinal.JavaWeb.Utils.Perfil;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "user_name" ,unique = true)
    @NotBlank(message = "Login é obrigatória")
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @Enumerated(EnumType.ORDINAL)
    private Perfil perfil;

    @OneToOne
    @JsonIgnoreProperties({"usuario", "senha", "authorities", "password"})
    private Participantes participante;

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() { return senha; }

    public String getUsername() { return login; }



}
