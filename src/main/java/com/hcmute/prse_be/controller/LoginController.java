package com.hcmute.prse_be.controller;

import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.HttpService;
import com.hcmute.prse_be.util.ConvertUtils;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginController {

    private static final String INFO_IP_URL = Config.getParam("info_ip", "base_url");

    private final HttpService httpService;

    @Autowired
    public LoginController(HttpService httpService) {
        this.httpService = httpService;
    }

//    @GetMapping("/testLogin")
//    public JSONObject loginTest(HttpServletRequest request) {
//        JSONObject loginInfo = new JSONObject();
//        String ip = request.getRemoteAddr();
//        String user_agent = request.getHeader("User-Agent");
//        String url = INFO_IP_URL + "/" + ip;
//        JSONObject response_from_info_ip = httpService.sendGet(url).block();
//        if(response_from_info_ip.get("status") != null
//                && ConvertUtils.toString(response_from_info_ip.get("status")).equals("success"))
//        {
//            loginInfo.put("country", ConvertUtils.toString(response_from_info_ip.get("country")));
//            loginInfo.put("city", ConvertUtils.toString(response_from_info_ip.get("city")));
//            loginInfo.put("lat", ConvertUtils.toString(response_from_info_ip.get("lat")));
//            loginInfo.put("lng", ConvertUtils.toString(response_from_info_ip.get("lon")));
//        }
//        loginInfo.put("remote addr", ip);
//        loginInfo.put("user-agent", user_agent);
//        return Response.success(loginInfo);
//    }

    // Return certificate page
    @GetMapping("/testcontroller")
    @ResponseBody
    public String index() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"vi\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Chứng Chỉ Của Bạn</title>\n" +
                "    <meta name=\"description\" content=\"Trang chứng chỉ video của bạn - hãy xem và lưu giữ thành quả học tập của mình!\">\n" +
                "    <meta name=\"author\" content=\"Hệ Thống Đào Tạo XYZ\">\n" +
                "    <meta name=\"robots\" content=\"index, follow\">\n" +
                "\n" +
                "    <!-- Open Graph cho Facebook / Zalo -->\n" +
                "    <meta property=\"og:title\" content=\"Chứng Chỉ Của Bạn - Hoàn Thành Khóa Học!\">\n" +
                "    <meta property=\"og:description\" content=\"Chúc mừng bạn đã hoàn thành khóa học. Xem video chứng chỉ ngay bây giờ!\">\n" +
                "    <meta property=\"og:image\" content=\"https://png.pngtree.com/element_pic/16/11/11/83b2bc072fb15b3ddd955caeaa0e31c9.jpg\">\n" +
                "    <meta property=\"og:image:width\" content=\"800\">\n" +
                "    <meta property=\"og:image:height\" content=\"600\">\n" +
                "    <meta property=\"og:url\" content=\"https://prse-be.ddns.net:8443/testcontroller\">\n" +
                "    <meta property=\"og:type\" content=\"website\">\n" +
                "    <meta property=\"og:site_name\" content=\"Hệ Thống Đào Tạo XYZ\">\n" +
                "\n" +
                "    <!-- Twitter Card -->\n" +
                "    <meta name=\"twitter:card\" content=\"summary_large_image\">\n" +
                "    <meta name=\"twitter:title\" content=\"Chứng Chỉ Của Bạn - Khóa học hoàn tất!\">\n" +
                "    <meta name=\"twitter:description\" content=\"Chứng nhận thành tích học tập của bạn. Click để xem video!\">\n" +
                "    <meta name=\"twitter:image\" content=\"https://png.pngtree.com/element_pic/16/11/11/83b2bc072fb15b3ddd955caeaa0e31c9.jpg\">\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f5f5f5;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "        .certificate-container {\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 40px;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n" +
                "            text-align: center;\n" +
                "            max-width: 800px;\n" +
                "        }\n" +
                "        h1 {\n" +
                "            color: #2a5885;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        p {\n" +
                "            color: #333;\n" +
                "            line-height: 1.6;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"certificate-container\">\n" +
                "        <h1>Chứng Chỉ Hoàn Thành Khóa Học</h1>\n" +
                "        <p>Chúc mừng bạn đã hoàn thành khóa học thành công!</p>\n" +
                "        <p>Đây là trang xác nhận thành tích học tập của bạn. Chứng chỉ này xác nhận rằng bạn đã hoàn thành tất cả các yêu cầu của chương trình đào tạo.</p>\n" +
                "        <p>Kỹ năng và kiến thức bạn đã thu được trong khóa học này sẽ giúp ích cho sự phát triển nghề nghiệp của bạn.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}