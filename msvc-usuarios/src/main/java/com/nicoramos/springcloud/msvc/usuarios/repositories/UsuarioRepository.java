package com.nicoramos.springcloud.msvc.usuarios.repositories;

import com.nicoramos.springcloud.msvc.usuarios.models.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    @Query("select u from Usuario u where u.email=?1")
    Optional<Usuario> porEmail(String email);

    boolean existsByEmail(String email);
}
