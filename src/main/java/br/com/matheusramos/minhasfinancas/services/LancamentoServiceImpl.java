package br.com.matheusramos.minhasfinancas.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.matheusramos.minhasfinancas.enums.EnumStatusLancamento;
import br.com.matheusramos.minhasfinancas.exceptions.RegraNegocioException;
import br.com.matheusramos.minhasfinancas.interfaces.LancamentoService;
import br.com.matheusramos.minhasfinancas.model.entity.Lancamento;
import br.com.matheusramos.minhasfinancas.repositories.LancamentoRepository;

@Service
public class LancamentoServiceImpl implements LancamentoService {

	private LancamentoRepository repository; 

	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(EnumStatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		/**
		 * Example - é armazenado o conteúdo do tipo transmitido e pode ser feito uma busca na base de dados a partir destes parâmetros
		 * ExampleMatcher - é opcional, e pode ser usado para ignorar formatos (exemplo string uppercase ou lowercase), como pode também encontrar valores exatos, valores iniciais e finais, ou buscar por pedaços
		 * Exato - .withStringMatcher(StringMatcher.EXACT)), Inicio - .withStringMatcher(StringMatcher.STARTING)), Buscar por pedaços - .withStringMatcher(StringMatcher.CONTAINING))
		 * */
		Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example);
	}

	@Override
	@Transactional
	public void atualizarStatus(Lancamento lancamento, EnumStatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	private void validar(Lancamento l) {
		if (l.getDescricao() == null || l.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma 'descrição' válida");
		}
		
		if (l.getMes() == null || l.getMes() < 1 || l.getMes() > 12) {
			throw new RegraNegocioException("Informe um 'mês' válido");
		}
		
		if (l.getAno() == null || l.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um 'ano' válido");
		}
		
		if (l.getUsuario() == null || l.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um 'usuário'");
		}
		
		if (l.getValor() == null || l.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um 'valor' válido");
		}
		
		if (l.getTipo() == null) {
			throw new RegraNegocioException("Informe um 'tipo de lançamento'");
		}
	}
	
}
