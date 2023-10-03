package com.mynt.parceldelivery.webservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VoucherService {

    @Value("${voucher.webservice.url}")
    private String voucherWebServiceUrl;

    @Value("${voucher.webservice.apikey}")
    private String voucherWebServiceApiKey;

    @Autowired
    private RestTemplate restTemplate;

    public VoucherDto getVoucherDetails(String voucherCode) {
        // Make a GET request to the external voucher web service
        String url = voucherWebServiceUrl + "/" + voucherCode + "&apikey=" + voucherWebServiceApiKey;
        return restTemplate.getForObject(url, VoucherDto.class);
    }
}
