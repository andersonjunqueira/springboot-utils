package br.com.iwstech.util.infraestructure.domain.model;

public enum SimNao {

    S ("S", "Sim"),
    N ("N", "Não");

    private String sigla;
    private String nome;

    private SimNao (String sigla, String nome) {
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
