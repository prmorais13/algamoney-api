package br.paulo.apicurso.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.paulo.apicurso.model.Cidade;

public interface CidadeRepository extends JpaRepository<Cidade, Long>{

	List<Cidade> findByEstadoCodigo(Long codigo);
}
