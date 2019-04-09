package metro.example1.form;

import javax.validation.constraints.NotBlank;

public class LoginForm {
    @NotBlank(message = "Hãy nhập Tên đăng nhập")
    private String userName;

    @NotBlank(message = "Hãy nhập Mật khẩu")
    private String passWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
