package br.com.matheusramos.minhasfinancas.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.matheusramos.minhasfinancas.enums.EnumStatusLancamento;
import br.com.matheusramos.minhasfinancas.enums.EnumTipoLancamento;
import br.com.matheusramos.minhasfinancas.model.entity.Lancamento;

@ExtendWith(SpringExtension.class) // Extensão dada para uso do JUnit 5 em vez de @RunWith
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	private LancamentoRepository repository;
		
	@Autowired
	private static TestEntityManager em;
	
	@Test
	public void salvarLancamento() {
		
		Lancamento lancamento = criarLancamento();
		
		lancamento = repository.save(lancamento);
		
		assertThat(lancamento.getId()).isNotNull();
	}	

	@Test
	public void deletarLancamento() {
		Lancamento lancamento = criarEPersistirLancamento();
		
		lancamento = em.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		
		Lancamento lancamentoDeletado = em.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoDeletado).isNull();
	}
	
	@Test
	public void atualizarLancamento() {
		Lancamento lancamento = criarEPersistirLancamento();
		
		lancamento.setAno(2019);
		lancamento.setDescricao("teste atualizar");
		lancamento.setStatus(EnumStatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = em.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2019);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("teste atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(EnumStatusLancamento.CANCELADO);
		
	}
	
	@Test
	public void buscarLancamentoPorId() {
		Lancamento lancamento = criarEPersistirLancamento();
		
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	public static Lancamento criarEPersistirLancamento() {
		Lancamento lancamento = criarLancamento();
		em.persist(lancamento);
		return lancamento;
	}

	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				.ano(2020)
				.mes(2)
				.descricao("lancamento 01")
				.valor(BigDecimal.valueOf(10))
				.tipo(EnumTipoLancamento.RECEITA)
				.status(EnumStatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
	}
}
