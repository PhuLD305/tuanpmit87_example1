package metro.example1.dao;

import metro.example1.common.Utility;
import metro.example1.form.SearchForm;
import metro.example1.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class UserDao extends JdbcDaoSupport {

    @Autowired
    public UserDao(@Qualifier("dataSource") DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    /**
     * Find all user by condition
     *
     * @param searchForm Form search
     * @param companyId company_internal_id
     * @param sort asc or desc
     * @param currentPage current page
     * @param limit number record per page
     * @param getTotal true if get total record, false if get limit record
     * @return List user information
     */
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

        ArrayList<String> wheres = new ArrayList<>();
        if(companyId != 0) {
            wheres.add("user.company_internal_id = " + companyId);
        }
        if(searchForm.getUserName() != null) {
            wheres.add("user.user_full_name LIKE '%" + searchForm.getUserName() + "%'");
        }
        if(searchForm.getInsuranceNumber() != null) {
            wheres.add("ins.insurance_number LIKE '%" + searchForm.getInsuranceNumber() + "%'");
        }
        if(searchForm.getPlaceOfRegister() != null) {
            wheres.add("ins.place_of_register LIKE '%" + searchForm.getPlaceOfRegister() + "%'");
        }

        String sqlWhere ="";
        if(wheres.size() > 0) {
            sqlWhere = " WHERE " + String.join(" AND ", wheres);
        }

        String sqlSort = "";
        if (sort != null) {
            sqlSort = " ORDER BY user.user_full_name " + sort;
        }

        String sqlLimit = "";
        if (getTotal == false) {
            sqlLimit = " LIMIT " + limit * (currentPage - 1) + ", " + limit;
        }

        return this.getJdbcTemplate().queryForList(sql + sqlWhere + sqlSort + sqlLimit);
    }

    /**
     * Find detail information by user id
     *
     * @param userId user_internal_id
     * @return Map
     */
    public Map<String, Object> findDetailInfoById(int userId) {
        String sql = "SELECT * FROM tbl_user user" +
                " LEFT JOIN tbl_company com ON com.company_internal_id = user.company_internal_id" +
                " LEFT JOIN tbl_insurance ins ON ins.insurance_internal_id = user.insurance_internal_id" +
                " WHERE user.user_internal_id = ?";

        return this.getJdbcTemplate().queryForMap(sql, userId);
    }

    /**
     * Check login information
     *
     * @param userName username
     * @param passWord password
     * @return If matching with userName, passWord then return true, else return false
     */
    public boolean checkLogin(String userName, String passWord) {
        String sql = "SELECT count(*) FROM tbl_user user" +
                " WHERE username = ? AND password = ?";

        Object[] params = new Object[] { userName, Utility.convertToMd5(passWord) };
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

    /**
     * Add a new user
     *
     * @param userModel Model user
     */
    public void addUser(UserModel userModel) {
        String sql = "INSERT INTO tbl_user VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        int companyId = userModel.getCompanyInternalId();
        int insId = userModel.getInsuranceId();
        String userName = userModel.getUserName();
        String passWord = Utility.convertToMd5(userModel.getPassWord());
        String fullName = userModel.getUserFullName();
        String sexDivision = userModel.getUserSex();
        String birthDate = Utility.formatDate(userModel.getBirthday(), "dd/MM/yyyy", "yyyy-MM-dd");

        Object[] params = new Object[] { null, companyId, insId, userName, passWord, fullName, sexDivision, birthDate };
        this.getJdbcTemplate().update(sql, params);
    }

    /**
     * Update user information
     *
     * @param userModel Model user
     */
    public void updateUser(UserModel userModel) {
        String sql = "UPDATE tbl_user SET"
                + " company_internal_id = ?, username = ?, user_full_name = ?, user_sex_division = ?, birthdate = ?"
                + " WHERE user_internal_id = ?";

        int companyId = userModel.getCompanyInternalId();
        String userName = userModel.getUserName();
        String fullName = userModel.getUserFullName();
        String sexDivision = userModel.getUserSex();
        String birthDate = Utility.formatDate(userModel.getBirthday(), "dd/MM/yyyy", "yyyy-MM-dd");

        Object[] params = new Object[] { companyId, userName, fullName, sexDivision, birthDate, userModel.getUserId() };
        this.getJdbcTemplate().update(sql, params);
    }

    /**
     * Delete user, insurance by user_internal_id
     *
     * @param id user_internal_id
     */
    public void deleteUser(int id) {
        String sql = "DELETE user, ins FROM tbl_user as user" +
                " INNER JOIN tbl_insurance as ins ON ins.insurance_internal_id = user.insurance_internal_id" +
                " WHERE user.user_internal_id = ?";

        this.getJdbcTemplate().update(sql, id);
    }
}
