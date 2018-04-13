package br.paulo.apicurso.security.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import br.paulo.apicurso.ApicursoApplication;
import br.paulo.apicurso.model.Lancamento;
import br.paulo.apicurso.storage.S3;

public class LancamentoAnexoListener {
	
	@PostLoad
	public void postLoad(Lancamento lancamento) {
		if (StringUtils.hasText(lancamento.getAnexo())) {
			S3 s3 = ApicursoApplication.getBean(S3.class);
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
		}
	}

}
