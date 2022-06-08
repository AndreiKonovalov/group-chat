package main.dto;

public class DtoUser {

    private String username;

    public DtoUser() {
    }

    public DtoUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
