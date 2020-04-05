package br.com.matheusramos.minhasfinancas.interfaces;

import br.com.matheusramos.minhasfinancas.model.entity.Usuario;

/**
 * @author Matheus
 * */
public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
}
