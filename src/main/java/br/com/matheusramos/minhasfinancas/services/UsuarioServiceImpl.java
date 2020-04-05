package br.com.matheusramos.minhasfinancas.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.matheusramos.minhasfinancas.exceptions.ErroAuthException;
import br.com.matheusramos.minhasfinancas.exceptions.RegraNegocioException;
import br.com.matheusramos.minhasfinancas.interfaces.UsuarioService;
import br.com.matheusramos.minhasfinancas.model.entity.Usuario;
import br.com.matheusramos.minhasfinancas.repositories.UsuarioRepository;

/**
 * @author Matheus
 * */
@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private UsuarioRepository usuarioRepository;
	
	public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		
		if (!usuario.isPresent()) {
			throw new ErroAuthException("Usuário não encontrado para o email informado");
		}
		
		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAuthException("Senha de usuário inválida");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return usuarioRepository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = usuarioRepository.existsByEmail(email);
		if (existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email");
		}
	}
	
}
