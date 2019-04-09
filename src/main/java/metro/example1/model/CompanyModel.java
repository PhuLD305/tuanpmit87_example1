package metro.example1.model;

public class CompanyModel {

    private int companyInternalId;
    private String companyName;
    private String address;
    private String email;
    private String telephone;

    public int getCompanyInternalId() {
        return companyInternalId;
    }

    public void setCompanyInternalId(int companyInternalId) {
        this.companyInternalId = companyInternalId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
