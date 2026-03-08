package com.nicoramos.springcloud.msvc.cursos.controllers;

import com.nicoramos.springcloud.msvc.cursos.models.Usuario;
import com.nicoramos.springcloud.msvc.cursos.models.entity.Curso;
import com.nicoramos.springcloud.msvc.cursos.services.CursoService;
import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class CursoControllers {
    @Autowired
    private CursoService cursoService;

    @GetMapping
    public List<Curso> listar() {
        return cursoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Curso> optionalCurso = cursoService.porId(id);
        if (optionalCurso.isPresent()) {
            return ResponseEntity.ok(optionalCurso.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, @PathVariable Long id) {
        Optional<Curso> o = cursoService.porId(id);
        if (o.isPresent()) {
            Curso cursoDb = o.get();
            cursoDb.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(cursoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso) {
        Curso cursorBD = cursoService.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursorBD);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Curso> o = cursoService.porId(id);
        if (o.isPresent()) {
            cursoService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        try {
            Optional<Usuario> optionalUsuario = cursoService.asignarUsuario(usuario, cursoId);
            if (optionalUsuario.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(optionalUsuario.get());
            }
            return ResponseEntity.notFound().build();

        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje", "No existe el usuario por el id o error en la comunicacion: " + e.getMessage()));
        }
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        try {
            Optional<Usuario> optionalUsuario = cursoService.crearUsuario(usuario, cursoId);
            if (optionalUsuario.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(optionalUsuario.get());
            }
            return ResponseEntity.notFound().build();

        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje", "No existe el usuario por el id o error en la comunicacion: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        try {
            Optional<Usuario> optionalUsuario = cursoService.eliminarUsuarioDelCurso(usuario, cursoId);
            if (optionalUsuario.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(optionalUsuario.get());
            }
            return ResponseEntity.notFound().build();

        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje", "No existe el usuario por el id o error en la comunicacion: " + e.getMessage()));
        }
    }

}
