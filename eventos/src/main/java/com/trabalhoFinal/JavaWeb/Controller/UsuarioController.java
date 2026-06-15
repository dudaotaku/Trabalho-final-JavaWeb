package com.trabalhoFinal.JavaWeb.Controller;

import com.trabalhoFinal.JavaWeb.Modelo.Usuario;
import com.trabalhoFinal.JavaWeb.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarUsuario (@RequestBody Usuario usuario){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(usuario));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> buscarUm (@PathVariable Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.buscarOne(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> listarUsuario (){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.listarUsuario());
        }catch (Exception e ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizarUsuarios(@RequestBody Usuario dados, @PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.alterar(dados, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> ExcluirUsuario(@PathVariable Long id) {
        try {
            service.excluirUsuario(id);
            return ResponseEntity.status(HttpStatus.OK).body("Usuario excluida com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> meuPerfil(Authentication authentication) {
        try {
            return ResponseEntity.ok(service.buscarPorLogin(authentication.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
