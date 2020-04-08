package br.com.matheusramos.minhasfinancas.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.matheusramos.minhasfinancas.enums.EnumStatusLancamento;
import br.com.matheusramos.minhasfinancas.exceptions.RegraNegocioException;
import br.com.matheusramos.minhasfinancas.model.entity.Lancamento;
import br.com.matheusramos.minhasfinancas.model.entity.Usuario;
import br.com.matheusramos.minhasfinancas.repositories.LancamentoRepository;
import br.com.matheusramos.minhasfinancas.repositories.LancamentoRepositoryTest;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;

	@Test
	public void salvarLancamento() {
		// cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(EnumStatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		// execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);

		// verificação
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(EnumStatusLancamento.PENDENTE);
	}

	@Test
	public void naoSalvarLancamentoException() {
		// cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);

		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void atualizarLancamento() {
		// cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(EnumStatusLancamento.PENDENTE);

		Mockito.doNothing().when(service).validar(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// execução
		service.atualizar(lancamentoSalvo);

		// verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}

	@Test
	public void atualizarLancamentoException() {
		// cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		// execução e verificação
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);

	}

	@Test
	public void deletarLancamento() {
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		// execução
		service.deletar(lancamento);

		// verificação
		Mockito.verify(repository).delete(lancamento);
	}

	@Test
	public void deletarLancamentoException() {
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		// execução
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

		// verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}

	@Test
	public void filtrarLancamentosException() {
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		// execução
		List<Lancamento> resultado = service.buscar(lancamento);

		// verificações
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);

	}

	@Test
	public void atualizarStatusLancamento() {

		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(EnumStatusLancamento.PENDENTE);

		EnumStatusLancamento novoStatus = EnumStatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		// execução
		service.atualizarStatus(lancamento, novoStatus);

		// verificações
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);

	}

	@Test
	public void obterLancamentoById() {
		// cenário
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		// verificação
		Assertions.assertThat(resultado.isPresent()).isTrue();

	}

	@Test
	public void obterLancamentoByIdNaoExistente() {
		// cenário
		Long id = 1L;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		// verificação
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}

	@Test
	public void validaLancamento() {
		Lancamento lancamento = new Lancamento();

		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma 'descrição' válida");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma 'descrição' válida");
		
		lancamento.setDescricao("Salario");
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'mês' válido");
		
		lancamento.setMes(15);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'mês' válido");
		
		lancamento.setMes(null);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'mês' válido");
		
		lancamento.setMes(2);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'ano' válido");
		
		lancamento.setAno(202);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'ano' válido");
		
		lancamento.setAno(null);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'ano' válido");
		
		lancamento.setAno(2020);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'usuário'");
		
		lancamento.setUsuario(new Usuario());
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'usuário'");
		
		lancamento.getUsuario().setId(1L);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'valor' válido");
		
		lancamento.setValor(null);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'valor' válido");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'valor' válido");
		
		lancamento.setValor(BigDecimal.valueOf(20));
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um 'tipo de lançamento'");
	}

}
