package ATJava;

public class PswDialogResponse {
    private String email;
    private String password;

    public PswDialogResponse(String EMail, String Password) {
        email = EMail;
        password = Password;
    }

    public PswDialogResponse(PswDialogResponse firstResponse) {
        email = firstResponse.email;
        password = firstResponse.password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setResponse (String EMail, String Password) {
        email = EMail;
        password = Password;
    }
}
