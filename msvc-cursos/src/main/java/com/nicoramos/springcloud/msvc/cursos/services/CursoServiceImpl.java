package com.nicoramos.springcloud.msvc.cursos.services;

import com.nicoramos.springcloud.msvc.cursos.clients.UsuarioClientRest;
import com.nicoramos.springcloud.msvc.cursos.models.Usuario;
import com.nicoramos.springcloud.msvc.cursos.models.entity.Curso;
import com.nicoramos.springcloud.msvc.cursos.models.entity.CursoUsuario;
import com.nicoramos.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CursoServiceImpl implements CursoService {
    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioClientRest clientUsuarioRest;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) cursoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Curso> porId(Long id) {
        return cursoRepository.findById(id);
    }

    @Transactional
    @Override
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long usuarioId) {
        cursoRepository.eliminarCursoUsuarioPorId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        Optional<Curso> optionalCurso = cursoRepository.findById(id);
        if (optionalCurso.isPresent()) {
            Curso curso = optionalCurso.get();
            if (!curso.getCursoUsuarios().isEmpty()) {
                List<Long> ids = curso.getCursoUsuarios().stream()
                        .map(CursoUsuario::getUsuarioId)
                        .toList();
                List<Usuario> usuarios = clientUsuarioRest.obtenerAlumnosPorCurso(ids);
                curso.setUsuarios(usuarios);
            }
                return Optional.of(curso);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        // Buscar el curso por ID
        Optional<Curso> cursoOptional = cursoRepository.findById(cursoId);
        if (cursoOptional.isPresent()) {
            // Llamar al microservicio de usuarios para obtener los detalles del usuario
            Usuario usuarioMsvc = clientUsuarioRest.detalle(usuario.getId());
            // Obtenemos el curso
            Curso curso = cursoOptional.get();
            boolean yaAsignado = curso.getCursoUsuarios().stream()
                    .anyMatch(cu -> Objects.equals(cu.getUsuarioId(), usuarioMsvc.getId()));
            if (yaAsignado) {
                return Optional.of(usuarioMsvc);
            }
            // Crear una nueva instancia de CursoUsuario y establecer el usuarioId
            CursoUsuario cursoUsuario = new CursoUsuario();
            // Asignar el ID del usuario obtenido del microservicio
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            // Agregar la relación al curso
            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();

    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> cursoOptional = cursoRepository.findById(cursoId);
        if (cursoOptional.isPresent()) {
            Usuario usuarioNuevoMsvc = clientUsuarioRest.crear(usuario);

            Curso curso = cursoOptional.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioNuevoMsvc.getId());

            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioNuevoMsvc);
        }
        return Optional.empty();

    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuarioDelCurso(Usuario usuario, Long cursoId) {
        Optional<Curso> cursoOptional = cursoRepository.findById(cursoId);
        if (cursoOptional.isPresent()) {
            Usuario usuarioMsvc = clientUsuarioRest.detalle(usuario.getId());
            Curso curso = cursoOptional.get();
            CursoUsuario cursoUsuario = new CursoUsuario();

            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            curso.removeCursoUsuario(cursoUsuario);

            cursoRepository.save(curso);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }
}
