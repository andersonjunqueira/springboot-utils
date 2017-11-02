package br.com.iwstech.util.infraestructure.dto;

public class Token {

    private String username;
    private String userId;
    private String name;

    public Token(String username, String userId, String name) {
        setUsername(username);
        setUserId(userId);
        setName(name);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
