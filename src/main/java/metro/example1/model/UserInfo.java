package metro.example1.model;

import javax.validation.constraints.NotBlank;

public class UserInfo {

    private int userId;
    private int companyId;
    private int insuranceId;

    @NotBlank(message = "Hãy nhập [Tên đăng nhập]!")
    private String userName;

    @NotBlank(message = "Hãy nhập [Mật khẩu đăng nhập]!")
    private String passWord;

    @NotBlank(message = "Hãy nhập [Họ và Tên]!")
    private String userFullName;

    private String userSex;
    private String birthday;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(int insuranceId) {
        this.insuranceId = insuranceId;
    }

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

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
