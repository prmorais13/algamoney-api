package br.paulo.apicurso.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.paulo.apicurso.model.Cidade;
import br.paulo.apicurso.service.CidadeService;

@RestController
@RequestMapping("/cidades")
public class CidadeResource {

	@Autowired
	private CidadeService cidadeService;
	
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<Cidade> pesquisar(@RequestParam Long estado) {
		return this.cidadeService.porCodigoEstado(estado);
	}
}
