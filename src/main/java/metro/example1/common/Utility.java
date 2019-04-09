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

import metro.example1.model.CompanyModel;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Controller
public class Utility {

    /**
     * Convert string to md5
     *
     * @param input string need convert
     * @return string md5
     */
    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigests = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigests);

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

    /**
     * Format string
     *
     * <p>Convert tieng viet thanh khong dau</p>
     *
     * <p>Delete special char</p>
     *
     * <p>Replace multi space to space and trim space</p>
     *
     * @param str String need format
     * @return String
     */
    public static String formatString(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^a-zA-Z ]+","");
        str = str.replaceAll("[ ]+"," ");
        str = str.trim();
        return WordUtils.capitalizeFully(str);
    }

    /**
     * Format date
     *
     * @param date String date need format
     * @param srcFormat source format
     * @param destFormat destination format
     * @return String
     * @throws ParseException
     */
    public static String formatDate(String date, String srcFormat, String destFormat) throws ParseException {
        SimpleDateFormat formatSrc = new SimpleDateFormat(srcFormat);
        SimpleDateFormat formatDest = new SimpleDateFormat(destFormat);
        return (date.equals("") == false) ? formatDest.format(formatSrc.parse(date)) : null;
    }

    /**
     * Create paging
     *
     * @param model
     * @param totalRecord
     * @param perPage
     * @param currentPage
     * @param numPage
     */
    public static void setPaging(Model model, int totalRecord, int perPage, int currentPage, int numPage) {
        int totalPage = (int)Math.ceil((double)totalRecord / perPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);
        int startPage = 1, endPage = (numPage < totalPage) ? numPage : totalPage;
        if (currentPage > numPage - numPage/2) {
            startPage = currentPage - numPage/2;
            endPage = (totalPage > currentPage + numPage/2) ? currentPage + numPage/2 : totalPage;
        }
        if (endPage == totalPage && totalPage > numPage) {
            startPage = totalPage - numPage + 1;
        }

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
    }

    /**
     * Export user list to csv
     *
     * @param userLists
     * @param companyInfo
     * @param response
     * @throws Exception
     */
    public static void download(List<Map<String, Object>> userLists, CompanyModel companyInfo, HttpServletResponse response) throws Exception {
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

    private static StringBuffer generateCsvFileBuffer(List<Map<String, Object>> userLists, CompanyModel companyInfo) throws ParseException {
        StringBuffer writer = new StringBuffer();
        writer.append("Danh sách thông tin thẻ bảo hiểm").append('\n');
        writer.append('\n');
        writer.append("Tên công ty").append(",").append(companyInfo.getCompanyName()).append('\n');
        writer.append("Địa chỉ").append(",").append(companyInfo.getAddress()).append('\n');
        writer.append("Email").append(",").append(companyInfo.getEmail()).append('\n');
        writer.append("Số điện thoại").append(",").append(companyInfo.getTelephone()).append('\n');
        writer.append('\n');

        String[] headers = { "Họ và tên","Giới tính","Ngày sinh","Mã số thẻ bảo hiểm","Ngày bắt đầu","Ngày kết thúc","Nơi đăng ký KCB" };
        writer.append(String.join(",", headers)).append('\n');
        for (Map<String, Object> userMap : userLists) {
            writer.append(userMap.get("user_full_name").toString()).append(",");
            writer.append(userMap.get("user_sex_division").toString().equals("01") ? "Nam" : "Nữ").append(",");
            String birthDay = "";
            if (userMap.get("birthdate") != null) {
                birthDay = Utility.formatDate(userMap.get("birthdate").toString(), "yyyy-MM-dd", "dd/MM/yyyy");
            }
            writer.append(birthDay).append(",");
            writer.append("'"+userMap.get("insurance_number").toString()).append(",");
            writer.append(Utility.formatDate(userMap.get("insurance_start_date").toString(), "yyyy-MM-dd", "dd/MM/yyyy")).append(",");
            writer.append(Utility.formatDate(userMap.get("insurance_end_date").toString(), "yyyy-MM-dd", "dd/MM/yyyy")).append(",");
            writer.append(userMap.get("place_of_register").toString()).append('\n');
        }
        return writer;
    }
}