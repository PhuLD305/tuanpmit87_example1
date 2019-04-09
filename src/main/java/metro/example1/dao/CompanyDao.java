package metro.example1.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import metro.example1.model.CompanyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class CompanyDao extends JdbcDaoSupport {
    @Autowired
    public CompanyDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public List<Map<String, Object>> getCompanyLists() {
        String sql = "SELECT c.company_internal_id, c.company_name FROM tbl_company c ORDER BY c.company_name";
        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql);
        return list;
    }

    public String addCompany(CompanyInfo companyModel) {
        String sql = "INSERT INTO tbl_company VALUES(?, ?, ?, ?, ?)";
        String companyName = companyModel.getCompanyName();
        String address = companyModel.getAddress();
        String email = companyModel.getEmail();
        String telephone = companyModel.getTelephone();

        Object[] params = new Object[] { null, companyName, address, email, telephone };
        int res = this.getJdbcTemplate().update(sql, params);
        String lastId = "";
        if (res == 1) {
            List<Map<String, Object>> objectMap = this.getJdbcTemplate().queryForList( "SELECT last_insert_id() as id" );
            lastId = objectMap.get(0).get("id").toString();
        }
        return lastId;
    }

    public CompanyInfo findCompanyById(int id) {
        CompanyInfo companyInfo = null;
        String sql = "SELECT * FROM tbl_company WHERE company_internal_id = ?";
        Object[] params = new Object[] { id };
        companyInfo = this.getJdbcTemplate().queryForObject(sql, params, new BeanPropertyRowMapper<>(CompanyInfo.class));
        return companyInfo;
    }
}
