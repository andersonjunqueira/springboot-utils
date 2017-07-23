package br.com.ertic.util.infraestructure.dto;

public class Token {

    private String username;

    public Token(String username) {
        setUsername(username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
