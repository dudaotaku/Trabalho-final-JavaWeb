package com.trabalhoFinal.JavaWeb.Controller;

import com.trabalhoFinal.JavaWeb.Modelo.Participantes;
import com.trabalhoFinal.JavaWeb.Modelo.Usuario;
import com.trabalhoFinal.JavaWeb.Service.ParticipantesService;
import com.trabalhoFinal.JavaWeb.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/participantes")
public class ParticipantesController {

    private final ParticipantesService service;
    private final UsuarioService usuarioService;

    public ParticipantesController(ParticipantesService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarParticipantes(@RequestBody Participantes part){
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
    public ResponseEntity<Object> listarParticipantes (){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.listarParticipantes());
        }catch (Exception e ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarParticipantes(@RequestBody Participantes dados, @PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.alterar(dados, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> ExcluirParticipantes(@PathVariable Long id) {
        try {
            service.excluirParticipantes(id);
            return ResponseEntity.status(HttpStatus.OK).body("Participante excluido com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> meuParticipante(Authentication authentication) {
        try {
            Usuario user = usuarioService.buscarPorLogin(authentication.getName()); // ✅
            return ResponseEntity.ok(service.buscarPorUsuario(user.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
