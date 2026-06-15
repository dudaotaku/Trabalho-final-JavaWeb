package com.trabalhoFinal.JavaWeb.Service;

import com.trabalhoFinal.JavaWeb.Exception.RegistroNaoEncontradoException;
import com.trabalhoFinal.JavaWeb.Modelo.Eventos;
import com.trabalhoFinal.JavaWeb.Modelo.Usuario;
import com.trabalhoFinal.JavaWeb.Repository.EventosRepository;
import com.trabalhoFinal.JavaWeb.Repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private UsuarioRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public UsuarioService(UsuarioRepository repository){this.repository = repository;}

    public Usuario salvar(Usuario user){
        user.setSenha(encoder.encode(user.getSenha()));
        return repository.save(user);
    }

    public Usuario buscarOne (Long id){
        return repository.findById(id).orElseThrow(()-> new RegistroNaoEncontradoException("Evento não encontrado"));
    }

    public Usuario buscarPorLogin(String login) {
        return repository.findByLogin(login)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Usuário não encontrado"));
    }

    public List<Usuario> listarUsuario (){return repository.findAll();}

    public void excluirUsuario (Long id){
        buscarOne(id);
        repository.deleteById(id);
    }

    public Usuario alterar(Usuario dados, Long id){
        Usuario user = buscarOne(id);
        user.setLogin(dados.getLogin());
        user.setSenha(encoder.encode(dados.getSenha()));
        return repository.save(user);
    }
}
