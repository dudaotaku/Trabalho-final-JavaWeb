package com.trabalhoFinal.JavaWeb.Service;

import com.trabalhoFinal.JavaWeb.Exception.DadosInvalidosException;
import com.trabalhoFinal.JavaWeb.Exception.EventoSemVagasException;
import com.trabalhoFinal.JavaWeb.Modelo.Eventos;
import com.trabalhoFinal.JavaWeb.Modelo.Inscricoes;
import com.trabalhoFinal.JavaWeb.Modelo.Participantes;
import com.trabalhoFinal.JavaWeb.Repository.EventosRepository;
import com.trabalhoFinal.JavaWeb.Repository.InscricoesRepository;
import com.trabalhoFinal.JavaWeb.Repository.ParticipantesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InscricoesService {

    private final InscricoesRepository repository;
    private final EventosService eventosService;
    private final ParticipantesService participantesService;


    public InscricoesService(InscricoesRepository repository, EventosService eventosService, ParticipantesService participantesService) {
        this.repository = repository;
        this.eventosService = eventosService;
        this.participantesService = participantesService;
    }

    public Inscricoes inscrever(Long eventoId, Long participanteId) {
        Eventos evento = eventosService.buscarOne(eventoId);

        Participantes participante = participantesService.buscarOne(participanteId);

        long inscritos = repository.countByEventos_Id(eventoId);
        if (inscritos >= evento.getVagas()) {
            throw new EventoSemVagasException("Evento sem vagas disponíveis");
        }

        boolean usuarioEstaInscrito = repository.existsByEventos_IdAndParticipantes_Id(eventoId, participanteId);
        if (usuarioEstaInscrito) {
            throw new DadosInvalidosException("Participante já inscrito neste evento");
        }

        Inscricoes inscricao = new Inscricoes();
        inscricao.setEventos(evento);
        inscricao.setParticipantes(participante);
        return repository.save(inscricao);
    }

    public List<Participantes> listarParticipantesPorEvento(Long eventoId) {
        eventosService.buscarOne(eventoId);
        return repository.findByEventos_Id(eventoId).stream().map(Inscricoes::getParticipantes).toList();
    }

    public List<Eventos> listarEventosPorParticipante(Long participanteId) {
        participantesService.buscarOne(participanteId);
        return repository.findByParticipantes_Id(participanteId).stream().map(Inscricoes::getEventos).toList();
    }

    public List<Inscricoes> listarInscricoes() {
        return repository.findAll();
    }
}
