package br.com.matheusramos.minhasfinancas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.matheusramos.minhasfinancas.model.entity.Usuario;

/**
 * @author Matheus
 * */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	boolean existsByEmail(String email);

	Optional<Usuario> findByEmail(String email);
	
}
