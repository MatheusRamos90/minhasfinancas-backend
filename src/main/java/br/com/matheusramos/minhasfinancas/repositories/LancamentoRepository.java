package br.com.matheusramos.minhasfinancas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.matheusramos.minhasfinancas.model.entity.Lancamento;

/**
 * @author Matheus
 */
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
