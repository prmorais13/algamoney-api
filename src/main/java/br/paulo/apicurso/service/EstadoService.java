package br.paulo.apicurso.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.paulo.apicurso.model.Estado;
import br.paulo.apicurso.repository.EstadoRepository;

@Service
public class EstadoService {
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	public List<Estado> buscarTodos() {
		return this.estadoRepository.findAll();
	}
	
	public Estado porCodigo(Long codigo) {
		return this.estadoRepository.findOne(codigo);
	}

}
