package com.devyatochka.hackatonfinal;

/**
 * Created by alexbelogurow on 01.04.17.
 */

public class Profile {
    private String login, password;

    public Profile(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
