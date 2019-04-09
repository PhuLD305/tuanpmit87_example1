package metro.example1.dao;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import metro.example1.common.Utility;
import metro.example1.model.InsuranceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class InsuranceDao extends JdbcDaoSupport {
    @Autowired
    public InsuranceDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public String addInsurance(InsuranceInfo insuranceModel) throws ParseException {
        String sql = "INSERT INTO tbl_insurance VALUES(?, ?, ?, ?, ?)";
        String insuranceNumber = insuranceModel.getInsuranceNumber();
        String startDate = Utility.convertDate(insuranceModel.getInsuranceStartDate(), "dd/MM/yyyy", "yyyy-MM-dd");
        String endDate = Utility.convertDate(insuranceModel.getInsuranceEndDate(), "dd/MM/yyyy", "yyyy-MM-dd");
        String place = insuranceModel.getPlaceOfRegister();

        Object[] params = new Object[] { null, insuranceNumber, startDate, endDate, place };
        int res = this.getJdbcTemplate().update(sql, params);
        String lastId = "";
        if (res == 1) {
            List<Map<String, Object>> objectMap = this.getJdbcTemplate().queryForList( "SELECT last_insert_id() as id" );
            lastId = objectMap.get(0).get("id").toString();
        }
        return lastId;
    }

    public void updateInsurance(InsuranceInfo insuranceModel) throws ParseException {
        String sql = "UPDATE tbl_insurance SET"
                + " insurance_number = ?, insurance_start_date = ?, insurance_end_date = ?, place_of_register =?"
                + " WHERE insurance_internal_id = ?";
        String insuranceNumber = insuranceModel.getInsuranceNumber();
        String startDate = Utility.convertDate(insuranceModel.getInsuranceStartDate(), "dd/MM/yyyy", "yyyy-MM-dd");
        String endDate = Utility.convertDate(insuranceModel.getInsuranceEndDate(), "dd/MM/yyyy", "yyyy-MM-dd");
        String place = insuranceModel.getPlaceOfRegister();

        Object[] params = new Object[] { insuranceNumber, startDate, endDate, place, insuranceModel.getInsuranceId() };
        this.getJdbcTemplate().update(sql, params);
    }

    public boolean checkExistInsuranceNumber(String insuranceNumber) {
        String sql = "SELECT count(*) FROM tbl_insurance" +
                " WHERE insurance_number = ?";
        Object[] params = new Object[] { insuranceNumber };
        try {
            List<String> insuranceInfo = this.getJdbcTemplate().queryForList(sql, String.class, params);
            if(Integer.parseInt(insuranceInfo.get(0)) > 0) {
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return false;
    }
}
