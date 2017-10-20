package br.com.ertic.util.infraestructure.domain.model;

import java.io.Serializable;

/**
 * Classe abstrata para extensão pelas entidades utilizadas pelo sistema. Possui
 * um parâmetro genérico ID que indica o tipo de dado da coluna chave primária
 * da tabela.
 */
public abstract class EntidadeBase<ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = -4833492543335464195L;
    public static final int MAX_LENGTH_EXCLUIDO = 1;
    
    /**
     * Retorna o <code>Id</code> da entidade
     *
     * @return id da entidade
     */
    public abstract ID getId();
    public abstract void setId(ID id);

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (getId() == null) {
            return false;
        }

        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }

        EntidadeBase<ID> other = (EntidadeBase<ID>) obj;

        return getId().equals(other.getId());

    }

    @Override
    public int hashCode() {
        final int prime = getClass().getName().hashCode();
        int result = prime + (getId() == null ? super.hashCode() : getId().hashCode());
        return result;
    }

}
