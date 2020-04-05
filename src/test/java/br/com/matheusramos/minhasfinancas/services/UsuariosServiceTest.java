package br.com.matheusramos.minhasfinancas.services;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.matheusramos.minhasfinancas.exceptions.ErroAuthException;
import br.com.matheusramos.minhasfinancas.exceptions.RegraNegocioException;
import br.com.matheusramos.minhasfinancas.model.entity.Usuario;
import br.com.matheusramos.minhasfinancas.repositories.UsuarioRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuariosServiceTest {

	@SpyBean
	private UsuarioServiceImpl service;
	
	@MockBean
	private UsuarioRepository repository;
	
//	@BeforeEach
//	public void setUp() {
//		service = new UsuarioServiceImpl(repository);
//	}
	
	/*
	 * Deve validar um usuário já cadastrado (email/senha válidos)
	 * */
	@Test
	public void validarUsuarioCadastrado() {
		String email = "matheus.hrs@live.com";
		String senha = "123";
		
		Usuario usuario = criaUsuario();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		Usuario resultado = service.autenticar(email, senha);
		
		Assertions.assertThat(resultado).isNotNull();
	}
	
	/*
	 * Mostrar erro informando usuário inexistente
	 * */
	@Test
	public void erroUsuarioNaoExiste() {
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable thrown = Assertions.catchThrowable(() -> service.autenticar("matheus.hrs@live.com", "123"));
		Assertions.assertThat(thrown).isInstanceOf(ErroAuthException.class).hasMessage("Usuário não encontrado para o email informado");
	}

	/*
	 * Mostra exceção quando não há usuario cadastrado para um email
	 * */
	@Test
	public void validarEmailTest() {		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		service.validarEmail("matheus.hrs@live.com");
	}
	
	/*
	 * Mostra exceção quando a senha esta incorreta para um usuario já cadastrado
	 * */
	@Test
	public void lancaExceptionQuandoSenhaIncorreta() {
		Usuario usuario = criaUsuario();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		Throwable thrown = Assertions.catchThrowable(() -> service.autenticar("matheus.hrs@live.com", "567"));
		Assertions.assertThat(thrown).isInstanceOf(ErroAuthException.class).hasMessage("Senha de usuário inválida");
	}
	
	/*
	 * Salvar um usuário testando a inserção
	 * */
	@Test
	public void salvarUsuario() {
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString()); // teste unitário do método 'validarEmail' do service mockado através do Spy
		Usuario usuario = criaUsuario(); // logo depois é criado um usuário
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario); // testado o save e o retorno pra ser bate os mesmos tipos
		
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("Matheus");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("matheus.hrs@live.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("123");
	}
	
	/*
	 * Erro ao salvar um usuário
	 * */
	@Test
	public void salvarUsuarioException() {
		try {
			String email = "matheus.hrs@live.com";
			Usuario usuario = Usuario.builder().email("matheus.hrs@live.com").build();
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
			
			service.salvarUsuario(usuario);
			
			Mockito.verify(repository, Mockito.never()).save(usuario);
		} catch (RegraNegocioException e) {
			
		}
	}
	
	@Test
	public void lancaExceptionAoExistirEmailJaCadastradoTest() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		Throwable thrown = Assertions.catchThrowable(() -> service.validarEmail("matheus.hrs@live.com"));
		Assertions.assertThat(thrown).isInstanceOf(RegraNegocioException.class).hasMessage("Já existe um usuário cadastrado com este email");
	}
	
	public static Usuario criaUsuario() {
		Usuario usuario = Usuario.builder().nome("Matheus").email("matheus.hrs@live.com").senha("123").id(1L).build();
		return usuario;
	}

}
