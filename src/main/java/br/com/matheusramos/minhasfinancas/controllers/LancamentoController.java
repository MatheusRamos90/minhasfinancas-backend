package br.com.matheusramos.minhasfinancas.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.matheusramos.minhasfinancas.dtos.AtualizaStatusDTO;
import br.com.matheusramos.minhasfinancas.dtos.LancamentoDTO;
import br.com.matheusramos.minhasfinancas.enums.EnumStatusLancamento;
import br.com.matheusramos.minhasfinancas.enums.EnumTipoLancamento;
import br.com.matheusramos.minhasfinancas.exceptions.RegraNegocioException;
import br.com.matheusramos.minhasfinancas.interfaces.LancamentoService;
import br.com.matheusramos.minhasfinancas.interfaces.UsuarioService;
import br.com.matheusramos.minhasfinancas.model.entity.Lancamento;
import br.com.matheusramos.minhasfinancas.model.entity.Usuario;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@GetMapping("{id}")
	public ResponseEntity obterLancamento(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO lancamentoDTO) {
		try {
			Lancamento lancamento = convertDTO(lancamentoDTO);
			lancamento = service.salvar(lancamento);
			
			return new ResponseEntity(lancamento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO) {
		return service.obterPorId(id).map(lancamentoEntidade -> {
			try {
				Lancamento lancamento = convertDTO(lancamentoDTO);
				lancamento.setId(lancamentoEntidade.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch(RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado com o Id informado", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(lancamentoEntidade -> {
			service.deletar(lancamentoEntidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado com o Id informado", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO statusDTO) {
		return service.obterPorId(id).map(lancamento -> {
			EnumStatusLancamento statusSelecionado = EnumStatusLancamento.valueOf(statusDTO.getStatus());
			if (statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie os dados novamente");
			}
			
			try {
				lancamento.setStatus(statusSelecionado);
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado com o Id informado", HttpStatus.BAD_REQUEST));
	}
	
	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao, 
			@RequestParam(value = "mes", required = false) Integer mes, 
			@RequestParam(value = "ano", required = false) Integer ano, 
			@RequestParam("usuario") String idUsuario) {
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuarioOpt = usuarioService.obterPorId(Long.valueOf(idUsuario));
		if (!usuarioOpt.isPresent()) {
			return ResponseEntity.badRequest().body("Houve um erro na consulta do lançamento. O usuário não foi encontrado para o Id informado");
		} else {
			lancamentoFiltro.setUsuario(usuarioOpt.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
		
	}
	
	private LancamentoDTO converter(Lancamento lancamento) {
		return LancamentoDTO.builder()
				.id(lancamento.getId())
				.descricao(lancamento.getDescricao())
				.valor(lancamento.getValor())
				.mes(lancamento.getMes())
				.ano(lancamento.getAno())
				.status(lancamento.getStatus().name())
				.tipo(lancamento.getTipo().name())
				.usuario(lancamento.getUsuario().getId())
				.build();
	}
	
	private Lancamento convertDTO(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
				.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado")); 
		lancamento.setUsuario(usuario);
		
		if (dto.getTipo() != null) {			
			lancamento.setTipo(EnumTipoLancamento.valueOf(dto.getTipo()));
		}
		
		if (dto.getStatus() != null) {			
			lancamento.setStatus(EnumStatusLancamento.valueOf(dto.getStatus()));
		}
		
		return lancamento;
	}
	
}
