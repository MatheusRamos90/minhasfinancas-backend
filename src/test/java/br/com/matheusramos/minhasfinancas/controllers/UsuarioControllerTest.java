package br.com.matheusramos.minhasfinancas.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.matheusramos.minhasfinancas.dtos.UsuarioDTO;
import br.com.matheusramos.minhasfinancas.exceptions.ErroAuthException;
import br.com.matheusramos.minhasfinancas.exceptions.RegraNegocioException;
import br.com.matheusramos.minhasfinancas.interfaces.LancamentoService;
import br.com.matheusramos.minhasfinancas.interfaces.UsuarioService;
import br.com.matheusramos.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;

	@Autowired
	MockMvc mvc;

	@MockBean
	UsuarioService usuarioService;
	@MockBean
	LancamentoService lancamentoService;

	@Test
	public void autenticarUsuario() throws Exception {

		// cenário
		String email = "usuario@live.com";
		String senha = "123";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); // foi passado por HTTP na requisição
																					// front/back
		Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build(); // usuário cadastrado na base de
																						// dados

		Mockito.when(usuarioService.autenticar(email, senha)).thenReturn(usuario);
		String json = new ObjectMapper().writeValueAsString(dto);

		// execução e verificação

		// aqui fica a requisição para API
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
				.contentType(JSON).content(json);

		// aqui fica o response retornado da API - no caso 200 - SUCCESS
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

	}

	@Test
	public void autenticarUsuarioException() throws Exception {
		// cenário
		String email = "usuario@live.com";
		String senha = "123";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();																						
		Mockito.when(usuarioService.autenticar(email, senha)).thenThrow(ErroAuthException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);

		// execução e verificação

		// aqui fica a requisição para API
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
				.contentType(JSON).content(json);

		// aqui fica o response retornado da API - no caso 400 - BAD REQUEST
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void salvarUsuario() throws Exception {

		// cenário
		String email = "usuario@live.com";
		String senha = "123";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
																				
		Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();																				

		Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		String json = new ObjectMapper().writeValueAsString(dto);

		// execução e verificação

		// aqui fica a requisição para API
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON)
				.contentType(JSON).content(json);

		// aqui fica o response retornado da API - no caso 200 - SUCCESS
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

	}
	
	@Test
	public void salvarUsuarioException() throws Exception {
		// cenário
		String email = "usuario@live.com";
		String senha = "123";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();																						
		Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);

		// execução e verificação

		// aqui fica a requisição para API
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON)
				.contentType(JSON).content(json);

		// aqui fica o response retornado da API - no caso 400 - BAD REQUEST
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}


}
