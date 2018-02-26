package br.com.iwstech.util.infraestructure.domain.model;

public enum SituacaoAtivo {

    A ("A", "Ativo"),
    I ("I", "Inativo");

    private String sigla;
    private String nome;

    private SituacaoAtivo (String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }

}
