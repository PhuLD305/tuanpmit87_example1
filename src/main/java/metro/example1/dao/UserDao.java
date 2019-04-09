package metro.example1.dao;

import metro.example1.common.Utility;
import metro.example1.form.SearchForm;
import metro.example1.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class UserDao extends JdbcDaoSupport {

    @Autowired
    public UserDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public List<Map<String, Object>> getUserLists(
        SearchForm searchForm,
        int companyId,
        String sort,
        int currentPage,
        int limit,
        boolean getTotal
    ) {

        String sql = "SELECT user.user_internal_id, user.user_full_name, user.user_sex_division,user.birthdate, ins.*" +
            " FROM tbl_user user" +
            " LEFT JOIN tbl_company com ON com.company_internal_id = user.company_internal_id" +
            " LEFT JOIN tbl_insurance ins ON ins.insurance_internal_id = user.insurance_internal_id";

        ArrayList<String> aryWhere = new ArrayList<>();
        if(companyId != 0) {
            aryWhere.add("user.company_internal_id = " + companyId);
        }
        if(searchForm.getUserName() != null) {
            aryWhere.add("user.user_full_name LIKE '%" + searchForm.getUserName() + "%'");
        }
        if(searchForm.getInsuranceNumber() != null) {
            aryWhere.add("ins.insurance_number LIKE '%" + searchForm.getInsuranceNumber() + "%'");
        }
        if(searchForm.getPlaceOfRegister() != null) {
            aryWhere.add("ins.place_of_register LIKE '%" + searchForm.getPlaceOfRegister() + "%'");
        }

        String strWhere ="";
        if(aryWhere.size() > 0) {
            strWhere = " WHERE " + String.join(" AND ", aryWhere);
        }

        String strSort = "";
        if (sort != null) {
            strSort = " ORDER BY user.user_full_name " + sort;
        }

        String strLimit = "";
        if (!getTotal) {
            strLimit = " LIMIT " + limit * (currentPage - 1) + ", " + limit;
        }
        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql + strWhere + strSort + strLimit);

        return list;
    }

    public List<Map<String, Object>> getUserInfo(int userId) {
        String sql = "SELECT * FROM tbl_user user" +
                " LEFT JOIN tbl_company com ON com.company_internal_id = user.company_internal_id" +
                " LEFT JOIN tbl_insurance ins ON ins.insurance_internal_id = user.insurance_internal_id" +
                " WHERE user.user_internal_id = ?";

        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, userId);

        return list;
    }

    public boolean checkLogin(String userName, String passWord) {
        String sql = "SELECT count(*) FROM tbl_user user" +
                " WHERE username = ? AND password = ?";
        Object[] params = new Object[] { userName, Utility.getMd5(passWord) };
        try {
            List<String> userInfo = this.getJdbcTemplate().queryForList(sql, String.class, params);
            if(Integer.parseInt(userInfo.get(0)) > 0) {
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
        return false;
    }

    public void addUser(UserInfo userModel) throws ParseException {
        String sql = "INSERT INTO tbl_user VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        int companyId = userModel.getCompanyId();
        int insId = userModel.getInsuranceId();
        String userName = userModel.getUserName();
        String passWord = userModel.getPassWord();
        String fullName = userModel.getUserFullName();
        String sexDivision = userModel.getUserSex();

        String birthDate = Utility.convertDate(userModel.getBirthday(), "dd/MM/yyyy", "yyyy-MM-dd");

        Object[] params = new Object[] { null, companyId, insId, userName,
                Utility.getMd5(passWord), fullName, sexDivision, birthDate };
        this.getJdbcTemplate().update(sql, params);
    }

    public void updateUser(UserInfo userModel) throws ParseException {
        String sql = "UPDATE tbl_user SET"
                + " company_internal_id = ?, username = ?, user_full_name = ?, user_sex_division = ?, birthdate = ?"
                + " WHERE user_internal_id = ?";
        int companyId = userModel.getCompanyId();
        String userName = userModel.getUserName();
        String fullName = userModel.getUserFullName();
        String sexDivision = userModel.getUserSex();

        String birthDate = Utility.convertDate(userModel.getBirthday(), "dd/MM/yyyy", "yyyy-MM-dd");

        Object[] params = new Object[] { companyId, userName, fullName, sexDivision, birthDate, userModel.getUserId() };
        this.getJdbcTemplate().update(sql, params);
    }

    public void deleteUser(int id) {
        String sql = "DELETE user, ins FROM tbl_user as user" +
                " INNER JOIN tbl_insurance as ins ON ins.insurance_internal_id = user.insurance_internal_id" +
                " WHERE user.user_internal_id = ?";
        this.getJdbcTemplate().update(sql, id);
    }
}
