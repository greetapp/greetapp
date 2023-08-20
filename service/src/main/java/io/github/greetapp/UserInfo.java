package io.github.greetapp;

import java.util.List;

public class UserInfo {

    private String username;
    private List<String> groups;

    public UserInfo(String username, List<String> groups) {
        this.username = username;
        this.groups = groups;
    }

    public String getUsername() {
        return this.username;
    }

    public List<String> getGroups() {
        return this.groups;
    }

}
