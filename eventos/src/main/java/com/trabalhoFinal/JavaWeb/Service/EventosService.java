package com.trabalhoFinal.JavaWeb.Service;

import com.trabalhoFinal.JavaWeb.Exception.RegistroNaoEncontradoException;
import com.trabalhoFinal.JavaWeb.Modelo.Eventos;
import com.trabalhoFinal.JavaWeb.Repository.EventosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventosService {

    private EventosRepository repository;

    public EventosService(EventosRepository repository){this.repository = repository;}

    public Eventos salvar(Eventos ev){return repository.save(ev);}

    public Eventos buscarOne (Long id){
        return repository.findById(id).orElseThrow(()-> new RegistroNaoEncontradoException("Evento não encontrado"));
    }

    public List<Eventos> listarEventos (){return repository.findAll();}

    public void excluirEvento(Long id){
        buscarOne(id);
        repository.deleteById(id);
    }

    public Eventos alterar(Eventos dados, Long id){
        Eventos ev = buscarOne(id);
        ev.setNome(dados.getNome());
        ev.setDescricao(dados.getDescricao());
        ev.setData(dados.getData());
        ev.setLocal(dados.getLocal());
        ev.setVagas(dados.getVagas());

        return repository.save(ev);
    }
}
