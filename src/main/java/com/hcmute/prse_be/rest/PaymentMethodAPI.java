package com.hcmute.prse_be.rest;


import com.hcmute.prse_be.constants.ApiPaths;
import com.hcmute.prse_be.response.Response;
import com.hcmute.prse_be.service.LogService;
import com.hcmute.prse_be.service.PaymentMethodService;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PAYMENT_METHOD_API)
public class PaymentMethodAPI {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodAPI(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }


    @GetMapping("")
    public ResponseEntity<JSONObject> getPaymentMethod() {
        LogService.getgI().info("[PaymentMethodAPI] getPaymentMethod " );

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payment_method", paymentMethodService.getPaymentMethodActive());
        return ResponseEntity.ok(Response.success(jsonObject));
    }
}
