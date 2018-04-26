package br.paulo.apicurso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.paulo.apicurso.model.Estado;

public interface EstadoRepository extends JpaRepository<Estado, Long>{

}
