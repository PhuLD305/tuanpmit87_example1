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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import metro.example1.model.CompanyModel;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Controller
public class Utility {

    public static final String ERROR_LOGIN = "Tên đăng nhập hoặc Mật khẩu không đúng!";
    public static final String ERROR_EXIST_INSURANCE_NUMBER = "Đã tồn tại thông tin thẻ bảo hiểm!";

    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_NEWLINE = "\n";

    private static final String CSV_TITLE = "Danh sách thông tin thẻ bảo hiểm";
    private static final String CSV_COMPANY_NAME = "Tên công ty";
    private static final String CSV_COMPANY_ADDRESS = "Địa chỉ";
    private static final String CSV_COMPANY_EMAIL = "Email";
    private static final String CSV_COMPANY_TELEPHONE = "Số điện thoại";

    private static final String CSV_HEADER_NAME = "Họ và tên";
    private static final String CSV_HEADER_SEX = "Giới tính";
    private static final String CSV_HEADER_BIRTHDAY = "Ngày sinh";
    private static final String CSV_HEADER_INSURANCE_NUMBER = "Mã số thẻ bảo hiểm";
    private static final String CSV_HEADER_START_DATE = "Ngày bắt đầu";
    private static final String CSV_HEADER_END_DATE = "Ngày kết thúc";
    private static final String CSV_HEADER_PLACE = "Nơi đăng ký KCB";


    /**
     * Convert string to md5
     *
     * @param input string need convert
     * @return string md5
     */
    public static String convertToMd5(String input) {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigests = md.digest(input.getBytes());

            // Convert byte array into representation
            BigInteger no = new BigInteger(1, messageDigests);

            // Convert message digest into hex value
            String hashText = no.toString(16);
            StringBuilder text = new StringBuilder();
            while (hashText.length() < 32) {
                hashText = text.append("0").append(hashText).toString();
            }
            return hashText;
        }  catch (NoSuchAlgorithmException e) { // For specifying wrong message digest algorithms
            throw new RuntimeException(e);
        }
    }

    /**
     * Format string
     *
     * <p>convert vietnamese to unsigned</p>
     *
     * <p>Delete special char</p>
     *
     * <p>Replace multi space to space and trim space</p>
     *
     * @param str String need format
     * @return String
     */
    public static String convertVietnameseUnsigned(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^a-zA-Z ]+", "");
        str = str.replaceAll("[ ]+", " ");
        str = str.trim();
        return WordUtils.capitalizeFully(str);
    }

    /**
     * Format date
     *
     * @param date String date need format
     * @param formDateFormat current format date
     * @param toDateFormat format date destination
     * @return String
     */
    public static String formatDate(String date, String formDateFormat, String toDateFormat) {
        try {
            if (date == null || date.equals("")) {
                return null;
            }
            SimpleDateFormat currentFormat = new SimpleDateFormat(formDateFormat);
            SimpleDateFormat destinationFormat = new SimpleDateFormat(toDateFormat);
            return destinationFormat.format(currentFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create paging
     *
     * @param totalRecord total user
     * @param recordPerPage number record per page
     * @param currentPage current page
     * @param numPage number page show on paging bar
     */
    public static Map<String, Object> createPaging(int totalRecord, int recordPerPage, int currentPage, int numPage) {
        Map<String, Object> pagingMap = new HashMap<>();
        int totalPage = (int)Math.ceil((double)totalRecord / recordPerPage);
        int startPage = 1, endPage = (numPage < totalPage) ? numPage : totalPage;
        if (currentPage > numPage - numPage / 2) {
            startPage = currentPage - numPage / 2;
            endPage = (totalPage > currentPage + numPage / 2) ? currentPage + numPage / 2 : totalPage;
        }
        if (endPage == totalPage && totalPage > numPage) {
            startPage = totalPage - numPage + 1;
        }

        pagingMap.put("currentPage", currentPage);
        pagingMap.put("totalPage", totalPage);
        pagingMap.put("startPage", startPage);
        pagingMap.put("endPage", endPage);
        return pagingMap;
    }

    /**
     * Export user list to csv
     *
     * @param userLists List user to export
     * @param companyInfo Company information
     * @param response Http Servlet Response
     */
    public static void writeCsvToResponse(
        List<Map<String, Object>> userLists,
        CompanyModel companyInfo,
        HttpServletResponse response
    ) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=user_list.csv");

        try (
            ServletOutputStream out = response.getOutputStream();
            InputStream in = new ByteArrayInputStream(
                    generateCsvFileBuffer(userLists, companyInfo).toString().getBytes("UTF-8")
            )
        ) {
            byte[] outputByte = new byte[4096];
            // copy binary content to output stream
            while(in.read(outputByte, 0, 4096) != -1) {
                out.write(outputByte, 0, 4096);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * generate Csv file buffer
     *
     * @param userLists List user to export
     * @param companyInfo Company information
     * @return String buffer
     */
    private static StringBuffer generateCsvFileBuffer(List<Map<String, Object>> userLists, CompanyModel companyInfo) {
        StringBuffer writer = new StringBuffer();
        writer.append(CSV_TITLE).append(CSV_NEWLINE);
        writer.append(CSV_NEWLINE);
        writer.append(CSV_COMPANY_NAME).append(CSV_SEPARATOR).append(companyInfo.getCompanyName()).append(CSV_NEWLINE);
        writer.append(CSV_COMPANY_ADDRESS).append(CSV_SEPARATOR).append(companyInfo.getAddress()).append(CSV_NEWLINE);
        writer.append(CSV_COMPANY_EMAIL).append(CSV_SEPARATOR).append(companyInfo.getEmail()).append(CSV_NEWLINE);
        writer.append(CSV_COMPANY_TELEPHONE).append(CSV_SEPARATOR).append(companyInfo.getTelephone()).append(CSV_NEWLINE);
        writer.append(CSV_NEWLINE);

        String[] headers = {CSV_HEADER_NAME, CSV_HEADER_SEX, CSV_HEADER_BIRTHDAY, CSV_HEADER_INSURANCE_NUMBER,
                CSV_HEADER_START_DATE, CSV_HEADER_END_DATE, CSV_HEADER_PLACE};
        writer.append(String.join(CSV_SEPARATOR, headers)).append(CSV_NEWLINE);
        for (Map<String, Object> userMap : userLists) {
            writer.append(userMap.get("user_full_name").toString()).append(CSV_SEPARATOR);
            writer.append(userMap.get("user_sex_division").toString().equals("01") ? "Nam" : "Nữ").append(CSV_SEPARATOR);
            String birthDay = "";
            if (userMap.get("birthdate") != null) {
                birthDay = Utility.formatDate(
                    userMap.get("birthdate").toString(), "yyyy-MM-dd", "dd/MM/yyyy"
                );
            }
            writer.append(birthDay).append(CSV_SEPARATOR);
            writer.append("'").append(userMap.get("insurance_number").toString()).append(CSV_SEPARATOR);
            String insuranceStartDate = Utility.formatDate(
                userMap.get("insurance_start_date").toString(), "yyyy-MM-dd", "dd/MM/yyyy"
            );
            writer.append(insuranceStartDate).append(CSV_SEPARATOR);
            String insuranceEndDate = Utility.formatDate(
                userMap.get("insurance_end_date").toString(), "yyyy-MM-dd", "dd/MM/yyyy"
            );
            writer.append(insuranceEndDate).append(CSV_SEPARATOR);
            writer.append(userMap.get("place_of_register").toString()).append(CSV_NEWLINE);
        }
        return writer;
    }
}