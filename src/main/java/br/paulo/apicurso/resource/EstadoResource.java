package br.paulo.apicurso.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.paulo.apicurso.model.Estado;
import br.paulo.apicurso.service.EstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoResource {

	@Autowired
	private EstadoService estadoService;
	
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<Estado> todosEstados() {
		return this.estadoService.buscarTodos();
	}
}
