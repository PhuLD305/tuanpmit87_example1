package metro.example1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import metro.example1.common.Utility;
import metro.example1.dao.CompanyDao;
import metro.example1.dao.InsuranceDao;
import metro.example1.dao.UserDao;
import metro.example1.form.LoginForm;
import metro.example1.form.SearchForm;
import metro.example1.model.CompanyInfo;
import metro.example1.model.InsuranceInfo;
import metro.example1.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private InsuranceDao insuranceDao;

    @RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
    public String signIn(LoginForm loginForm, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") != null) {
            return "redirect:/user-list";
        }
        model.addAttribute("loginForm", loginForm);
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Valid LoginForm loginForm, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            String userName = loginForm.getUserName();
            String passWord = loginForm.getPassWord();
            boolean check = userDao.checkLogin(userName, passWord);
            if(check) {
                HttpSession session = request.getSession();
                session.setAttribute("isLogin", true);
                return "redirect:/user-list";
            }

            ObjectError error = new ObjectError("email","Tên đăng nhập hoặc Mật khẩu không đúng!");
            bindingResult.addError(error);
        }
        model.addAttribute("arrErr", bindingResult.getAllErrors());
        model.addAttribute("loginForm", loginForm);
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String signOut(HttpServletRequest request) {
        HttpSession session=request.getSession();
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(value = "/user-list", method = {RequestMethod.GET, RequestMethod.POST})
    public String getUserLists(
        SearchForm searchForm,
        Model model,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") == null) {
            return "redirect:/login";
        }

        List<Map<String, Object>> companyLists = companyDao.getCompanyLists();
        model.addAttribute("companyLists", companyLists);

        if (request.getMethod().equals("POST")) {
            System.out.println(searchForm.getUserName());
            if (searchForm.getUserName() != null) {
                session.setAttribute("sessionParamUserName", searchForm.getUserName());
            }
            if (searchForm.getInsuranceNumber() != null) {
                session.setAttribute("sessionParamInsuranceNumber", searchForm.getInsuranceNumber());
            }
            if (searchForm.getPlaceOfRegister() != null) {
                session.setAttribute("sessionParamPlaceOfRegister", searchForm.getPlaceOfRegister());
            }
        }

        int companyId;
        if (request.getParameter("companyId") != null) {
            companyId = Integer.parseInt(request.getParameter("companyId").toString());
            session.removeAttribute("sessionParamUserName");
            session.removeAttribute("sessionParamInsuranceNumber");
            session.removeAttribute("sessionParamPlaceOfRegister");
            session.removeAttribute("sessionPage");
        } else if (session.getAttribute("sessionCompanyId") != null) {
            companyId = Integer.parseInt(session.getAttribute("sessionCompanyId").toString());
            session.removeAttribute("sessionCompanyId");
        } else {
            companyId = Integer.parseInt(companyLists.get(0).get("company_internal_id").toString());
        }
        model.addAttribute("companyId", companyId);
        session.setAttribute("sessionCompanyId", companyId);

        if (session.getAttribute("sessionParamUserName") != null) {
            searchForm.setUserName(session.getAttribute("sessionParamUserName").toString());
        }
        if (session.getAttribute("sessionParamInsuranceNumber") != null) {
            searchForm.setInsuranceNumber(session.getAttribute("sessionParamInsuranceNumber").toString());
        }
        if (session.getAttribute("sessionParamPlaceOfRegister") != null) {
            searchForm.setPlaceOfRegister(session.getAttribute("sessionParamPlaceOfRegister").toString());
        }

        String sort = "asc";
        if (request.getParameter("sort") != null) {
            sort = request.getParameter("sort");
        }
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            currentPage = Integer.parseInt(request.getParameter("page").toString());
        } else if (session.getAttribute("sessionPage") != null) {
            currentPage = Integer.parseInt(session.getAttribute("sessionPage").toString());
            session.removeAttribute("sessionPage");
        }
        session.setAttribute("sessionPage", currentPage);

        int limit = 5;
        List<Map<String, Object>> userLists = userDao.getUserLists(searchForm,companyId,sort,currentPage,limit,false);
        model.addAttribute("userLists", userLists);
        model.addAttribute("paramSort", sort);
        model.addAttribute("searchForm", searchForm);

        List<Map<String, Object>> userTotalLists = userDao.getUserLists(searchForm,companyId,sort,currentPage,limit,true);
        int totalRecord = userTotalLists.size();
        Utility.setPaging(model, totalRecord, limit, currentPage, 5);

        if (request.getParameter("act") != null && request.getParameter("act").equals("export")) {
            CompanyInfo companyInfo = companyDao.findCompanyById(companyId);
            Utility.download(userTotalLists, companyInfo, response);
        }
        return "userList";
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String getUserDetail(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") == null) {
            return "redirect:/login";
        }
        int id = Integer.parseInt(request.getParameter("id"));
        List<Map<String, Object>> userInfo = userDao.getUserInfo(id);
        model.addAttribute("userInfo", userInfo.get(0));
        return "detail";
    }

    @RequestMapping(value = "/user", method = {RequestMethod.GET, RequestMethod.POST})
    public String newForm(
        UserInfo userModel,
        CompanyInfo companyModel,
        InsuranceInfo insuranceModel,
        Model model,
        HttpServletRequest request
    ) throws ParseException {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") == null) {
            return "redirect:/login";
        }
        List<Map<String, Object>> companyLists = companyDao.getCompanyLists();
        model.addAttribute("companyLists", companyLists);

        companyModel.setCompanyId(Integer.parseInt(session.getAttribute("sessionCompanyId").toString()));

        if(request.getParameter("id") != null) {
            int userId = Integer.parseInt(request.getParameter("id"));
            Map<String, Object> userInfo = userDao.getUserInfo(userId).get(0);

            companyModel.setCompanyId(Integer.parseInt(userInfo.get("company_internal_id").toString()));

            insuranceModel.setInsuranceId(Integer.parseInt(userInfo.get("insurance_internal_id").toString()));
            insuranceModel.setInsuranceNumber(userInfo.get("insurance_number").toString());
            insuranceModel.setPlaceOfRegister(userInfo.get("place_of_register").toString());
            String startDate = userInfo.get("insurance_start_date").toString();
            insuranceModel.setInsuranceStartDate(Utility.convertDate(startDate, "yyyy-MM-dd", "dd/MM/yyyy"));
            String endDate = userInfo.get("insurance_end_date").toString();
            insuranceModel.setInsuranceEndDate(Utility.convertDate(endDate, "yyyy-MM-dd", "dd/MM/yyyy"));

            userModel.setUserId(userId);
            userModel.setUserFullName(userInfo.get("user_full_name").toString());
            userModel.setUserName(userInfo.get("username").toString());
            userModel.setPassWord(userInfo.get("password").toString());
            userModel.setUserSex(userInfo.get("user_sex_division").toString());
            if (userInfo.get("birthdate") != null) {
                String birthdate = userInfo.get("birthdate").toString();
                userModel.setBirthday(Utility.convertDate(birthdate, "yyyy-MM-dd", "dd/MM/yyyy"));
            }

            model.addAttribute("oldInsuranceNumber", userInfo.get("insurance_number").toString());
        }
        model.addAttribute("userModel", userModel);
        model.addAttribute("companyModel", companyModel);
        model.addAttribute("insuranceModel", insuranceModel);
        return "newForm";
    }

    @RequestMapping(value = "/doAdd", method = RequestMethod.POST)
    public String updateUser(
        @Valid @ModelAttribute("userModel") UserInfo userModel,
        BindingResult bindingResult1,
        CompanyInfo companyModel,
        @Valid @ModelAttribute("insuranceModel")InsuranceInfo insuranceModel,
        BindingResult bindingResult2,
        Model model,
        HttpServletRequest request
    ) throws ParseException {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") == null) {
            return "redirect:/login";
        }
        String oldNumber = request.getParameter("oldInsuranceNumber");
        if (!bindingResult1.hasErrors() && !bindingResult2.hasErrors()) {
            boolean check = insuranceDao.checkExistInsuranceNumber(insuranceModel.getInsuranceNumber());
            if (((oldNumber == null) || ((oldNumber != null) && (!oldNumber.equals(insuranceModel.getInsuranceNumber())))) && check) {
                model.addAttribute("err", "Đã tồn tại thông tin thẻ bảo hiểm!");
            } else {
                String hasExist = request.getParameter("hasExist");
                if (hasExist.equals("0")) {
                    companyModel.setCompanyName(Utility.convertString(companyModel.getCompanyName()));
                    String companyId = companyDao.addCompany(companyModel);
                    userModel.setCompanyId(Integer.parseInt(companyId));
                }
                userModel.setUserFullName(Utility.convertString(userModel.getUserFullName()));
                if(request.getParameter("userId") != null) {
                    insuranceDao.updateInsurance(insuranceModel);
                    userDao.updateUser(userModel);
                } else {
                    String insuranceId = insuranceDao.addInsurance(insuranceModel);
                    userModel.setInsuranceId(Integer.parseInt(insuranceId));
                    userDao.addUser(userModel);
                }
                session.removeAttribute("sessionParamUserName");
                session.removeAttribute("sessionParamInsuranceNumber");
                session.removeAttribute("sessionParamPlaceOfRegister");
                session.removeAttribute("sessionPage");
                return "redirect:/user-list";
            }
        }
        model.addAttribute("oldInsuranceNumber", oldNumber);
        List<Map<String, Object>> companyLists = companyDao.getCompanyLists();
        model.addAttribute("companyLists", companyLists);
        model.addAttribute("userModel", userModel);
        model.addAttribute("companyModel", companyModel);
        model.addAttribute("insuranceModel", insuranceModel);

        return "newForm";
    }

    @RequestMapping(value = "/delete-user", method = RequestMethod.GET)
    public String deleteUser(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") == null) {
            return "redirect:/login";
        }
        int id = Integer.parseInt(request.getParameter("id"));
        userDao.deleteUser(id);

        session.removeAttribute("sessionParamUserName");
        session.removeAttribute("sessionParamInsuranceNumber");
        session.removeAttribute("sessionParamPlaceOfRegister");
        session.removeAttribute("sessionPage");

        return "redirect:/user-list";
    }

    @RequestMapping(value = "/company-info", method = RequestMethod.GET)
    public void getCompanyInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CompanyInfo companyInfo = companyDao.findCompanyById(Integer.parseInt(request.getParameter("id")));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonData = ow.writeValueAsString(companyInfo);
        response.setContentType("application/json");
        response.getWriter().write(jsonData);
    }
}
