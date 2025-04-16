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
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Chứng Chỉ Của Bạn</title>
                    <meta name="description" content="Trang chứng chỉ video của bạn - hãy xem và lưu giữ thành quả học tập của mình!">
                    <meta name="author" content="Hệ Thống Đào Tạo XYZ">
                    <meta name="robots" content="index, follow">

                    <!-- Open Graph cho Facebook / Zalo -->
                    <meta property="og:title" content="Chứng Chỉ Của Bạn - Hoàn Thành Khóa Học!">
                    <meta property="og:description" content="Chúc mừng bạn đã hoàn thành khóa học. Xem video chứng chỉ ngay bây giờ!">
                    <meta property="og:image" content="https://png.pngtree.com/element_pic/16/11/11/83b2bc072fb15b3ddd955caeaa0e31c9.jpg">
                    <meta property="og:image:width" content="800">
                    <meta property="og:image:height" content="600">
                    <meta property="og:url" content="https://prse-be.ddns.net:8443/testcontroller">
                    <meta property="og:type" content="website">
                    <meta property="og:site_name" content="Hệ Thống Đào Tạo XYZ">

                    <!-- Twitter Card -->
                    <meta name="twitter:card" content="summary_large_image">
                    <meta name="twitter:title" content="Chứng Chỉ Của Bạn - Khóa học hoàn tất!">
                    <meta name="twitter:description" content="Chứng nhận thành tích học tập của bạn. Click để xem video!">
                    <meta name="twitter:image" content="https://png.pngtree.com/element_pic/16/11/11/83b2bc072fb15b3ddd955caeaa0e31c9.jpg">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f5f5f5;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                        }
                        .certificate-container {
                            background-color: #ffffff;
                            padding: 40px;
                            border-radius: 10px;
                            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                            text-align: center;
                            max-width: 800px;
                        }
                        h1 {
                            color: #2a5885;
                            margin-bottom: 20px;
                        }
                        p {
                            color: #333;
                            line-height: 1.6;
                            margin-bottom: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="certificate-container">
                        <h1>Chứng Chỉ Hoàn Thành Khóa Học</h1>
                        <p>Chúc mừng bạn đã hoàn thành khóa học thành công!</p>
                        <p>Đây là trang xác nhận thành tích học tập của bạn. Chứng chỉ này xác nhận rằng bạn đã hoàn thành tất cả các yêu cầu của chương trình đào tạo.</p>
                        <p>Kỹ năng và kiến thức bạn đã thu được trong khóa học này sẽ giúp ích cho sự phát triển nghề nghiệp của bạn.</p>
                    </div>
                </body>
                </html>""";
    }

    @GetMapping("/robots.txt")
    @ResponseBody
    public String robotsTxtAsHtml() {
        // Trả về cùng nội dung HTML như endpoint chính
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Chứng Chỉ Của Bạn</title>
                <meta name="description" content="Trang chứng chỉ video của bạn - hãy xem và lưu giữ thành quả học tập của mình!">
                <meta name="author" content="Hệ Thống Đào Tạo XYZ">
                <meta name="robots" content="index, follow">

                <!-- Open Graph cho Facebook / Zalo -->
                <meta property="og:title" content="Chứng Chỉ Của Bạn - Hoàn Thành Khóa Học!">
                <meta property="og:description" content="Chúc mừng bạn đã hoàn thành khóa học. Xem video chứng chỉ ngay bây giờ!">
                <meta property="og:image" content="https://png.pngtree.com/element_pic/16/11/11/83b2bc072fb15b3ddd955caeaa0e31c9.jpg">
                <meta property="og:image:width" content="800">
                <meta property="og:image:height" content="600">
                <meta property="og:url" content="https://prse-be.ddns.net:8443/testcontroller">
                <meta property="og:type" content="website">
                <meta property="og:site_name" content="Hệ Thống Đào Tạo XYZ">

                <!-- Twitter Card -->
                <meta name="twitter:card" content="summary_large_image">
                <meta name="twitter:title" content="Chứng Chỉ Của Bạn - Khóa học hoàn tất!">
                <meta name="twitter:description" content="Chứng nhận thành tích học tập của bạn. Click để xem video!">
                <meta name="twitter:image" content="https://png.pngtree.com/element_pic/16/11/11/83b2bc072fb15b3ddd955caeaa0e31c9.jpg">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f5f5f5;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                    }
                    .certificate-container {
                        background-color: #ffffff;
                        padding: 40px;
                        border-radius: 10px;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                        text-align: center;
                        max-width: 800px;
                    }
                    h1 {
                        color: #2a5885;
                        margin-bottom: 20px;
                    }
                    p {
                        color: #333;
                        line-height: 1.6;
                        margin-bottom: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="certificate-container">
                    <h1>Chứng Chỉ Hoàn Thành Khóa Học</h1>
                    <p>Chúc mừng bạn đã hoàn thành khóa học thành công!</p>
                    <p>Đây là trang xác nhận thành tích học tập của bạn. Chứng chỉ này xác nhận rằng bạn đã hoàn thành tất cả các yêu cầu của chương trình đào tạo.</p>
                    <p>Kỹ năng và kiến thức bạn đã thu được trong khóa học này sẽ giúp ích cho sự phát triển nghề nghiệp của bạn.</p>
                </div>
            </body>
            </html>""";
    }
}