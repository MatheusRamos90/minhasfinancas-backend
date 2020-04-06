package br.com.matheusramos.minhasfinancas.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.matheusramos.minhasfinancas.enums.EnumStatusLancamento;
import br.com.matheusramos.minhasfinancas.model.entity.Lancamento;

public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamento);
	
	void atualizarStatus(Lancamento lancamento, EnumStatusLancamento status);
	
	Optional<Lancamento> obterPorId(Long lancamento);
	
	BigDecimal obterSaldoPorUsuario(Long idUsuario);
	
}
