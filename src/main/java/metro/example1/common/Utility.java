package metro.example1.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import metro.example1.model.CompanyInfo;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Controller
public class Utility {

    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertString(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^a-zA-Z ]+","");
        str = str.replaceAll("[ ]+"," ");
        str = str.trim();
        return WordUtils.capitalizeFully(str);
    }

    public static String convertDate(String date, String srcFormat, String destFormat) throws ParseException {
        SimpleDateFormat formatSrc = new SimpleDateFormat(srcFormat);
        SimpleDateFormat formatDest = new SimpleDateFormat(destFormat);
        return !date.equals("") ? formatDest.format(formatSrc.parse(date)) : null;
    }

    public static void setPaging(Model model, int totalRecord, int perPage, int currentPage, int numPage) {
        int totalPage = (int)Math.ceil((double)totalRecord / perPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        int startPage = 1, endPage = (numPage < totalPage) ? numPage : totalPage;
        if (currentPage > numPage - numPage/2) {
            startPage = currentPage - numPage/2;
            endPage = (totalPage > currentPage + numPage/2) ? currentPage + numPage/2 : totalPage;
        }
        if (endPage == totalPage && totalPage > numPage) startPage = totalPage - numPage + 1;

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
    }

    public static void download(List<Map<String, Object>> userLists, CompanyInfo companyInfo, HttpServletResponse response) throws Exception {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition","attachment;filename=user_list.csv");

        try {
            ServletOutputStream out = response.getOutputStream();
            StringBuffer sb = generateCsvFileBuffer(userLists, companyInfo);

            InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

            byte[] outputByte = new byte[4096];
            //copy binary contect to output stream
            while(in.read(outputByte, 0, 4096) != -1) {
                out.write(outputByte, 0, 4096);
            }
            in.close();
            out.flush();
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static StringBuffer generateCsvFileBuffer(List<Map<String, Object>> userLists, CompanyInfo companyInfo) throws ParseException {
        StringBuffer writer = new StringBuffer();
        writer.append("Danh sách thông tin thẻ bảo hiểm").append('\n');
        writer.append('\n');
        writer.append("Tên công ty").append(",").append(companyInfo.getCompanyName()).append('\n');
        writer.append("Địa chỉ").append(",").append(companyInfo.getAddress()).append('\n');
        writer.append("Email").append(",").append(companyInfo.getEmail()).append('\n');
        writer.append("Số điện thoại").append(",").append(companyInfo.getTelephone()).append('\n');
        writer.append('\n');

        String[] header = { "Họ và tên","Giới tính","Ngày sinh","Mã số thẻ bảo hiểm","Ngày bắt đầu","Ngày kết thúc","Nơi đăng ký KCB" };
        writer.append(String.join(",", header)).append('\n');
        for (Map<String, Object> user : userLists) {
            writer.append(user.get("user_full_name").toString()).append(",");
            writer.append(user.get("user_sex_division").toString().equals("01") ? "Nam" : "Nữ").append(",");
            String birthDay = "";
            if (user.get("birthdate") != null) {
                birthDay = Utility.convertDate(user.get("birthdate").toString(), "yyyy-MM-dd", "dd/MM/yyyy");
            }
            writer.append(birthDay).append(",");
            writer.append("'"+user.get("insurance_number").toString()).append(",");
            writer.append(Utility.convertDate(user.get("insurance_start_date").toString(), "yyyy-MM-dd", "dd/MM/yyyy")).append(",");
            writer.append(Utility.convertDate(user.get("insurance_end_date").toString(), "yyyy-MM-dd", "dd/MM/yyyy")).append(",");
            writer.append(user.get("place_of_register").toString()).append('\n');
        }
        return writer;
    }
}