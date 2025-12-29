package com.mynt.parceldelivery.service;

import com.mynt.parceldelivery.constant.Constants;
import com.mynt.parceldelivery.controller.request.ParcelDetailsDto;
import com.mynt.parceldelivery.controller.response.ParcelCostDto;
import com.mynt.parceldelivery.domain.ParcelRule;
import com.mynt.parceldelivery.exception.VoucherCodeException;
import com.mynt.parceldelivery.repository.ParcelRuleRepository;
import com.mynt.parceldelivery.webservice.VoucherDto;
import com.mynt.parceldelivery.webservice.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ParcelServiceTest {

    @Autowired
    private ParcelService parcelService;

    @MockBean
    private ParcelRuleRepository parcelRuleRepository;

    @MockBean
    private VoucherService voucherService;

    private ParcelDetailsDto createParcel(float weight, float length, float width, float height, String voucherCode) {
        return ParcelDetailsDto.builder()
                .weight(weight)
                .length(length)
                .width(width)
                .height(height)
                .voucherCode(voucherCode)
                .build();
    }

    private ParcelRule createRule(ParcelRule.RuleName ruleName, float baseCost) {
        return ParcelRule.builder()
                .ruleName(ruleName)
                .baseCost(baseCost)
                .build();
    }

    private VoucherDto createVoucher(String code, String discount, LocalDate expiry) {
        return VoucherDto.builder()
                .code(code)
                .discount(discount)
                .expiry(expiry.toString())
                .build();
    }

    @Test
    void computesHeavyParcelWithValidVoucher() {
        ParcelDetailsDto parcel = createParcel(15f, 5f, 5f, 5f, "MYNT");
        ParcelRule rule = createRule(ParcelRule.RuleName.HEAVY_PARCEL, 20f);
        VoucherDto voucher = createVoucher("MYNT", "0.15", LocalDate.now());

        when(parcelRuleRepository.findByRuleName(rule.getRuleName())).thenReturn(Optional.of(rule));
        when(voucherService.getVoucherDetails("MYNT")).thenReturn(voucher);

        ParcelCostDto result = parcelService.computeParcelPrice(parcel);
        assertEquals(255f, result.getParcelCost());
    }

    @Test
    void throwsExceptionForInvalidVoucher() {
        ParcelDetailsDto parcel = createParcel(15f, 5f, 5f, 5f, "INVALID");
        ParcelRule rule = createRule(ParcelRule.RuleName.HEAVY_PARCEL, 20f);

        when(parcelRuleRepository.findByRuleName(rule.getRuleName())).thenReturn(Optional.of(rule));
        when(voucherService.getVoucherDetails("INVALID")).thenThrow(HttpClientErrorException.class);

        VoucherCodeException ex = assertThrows(VoucherCodeException.class,
                () -> parcelService.computeParcelPrice(parcel));
        assertEquals(Constants.VOUCHER_IS_INVALID, ex.getMessage());
    }

    @Test
    void throwsExceptionForExpiredVoucher() {
        ParcelDetailsDto parcel = createParcel(15f, 5f, 5f, 5f, "MYNT");
        ParcelRule rule = createRule(ParcelRule.RuleName.HEAVY_PARCEL, 20f);
        VoucherDto expiredVoucher = createVoucher("MYNT", "0.15", LocalDate.now().minusDays(1));

        when(parcelRuleRepository.findByRuleName(rule.getRuleName())).thenReturn(Optional.of(rule));
        when(voucherService.getVoucherDetails("MYNT")).thenReturn(expiredVoucher);

        VoucherCodeException ex = assertThrows(VoucherCodeException.class,
                () -> parcelService.computeParcelPrice(parcel));
        assertEquals(Constants.VOUCHER_IS_EXPIRED, ex.getMessage());
    }

    @Test
    void computesSmallParcelWithoutVoucher() {
        ParcelDetailsDto parcel = createParcel(10f, 5f, 5f, 5f, null);
        ParcelRule rule = createRule(ParcelRule.RuleName.SMALL_PARCEL, 0.03f);

        when(parcelRuleRepository.findByRuleName(rule.getRuleName())).thenReturn(Optional.of(rule));

        ParcelCostDto result = parcelService.computeParcelPrice(parcel);
        assertEquals(3.75f, result.getParcelCost());
    }

    @Test
    void computesMediumParcelWithoutVoucher() {
        ParcelDetailsDto parcel = createParcel(10f, 25f, 25f, 3f, null);
        ParcelRule rule = createRule(ParcelRule.RuleName.MEDIUM_PARCEL, 0.04f);

        when(parcelRuleRepository.findByRuleName(rule.getRuleName())).thenReturn(Optional.of(rule));

        ParcelCostDto result = parcelService.computeParcelPrice(parcel);
        assertEquals(75f, result.getParcelCost());
    }

    @Test
    void computesLargeParcelWithoutVoucher() {
        ParcelDetailsDto parcel = createParcel(10f, 25f, 25f, 5f, null);
        ParcelRule rule = createRule(ParcelRule.RuleName.LARGE_PARCEL, 0.05f);

        when(parcelRuleRepository.findByRuleName(rule.getRuleName())).thenReturn(Optional.of(rule));

        ParcelCostDto result = parcelService.computeParcelPrice(parcel);
        assertEquals(156.25f, result.getParcelCost());
    }
}