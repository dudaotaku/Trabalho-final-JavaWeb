package com.trabalhoFinal.JavaWeb.Security;

import com.trabalhoFinal.JavaWeb.Repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailService (UsuarioRepository repository){
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

}
