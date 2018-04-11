package br.paulo.apicurso.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.paulo.apicurso.dto.LancamentoEstatisticaCategoria;
import br.paulo.apicurso.dto.LancamentoEstatisticaDia;
import br.paulo.apicurso.dto.LancamentoEstatisticaPessoa;
import br.paulo.apicurso.mail.Mailer;
import br.paulo.apicurso.model.Lancamento;
import br.paulo.apicurso.model.Pessoa;
import br.paulo.apicurso.model.Usuario;
import br.paulo.apicurso.repository.Lancamentos;
import br.paulo.apicurso.repository.Pessoas;
import br.paulo.apicurso.repository.UsuarioRepository;
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
	
	private static final Logger logger = LoggerFactory.getLogger(LancamentoService.class);
	
	@Autowired
	private Lancamentos lancamentos;

	@Autowired
	private Pessoas pessoas;
	
	@Autowired
	private UsuarioRepository usuarios;
	
	@Autowired
	private Mailer mailer;
	
	
	// @Scheduled(cron = "0 30 10 * * *")
	@Scheduled(fixedDelay = 1000 * 60 * 30)
	public void avisarLancamentosVencidos() {
		if (logger.isDebugEnabled()) {
			logger.debug("Preparando envio de emails com lançamentos vencidos.");
		}
		
		List<Lancamento> vencidos = this.lancamentos.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());	
		
		if (vencidos.isEmpty()) {
			logger.info("Não existem lançamentos vencidos.");
			return;
		}
		
		logger.info("Existe(m} {} lançmento(s) vencido(s).", vencidos.size());
		
		List<Usuario> destinatarios = this.usuarios.findByPermissoesDescricao("ROLE_PESQUISAR_LANCAMENTO");
		
		if (destinatarios.isEmpty()) {
			logger.warn("Existem lançamentos vencidos, mas não foram encontrados destinatários para envio");
			return;
		}
		
		this.mailer.avisarLancamentosVencidos(vencidos, destinatarios);
		
		logger.info("Email com Lançamentos vencidos enviado.");

	}
	
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
