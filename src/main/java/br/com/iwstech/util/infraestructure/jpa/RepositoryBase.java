package br.com.iwstech.util.infraestructure.jpa;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.iwstech.util.infraestructure.domain.model.EntidadeBase;

public interface RepositoryBase<E extends EntidadeBase<PK>, PK extends Serializable>
    extends JpaRepository<E, PK> {

    @Query(value="SELECT nextval(?1)", nativeQuery=true)
    public Long nextVal(String sequenceName);

}
