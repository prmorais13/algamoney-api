package br.paulo.apicurso.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.paulo.apicurso.model.Cidade;
import br.paulo.apicurso.repository.CidadeRepository;

@Service
public class CidadeService {
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	public List<Cidade> buscarTodos() {
		return this.cidadeRepository.findAll();
	}
	
	public Cidade porCodigo(Long codigo) {
		return this.cidadeRepository.findOne(codigo);
	}

	public List<Cidade> porCodigoEstado(Long codigoEstado) {
		return this.cidadeRepository.findByEstadoCodigo(codigoEstado);
	}

}
