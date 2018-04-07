package br.paulo.apicurso.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.paulo.apicurso.dto.LancamentoEstatisticaCategoria;
import br.paulo.apicurso.dto.LancamentoEstatisticaDia;
import br.paulo.apicurso.dto.LancamentoEstatisticaPessoa;
import br.paulo.apicurso.model.Lancamento;
import br.paulo.apicurso.repository.filter.LancamentoFilter;
import br.paulo.apicurso.repository.projection.ResumoLancamento;

public interface LancamentosQuery {
	
	public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate dataInicio, LocalDate dataFinal);
	
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);
	
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
	public Page<ResumoLancamento> resumo (LancamentoFilter filter, Pageable pageable);

}
