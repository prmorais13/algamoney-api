package br.paulo.apicurso.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.paulo.apicurso.model.Lancamento;
import br.paulo.apicurso.repository.lancamento.LancamentosQuery;


public interface Lancamentos extends JpaRepository<Lancamento, Long>, LancamentosQuery {
	
	public List<Lancamento> findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate data);
}