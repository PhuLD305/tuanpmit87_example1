package metro.example1.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class InsuranceModel {
    private int insuranceId;

    @Size(min=10, max=10, message = "[Mã số thẻ bảo hiểm] gồm 10 chữ số")
    private String insuranceNumber;

    @NotBlank(message = "Hãy nhập [Ngày bắt đầu thẻ BH]!")
    private String insuranceStartDate;

    @NotBlank(message = "Hãy nhập [Ngày kết thúc thẻ BH]!")
    private String insuranceEndDate;

    @NotBlank(message = "Hãy nhập [Nơi đăng ký KCB]!")
    private String placeOfRegister;

    public int getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(int insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getInsuranceStartDate() {
        return insuranceStartDate;
    }

    public void setInsuranceStartDate(String insuranceStartDate) {
        this.insuranceStartDate = insuranceStartDate;
    }

    public String getInsuranceEndDate() {
        return insuranceEndDate;
    }

    public void setInsuranceEndDate(String insuranceEndDate) {
        this.insuranceEndDate = insuranceEndDate;
    }

    public String getPlaceOfRegister() {
        return placeOfRegister;
    }

    public void setPlaceOfRegister(String placeOfRegister) {
        this.placeOfRegister = placeOfRegister;
    }
}

