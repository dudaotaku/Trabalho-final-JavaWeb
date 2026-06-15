package com.trabalhoFinal.JavaWeb;

import com.trabalhoFinal.JavaWeb.Modelo.Usuario;
import com.trabalhoFinal.JavaWeb.Repository.UsuarioRepository;
import com.trabalhoFinal.JavaWeb.Utils.Perfil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EventosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventosApplication.class, args);
	}

	@Bean
	public CommandLineRunner carregarDados(UsuarioRepository repository, BCryptPasswordEncoder encoder) {
		return args -> {

			if (repository.findByLogin("admin").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setLogin("admin");
				admin.setSenha(encoder.encode("123"));
				admin.setPerfil(Perfil.ADMIN);
				repository.save(admin);
			}

			if (repository.findByLogin("user").isEmpty()) {
				Usuario user = new Usuario();
				user.setLogin("user");
				user.setSenha(encoder.encode("123"));
				user.setPerfil(Perfil.USER);
				repository.save(user);
			}
		};
	}
}
