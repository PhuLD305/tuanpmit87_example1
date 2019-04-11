package metro.example1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import metro.example1.common.Utility;
import metro.example1.dao.CompanyDao;
import metro.example1.dao.InsuranceDao;
import metro.example1.dao.UserDao;
import metro.example1.form.LoginForm;
import metro.example1.form.SearchForm;
import metro.example1.model.CompanyModel;
import metro.example1.model.InsuranceModel;
import metro.example1.model.UserModel;
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
        if (bindingResult.hasErrors() == false) {
            String userName = loginForm.getUserName();
            String passWord = loginForm.getPassWord();
            boolean check = userDao.checkLogin(userName, passWord);
            if(check) {
                HttpSession session = request.getSession();
                session.setAttribute("isLogin", true);
                return "redirect:/user-list";
            }

            ObjectError error = new ObjectError("email", Utility.ERROR_LOGIN);
            bindingResult.addError(error);
        }
        model.addAttribute("arrErr", bindingResult.getAllErrors());
        model.addAttribute("loginForm", loginForm);
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String signOut(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(value = "/user-list", method = {RequestMethod.GET, RequestMethod.POST})
    public String getUserLists(SearchForm searchForm, Model model, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") == null) {
            return "redirect:/login";
        }

        List<CompanyModel> companyLists = companyDao.getCompanyLists();
        model.addAttribute("companyLists", companyLists);

        if (request.getMethod().equals("POST")) {
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

        int companyId = companyLists.get(0).getCompanyInternalId();
        if (request.getParameter("companyId") != null) {
            companyId = Integer.parseInt(request.getParameter("companyId"));
            session.removeAttribute("sessionParamUserName");
            session.removeAttribute("sessionParamInsuranceNumber");
            session.removeAttribute("sessionParamPlaceOfRegister");
            session.removeAttribute("sessionPage");
        } else if (session.getAttribute("sessionCompanyId") != null) {
            companyId = Integer.parseInt(session.getAttribute("sessionCompanyId").toString());
            session.removeAttribute("sessionCompanyId");
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
            currentPage = Integer.parseInt(request.getParameter("page"));
        } else if (session.getAttribute("sessionPage") != null) {
            currentPage = Integer.parseInt(session.getAttribute("sessionPage").toString());
            session.removeAttribute("sessionPage");
        }
        session.setAttribute("sessionPage", currentPage);

        int limit = 5;
        List<Map<String, Object>> userLists = userDao.getUserLists(
            searchForm,
            companyId,
            sort,
            currentPage,
            limit,
            false
        );
        model.addAttribute("userLists", userLists);
        model.addAttribute("paramSort", sort);
        model.addAttribute("searchForm", searchForm);

        List<Map<String, Object>> userTotalLists = userDao.getUserLists(
            searchForm,
            companyId,
            sort,
            currentPage,
            limit,
            true
        );
        int totalRecord = userTotalLists.size();
        Map<String, Object> pagingMap = Utility.createPaging(totalRecord, limit, currentPage, 5);
        for (String key : pagingMap.keySet()) {
            model.addAttribute(key, pagingMap.get(key));
        }

        if (request.getParameter("act") != null && request.getParameter("act").equals("export")) {
            CompanyModel companyInfo = companyDao.findCompanyById(companyId);
            Utility.writeCsvToResponse(userTotalLists, companyInfo, response);
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
        Map<String, Object> userMap = userDao.findDetailInfoById(id);
        model.addAttribute("detailInfo", userMap);
        return "detail";
    }

    @RequestMapping(value = "/user", method = {RequestMethod.GET, RequestMethod.POST})
    public String newForm(
        UserModel userModel,
        CompanyModel companyModel,
        InsuranceModel insuranceModel,
        Model model,
        HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") == null) {
            return "redirect:/login";
        }
        List<CompanyModel> companyLists = companyDao.getCompanyLists();
        model.addAttribute("companyLists", companyLists);

        companyModel.setCompanyInternalId(Integer.parseInt(session.getAttribute("sessionCompanyId").toString()));

        if(request.getParameter("id") != null) {
            int userId = Integer.parseInt(request.getParameter("id"));
            Map<String, Object> userInfo = userDao.findDetailInfoById(userId);

            companyModel.setCompanyInternalId(Integer.parseInt(userInfo.get("company_internal_id").toString()));

            insuranceModel.setInsuranceId(Integer.parseInt(userInfo.get("insurance_internal_id").toString()));
            insuranceModel.setInsuranceNumber(userInfo.get("insurance_number").toString());
            insuranceModel.setPlaceOfRegister(userInfo.get("place_of_register").toString());
            String startDate = userInfo.get("insurance_start_date").toString();
            insuranceModel.setInsuranceStartDate(
                Utility.formatDate(startDate, "yyyy-MM-dd", "dd/MM/yyyy")
            );
            String endDate = userInfo.get("insurance_end_date").toString();
            insuranceModel.setInsuranceEndDate(
                Utility.formatDate(endDate, "yyyy-MM-dd", "dd/MM/yyyy")
            );

            userModel.setUserId(userId);
            userModel.setUserFullName(userInfo.get("user_full_name").toString());
            userModel.setUserName(userInfo.get("username").toString());
            userModel.setPassWord(userInfo.get("password").toString());
            userModel.setUserSex(userInfo.get("user_sex_division").toString());
            if (userInfo.get("birthdate") != null) {
                String birthDate = userInfo.get("birthdate").toString();
                userModel.setBirthday(Utility.formatDate(birthDate, "yyyy-MM-dd", "dd/MM/yyyy"));
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
        @Valid @ModelAttribute("userModel") UserModel userModel,
        BindingResult bindingResult1,
        CompanyModel companyModel,
        @Valid @ModelAttribute("insuranceModel")InsuranceModel insuranceModel,
        BindingResult bindingResult2,
        Model model,
        HttpServletRequest request
    ) {
        HttpSession session = request.getSession();
        if(session.getAttribute("isLogin") == null) {
            return "redirect:/login";
        }
        String oldNumber = request.getParameter("oldInsuranceNumber");
        if (bindingResult1.hasErrors() == false && bindingResult2.hasErrors() == false) {
            boolean check = insuranceDao.checkExistInsuranceNumber(insuranceModel.getInsuranceNumber());
            if (((oldNumber == null) || (oldNumber.equals(insuranceModel.getInsuranceNumber()) == false)) && check) {
                model.addAttribute("err", Utility.ERROR_EXIST_INSURANCE_NUMBER);
            } else {
                String hasExist = request.getParameter("hasExist");
                if (hasExist.equals("0")) {
                    companyModel.setCompanyName(Utility.convertVietnameseUnsigned(companyModel.getCompanyName()));
                    int companyId = companyDao.addCompany(companyModel);
                    userModel.setCompanyInternalId(companyId);
                }
                userModel.setUserFullName(Utility.convertVietnameseUnsigned(userModel.getUserFullName()));
                if(request.getParameter("userId") != null) {
                    insuranceDao.updateInsurance(insuranceModel);
                    userDao.updateUser(userModel);
                } else {
                    int insuranceId = insuranceDao.addInsurance(insuranceModel);
                    userModel.setInsuranceId(insuranceId);
                    userDao.addUser(userModel);
                }
                session.removeAttribute("sessionParamUserName");
                session.removeAttribute("sessionParamInsuranceNumber");
                session.removeAttribute("sessionParamPlaceOfRegister");
                session.removeAttribute("sessionPage");
                return "redirect:/user-list";
            }
        }
        if ((oldNumber != null)) {
            model.addAttribute("oldInsuranceNumber", oldNumber);
        }
        List<CompanyModel> companyLists = companyDao.getCompanyLists();
        model.addAttribute("companyLists", companyLists);
        model.addAttribute("userModel", userModel);
        model.addAttribute("companyModel", companyModel);
        model.addAttribute("insuranceModel", insuranceModel);

        return "newForm";
    }

    @RequestMapping(value = "/delete-user", method = RequestMethod.GET)
    public String deleteUser(HttpServletRequest request) {
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
        CompanyModel companyInfo = companyDao.findCompanyById(Integer.parseInt(request.getParameter("id")));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonData = ow.writeValueAsString(companyInfo);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonData);
    }
}
