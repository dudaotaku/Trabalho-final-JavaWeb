package com.trabalhoFinal.JavaWeb.Controller;

import com.trabalhoFinal.JavaWeb.Modelo.Eventos;
import com.trabalhoFinal.JavaWeb.Service.EventosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    private final EventosService service;

    public EventosController(EventosService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarEvento(@RequestBody Eventos part){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(part));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Object> buscarUm (@PathVariable Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.buscarOne(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Object> listarEventos (){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.listarEventos());
        }catch (Exception e ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarEventos(@RequestBody Eventos dados, @PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.alterar(dados, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> ExcluirEvento(@PathVariable Long id) {
        try {
            service.excluirEvento(id);
            return ResponseEntity.status(HttpStatus.OK).body("Evento excluido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
