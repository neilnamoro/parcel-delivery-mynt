package com.mynt.parceldelivery.webservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(voucherService, "voucherWebServiceUrl", "https://mynt-exam.mocklab.io/voucher");
        ReflectionTestUtils.setField(voucherService, "voucherWebServiceApiKey", "apikey");
    }

    @Test
    public void testGetVoucherDetails() {
        VoucherDto expectedResponse = new VoucherDto();
        expectedResponse.setCode("MYNT");
        expectedResponse.setDiscount("0.15");
        expectedResponse.setExpiry("2023-10-02");

        String voucherCode = "MYNT";

        String url = "https://mynt-exam.mocklab.io/voucher/" + voucherCode + "&apikey=apikey";

        when(restTemplate.getForObject(url, VoucherDto.class)).thenReturn(expectedResponse);

        VoucherDto actualResponse = voucherService.getVoucherDetails(voucherCode);
        assertEquals(expectedResponse.getCode(), actualResponse.getCode());
        assertEquals(expectedResponse.getDiscount(), actualResponse.getDiscount());
        assertEquals(expectedResponse.getExpiry(), actualResponse.getExpiry());
    }
}
