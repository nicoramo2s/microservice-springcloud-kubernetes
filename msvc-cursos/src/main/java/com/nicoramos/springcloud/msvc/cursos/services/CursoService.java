package com.nicoramos.springcloud.msvc.cursos.services;

import com.nicoramos.springcloud.msvc.cursos.models.Usuario;
import com.nicoramos.springcloud.msvc.cursos.models.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoService {
    List<Curso> listar();
    Optional<Curso> porId(Long id);
    Curso guardar(Curso curso);
    void eliminar(Long id);
    void eliminarCursoUsuarioPorId(Long usuarioId);
    Optional<Curso> porIdConUsuarios(Long id);
    Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> eliminarUsuarioDelCurso(Usuario usuario, Long cursoId);
}
