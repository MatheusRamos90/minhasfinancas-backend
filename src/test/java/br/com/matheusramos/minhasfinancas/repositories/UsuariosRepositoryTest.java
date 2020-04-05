package br.com.matheusramos.minhasfinancas.repositories;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.matheusramos.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class) // Extensão dada para uso do JUnit 5 em vez de @RunWith
@ActiveProfiles("test")
/*
 * @DataJpaTest
 * Cria uma instância do banco de dados em memória e ao finalizar a bateria teste é encerrado a mesma instancia
 * Toda vez que se inicia um teste é criado uma transação, e quando é finalizado é feito um rollback, ou seja é desfeito tudo o que foi feito na transação
 * */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuariosRepositoryTest {

	@Autowired
	private UsuarioRepository usuarioRepository;
		
	@Autowired
	private TestEntityManager entityManager;
	
	/*
	 * Verificar se usuário existe a partir de um e-mail
	 * */
	@Test
	public void verificaEmailExiste() {
		Usuario usuario = criaUsuario();
//		usuarioRepository.save(usuario);
		entityManager.persist(usuario);
		
		boolean result = usuarioRepository.existsByEmail(usuario.getEmail());
		
		Assertions.assertThat(result).isTrue();
	}
	
	/*
	 * Verificar se usuário existe a partir de um e-mail que não está cadastrado
	 * */
	@Test
	public void retornaFalsoUsuarioNaoCadastroComEmail() {
//		usuarioRepository.deleteAll();
		boolean usuario = usuarioRepository.existsByEmail("matheus.hrs@live.com");
		Assertions.assertThat(usuario).isFalse();
	}
	
	/**
	 * Deve persistir um usuário na base de dados
	 * */
	@Test
	public void persistirUsuario() {		
		Usuario usuarioSalvo = usuarioRepository.save(criaUsuario());
		
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	/**
	 * Deve buscar um usuário por email
	 * */
	@Test
	public void buscaUsuarioPorEmail() {
		Usuario usuario = criaUsuario();
		entityManager.persist(usuario);
		
		Optional<Usuario> usuarioExiste = usuarioRepository.findByEmail("matheus.hrs@live.com");
		
		Assertions.assertThat(usuarioExiste.isPresent()).isTrue();
	}
	
	/**
	 * Deve buscar um usuário que não existe pelo email
	 * */
	@Test
	public void buscaUsuarioPorEmailNaoExistente() {		
		Optional<Usuario> usuarioExiste = usuarioRepository.findByEmail("matheus.hrs@live.com");
		
		Assertions.assertThat(usuarioExiste.isPresent()).isFalse();
	}
	
	public static Usuario criaUsuario() {
		Usuario usuario = Usuario.builder().nome("Matheus").email("matheus.hrs@live.com").senha("senha").build();
		return usuario;
	}
}
