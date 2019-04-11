package metro.example1.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import metro.example1.model.CompanyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class CompanyDao extends JdbcDaoSupport {
    @Autowired
    public CompanyDao(@Qualifier("dataSource") DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    /**
     * Find all {@link CompanyModel}, order by name asc
     *
     * @return List of {@link CompanyModel}
     */
    public List<CompanyModel> getCompanyLists() {
        String sql = "SELECT * FROM tbl_company ORDER BY company_name";
        return this.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(CompanyModel.class));
    }

    /**
     * Add a new company
     *
     * @param companyModel model Company
     * @return Last id
     */
    public int addCompany(CompanyModel companyModel) {
        String sql = "INSERT INTO tbl_company VALUES(?, ?, ?, ?, ?)";
        String companyName = companyModel.getCompanyName();
        String address = companyModel.getAddress();
        String email = companyModel.getEmail();
        String telephone = companyModel.getTelephone();

        Object[] params = new Object[] { null, companyName, address, email, telephone };
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
     * Find {@link CompanyModel} by company_internal_id
     *
     * @param id company_internal_id
     * @return {@link CompanyModel}
     */
    public CompanyModel findCompanyById(int id) {
        String sql = "SELECT * FROM tbl_company WHERE `company_internal_id` = ?";
        Object[] params = new Object[] { id };
        return (CompanyModel)this.getJdbcTemplate().queryForObject(
            sql,
            params,
            new BeanPropertyRowMapper<>(CompanyModel.class)
        );
    }
}
