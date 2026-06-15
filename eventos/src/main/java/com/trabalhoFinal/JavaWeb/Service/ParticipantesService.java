package com.trabalhoFinal.JavaWeb.Service;

import com.trabalhoFinal.JavaWeb.Exception.RegistroNaoEncontradoException;
import com.trabalhoFinal.JavaWeb.Modelo.Participantes;
import com.trabalhoFinal.JavaWeb.Repository.ParticipantesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ParticipantesService {


    private ParticipantesRepository repository;

    public ParticipantesService(ParticipantesRepository repository){this.repository = repository;}

    public Participantes salvar(Participantes part){return repository.save(part);}

    public Participantes buscarOne (Long id){
        return repository.findById(id).orElseThrow(()-> new RegistroNaoEncontradoException("Evento não encontrado"));
    }

    public Participantes buscarPorUsuario(Long usuarioId) {
        return repository.findByUsuario_Id(usuarioId).orElseThrow(() -> new RegistroNaoEncontradoException("Participante não vinculado a este usuário"));
    }

    public List<Participantes> listarParticipantes (){return repository.findAll();}

    public void excluirParticipantes(Long id){
        buscarOne(id);
        repository.deleteById(id);
    }

    public Participantes alterar(Participantes dados, Long id){

        Participantes part = buscarOne(id);
        part.setNome(dados.getNome());
        part.setEmail(dados.getEmail());
        part.setUsuario(dados.getUsuario());
        return repository.save(part);
    }

}
