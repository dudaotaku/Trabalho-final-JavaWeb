package com.trabalhoFinal.JavaWeb.Controller;

import com.trabalhoFinal.JavaWeb.Modelo.Eventos;
import com.trabalhoFinal.JavaWeb.Modelo.Inscricoes;
import com.trabalhoFinal.JavaWeb.Modelo.Participantes;
import com.trabalhoFinal.JavaWeb.Service.InscricoesService;
import com.trabalhoFinal.JavaWeb.Service.ParticipantesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incricoes")
public class InscricoesController {

    private final InscricoesService service;

    public InscricoesController(InscricoesService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> inscrever(@RequestParam Long eventoId, @RequestParam Long participanteId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.inscrever(eventoId, participanteId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/eventos/{id}/participantes")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> listarParticipantesPorEvento(@PathVariable Long id) {
        try {
            List<Participantes> participantes = service.listarParticipantesPorEvento(id);
            return ResponseEntity.status(HttpStatus.OK).body(participantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/participantes/{id}/eventos")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> listarEventosPorParticipante(@PathVariable Long id) {
        try {
            List<Eventos> eventos = service.listarEventosPorParticipante(id);
            return ResponseEntity.status(HttpStatus.OK).body(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarInscricoes() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.listarInscricoes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

