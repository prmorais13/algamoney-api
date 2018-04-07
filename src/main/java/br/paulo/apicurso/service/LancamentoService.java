package br.paulo.apicurso.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.paulo.apicurso.dto.LancamentoEstatisticaCategoria;
import br.paulo.apicurso.dto.LancamentoEstatisticaDia;
import br.paulo.apicurso.dto.LancamentoEstatisticaPessoa;
import br.paulo.apicurso.model.Lancamento;
import br.paulo.apicurso.model.Pessoa;
import br.paulo.apicurso.repository.Lancamentos;
import br.paulo.apicurso.repository.Pessoas;
import br.paulo.apicurso.repository.filter.LancamentoFilter;
import br.paulo.apicurso.repository.projection.ResumoLancamento;
import br.paulo.apicurso.service.exception.PessoaInexistenteOuInativaException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {
	
	@Autowired
	private Lancamentos lancamentos;

	@Autowired
	private Pessoas pessoas;
	
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia) {
		return this.lancamentos.porDia(mesReferencia);
	}
	
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia) {
		return this.lancamentos.porCategoria(mesReferencia);
	}
	
	public Page<Lancamento> pesquisa(LancamentoFilter lancamentoFilter, Pageable pageable) {		
		return this.lancamentos.filtrar(lancamentoFilter, pageable);
	}
	
	public Page<ResumoLancamento> resumir(LancamentoFilter filter, Pageable pageable) {		
		return this.lancamentos.resumo(filter, pageable);
	}
	
	public Lancamento porCodigo(Long codigo) {
		return this.lancamentos.findOne(codigo);
	}
	

	public Lancamento salvar(Lancamento lancamento) {
		this.validarPessoa(lancamento);
		return this.lancamentos.save(lancamento);
	}
	
	public byte[] relatorioPorPessoa(LocalDate dataInicio, LocalDate dataFinal) throws JRException {
		List<LancamentoEstatisticaPessoa> dados = this.lancamentos.porPessoa(dataInicio, dataFinal);
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(dataInicio));
		parametros.put("DT_FINAL", Date.valueOf(dataFinal));
		parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
		
		InputStream stream = this.getClass().getResourceAsStream(
				"/relatorios/lancamentos-por-pessoa.jasper");
		
		JasperPrint relatorio = JasperFillManager.fillReport(stream, parametros,
				new JRBeanCollectionDataSource(dados));
		
		return JasperExportManager.exportReportToPdf(relatorio);
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = this.buscarLancamentoExistente(codigo);
		
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			this.validarPessoa(lancamento);
		}
		
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
		
		return this.lancamentos.save(lancamentoSalvo);
	}
	
	public void excluir(Long codigo) {
		Lancamento lancamentoSalvo = this.buscarLancamentoExistente(codigo);
		this.lancamentos.delete(lancamentoSalvo.getCodigo());
	}
	
	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		if (lancamento.getPessoa().getCodigo() != null) {
			pessoa = this.pessoas.findOne(lancamento.getPessoa().getCodigo());
		}
		
		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}
	
	private Lancamento buscarLancamentoExistente(Long codigo) {
		Lancamento lancamentoSalvo = this.lancamentos.findOne(codigo);
		if (lancamentoSalvo == null) {
			throw new IllegalArgumentException();
		}
		return lancamentoSalvo;
	}
	
}
