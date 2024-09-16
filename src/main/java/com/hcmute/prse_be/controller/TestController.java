package com.hcmute.prse_be.controller;

import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.util.ConvertUtils;
import com.hcmute.prse_be.util.JsonUtils;
import io.swagger.v3.oas.models.headers.Header;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
public class TestController {
    @PostMapping("/test1")
    public JSONObject doTest1(@RequestBody Map<String, Object> request){
        LogService.getgI().info("[TestController] doTest1 : " + JsonUtils.Serialize(request));
        return Response.error("Test1");
    }

    @PostMapping("/test2")
    public JSONObject doTest2(@RequestBody Map<String, Object> request){
        LogService.getgI().info("[TestController] doTest2 : " + JsonUtils.Serialize(request));
        // request body : "code" : 0 => response error  + message : "API ERROR"
        // request body : "code" : 1 => response success + data : {"code" : 1, "msg" : "API SUCCESS"}
        int code = ConvertUtils.toInt(request.get("code"));
        if(code == 1)
        {
            JSONObject response = new JSONObject();
            response.put("code", code);
            response.put("msg", "API SUCCESS");
            return Response.success(response);
        }
        return Response.error("API ERROR");
    }
    @PostMapping("/test")
    public JSONObject doTest(@RequestBody Map<String,Object> request, @RequestHeader("accept-token") String acceptToken){
        LogService.getgI().info("[TestController] doTest : " + JsonUtils.Serialize(request));
        if(acceptToken.equals("UTE123"))
        {
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) request.get("data");

            Random random = new Random();
            int number = random.nextInt(10);
            number = 2;
            if(number > dataList.size())
            {
                JSONObject data = new JSONObject();
                return Response.success(data);
            }
            else {
                JSONObject data = new JSONObject();
                for(Map<String, Object> item : dataList )
                {
                    int id = ConvertUtils.toInt(item.get("id"));
                    if(id==number)
                    {
                        JSONObject dataOutput = new JSONObject();
                        dataOutput.put("id",item.get("id"));
                        dataOutput.put("name",item.get("name"));
                        dataOutput.put("age",item.get("age"));
                        dataOutput.put("email",item.get("email"));
                        return Response.success(dataOutput);
                    }
                }
            }
        }

        return Response.error("ERROR");
    }
    @PostMapping("/test11")
    public JSONObject doTest11(@RequestBody Map<String,Object> request, @RequestHeader("accept-token") String acceptToken)
    {
        if(acceptToken.equals("UTE123"))
        {
            Random random = new Random();
            int numb1 = random.nextInt(5);
            int numb2 = random.nextInt(5);
            numb1 =2;
            numb2 =1;
            List<Map<String,Object>> dataList = (List<Map<String, Object>>) request.get("data");
            JSONObject dataResponse = new JSONObject();
            JSONArray dataArray = new JSONArray();
            if(numb1> dataList.size() && numb2> dataList.size())
            {
                return Response.success(dataResponse);
            }
            else {
                for(Map<String, Object> item : dataList)
                {
                    int numbTemp = ConvertUtils.toInt(item.get("id"));
                    if(numbTemp ==numb1 || numbTemp ==numb2){
                        JSONObject dataTemp = new JSONObject();
                        dataTemp.put("id",item.get("id"));
                        dataTemp.put("name",item.get("name"));
                        dataTemp.put("age", item.get("age"));
                        dataTemp.put("email", item.get("email"));
                        dataArray.add(dataTemp);
                    }
                }
                dataResponse.put("infor",dataArray);
                return Response.success(dataResponse);
            }
        }
        return Response.error("ERROR");
    }

}
