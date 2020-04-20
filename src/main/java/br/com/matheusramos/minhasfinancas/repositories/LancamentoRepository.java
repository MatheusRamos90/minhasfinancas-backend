package br.com.matheusramos.minhasfinancas.repositories;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.matheusramos.minhasfinancas.enums.EnumStatusLancamento;
import br.com.matheusramos.minhasfinancas.enums.EnumTipoLancamento;
import br.com.matheusramos.minhasfinancas.model.entity.Lancamento;

/**
 * @author Matheus
 */
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
	@Query(value = "select sum(l.valor) from Lancamento l join l.usuario u where u.id = :idUsuario and l.tipo = :tipoLancamento and l.status = :status group by u")
	BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(
			@Param("idUsuario") Long idUsuario, 
			@Param("tipoLancamento") EnumTipoLancamento tipoLancamento,
			@Param("status") EnumStatusLancamento satus);
	
}
