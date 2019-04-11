package metro.example1.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import metro.example1.common.Utility;
import metro.example1.model.InsuranceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class InsuranceDao extends JdbcDaoSupport {
    @Autowired
    public InsuranceDao(@Qualifier("dataSource") DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    /**
     * Add a new insurance
     *
     * @param insuranceModel Model insurance
     * @return Last id
     */
    public int addInsurance(InsuranceModel insuranceModel) {
        String sql = "INSERT INTO tbl_insurance VALUES(?, ?, ?, ?, ?)";
        String insuranceNumber = insuranceModel.getInsuranceNumber();
        String startDate = Utility.formatDate(
            insuranceModel.getInsuranceStartDate(), "dd/MM/yyyy", "yyyy-MM-dd"
        );
        String endDate = Utility.formatDate(
            insuranceModel.getInsuranceEndDate(), "dd/MM/yyyy", "yyyy-MM-dd"
        );
        String place = insuranceModel.getPlaceOfRegister();

        Object[] params = new Object[] { null, insuranceNumber, startDate, endDate, place };
        int res = this.getJdbcTemplate().update(sql, params);
        int lastId = 0;
        // not error
        if (res == 1) {
            sql = "SELECT last_insert_id() as id";
            Map<String, Object> objectMap = this.getJdbcTemplate().queryForList(sql).get(0);
            lastId = Integer.parseInt(objectMap.get("id").toString());
        }
        return lastId;
    }

    /**
     * Update insurance information
     *
     * @param insuranceModel Model insurance
     */
    public void updateInsurance(InsuranceModel insuranceModel) {
        String sql = "UPDATE tbl_insurance SET"
                + " insurance_number = ?, insurance_start_date = ?, insurance_end_date = ?, place_of_register =?"
                + " WHERE insurance_internal_id = ?";
        String insuranceNumber = insuranceModel.getInsuranceNumber();
        String startDate = Utility.formatDate(
            insuranceModel.getInsuranceStartDate(), "dd/MM/yyyy", "yyyy-MM-dd"
        );
        String endDate = Utility.formatDate(
            insuranceModel.getInsuranceEndDate(), "dd/MM/yyyy", "yyyy-MM-dd"
        );
        String place = insuranceModel.getPlaceOfRegister();

        Object[] params = new Object[] { insuranceNumber, startDate, endDate, place, insuranceModel.getInsuranceId() };
        this.getJdbcTemplate().update(sql, params);
    }

    /**
     * Check duplicate insurance number
     *
     * @param insuranceNumber insurance_number
     * @return true if exist insurance number, false if not exist
     */
    public boolean checkExistInsuranceNumber(String insuranceNumber) {
        String sql = "SELECT count(*) FROM tbl_insurance" +
                " WHERE insurance_number = ?";
        Object[] params = new Object[] { insuranceNumber };
        List<String> insuranceInfo = this.getJdbcTemplate().queryForList(sql, String.class, params);
        return (Integer.parseInt(insuranceInfo.get(0)) > 0);
    }
}
