package br.com.tonypool.auth.requests;

public class PasswordForgotRequest {
    private String email;

    public PasswordForgotRequest() {}

    public PasswordForgotRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
