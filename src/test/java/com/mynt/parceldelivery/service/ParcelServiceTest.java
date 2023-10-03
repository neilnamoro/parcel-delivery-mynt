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
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ParcelServiceTest {

    @Autowired
    private ParcelService parcelService;

    @MockBean
    private ParcelRuleRepository parcelRuleRepository;

    @MockBean
    private VoucherService voucherService;


    @Test
    public void testHeavyParcelWithDiscount() {
        ParcelDetailsDto parcelDetailsDto = ParcelDetailsDto.builder()
                .weight(15f)
                .length(5f)
                .width(5f)
                .height(5f)
                .voucherCode("MYNT")
                .build();

        ParcelRule heavyParcelRule = ParcelRule.builder()
                .ruleName(ParcelRule.RuleName.HEAVY_PARCEL)
                .baseCost(20f)
                .build();

        VoucherDto voucherDto = VoucherDto.builder()
                .code("MYNT")
                .discount("0.15")
                .expiry(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();

        when(parcelRuleRepository.findByRuleName(ParcelRule.RuleName.HEAVY_PARCEL))
                .thenReturn(Optional.ofNullable(heavyParcelRule));

        when(voucherService.getVoucherDetails("MYNT")).thenReturn(voucherDto);

        ParcelCostDto parcelCostDto = parcelService.computeParcelPrice(parcelDetailsDto);
        assertEquals(255f, parcelCostDto.getParcelCost());
    }

    @Test
    public void testHeavyParcelWithInvalidVoucher() {
        ParcelDetailsDto parcelDetailsDto = ParcelDetailsDto.builder()
                .weight(15f)
                .length(5f)
                .width(5f)
                .height(5f)
                .voucherCode("INVALID")
                .build();

        ParcelRule heavyParcelRule = ParcelRule.builder()
                .ruleName(ParcelRule.RuleName.HEAVY_PARCEL)
                .baseCost(20f)
                .build();

        when(parcelRuleRepository.findByRuleName(ParcelRule.RuleName.HEAVY_PARCEL))
                .thenReturn(Optional.ofNullable(heavyParcelRule));

        when(voucherService.getVoucherDetails("INVALID"))
                .thenThrow(HttpClientErrorException.class);

        VoucherCodeException exception = assertThrows(VoucherCodeException.class,
                () -> parcelService.computeParcelPrice(parcelDetailsDto));

        assertEquals(Constants.VOUCHER_IS_INVALID, exception.getMessage());
    }

    @Test
    public void testHeavyParcelWithExpiredVoucher() {
        ParcelDetailsDto parcelDetailsDto = ParcelDetailsDto.builder()
                .weight(15f)
                .length(5f)
                .width(5f)
                .height(5f)
                .voucherCode("MYNT")
                .build();

        ParcelRule heavyParcelRule = ParcelRule.builder()
                .ruleName(ParcelRule.RuleName.HEAVY_PARCEL)
                .baseCost(20f)
                .build();

        VoucherDto voucherDto = VoucherDto.builder()
                .code("MYNT")
                .discount("0.15")
                .expiry(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();

        when(parcelRuleRepository.findByRuleName(ParcelRule.RuleName.HEAVY_PARCEL))
                .thenReturn(Optional.ofNullable(heavyParcelRule));

        when(voucherService.getVoucherDetails("MYNT")).thenReturn(voucherDto);

        VoucherCodeException exception = assertThrows(VoucherCodeException.class,
                () -> parcelService.computeParcelPrice(parcelDetailsDto));

        assertEquals(Constants.VOUCHER_IS_EXPIRED, exception.getMessage());
    }

    @Test
    public void testSmallParcelWithoutVoucher() {
        ParcelDetailsDto parcelDetailsDto = ParcelDetailsDto.builder()
                .weight(10f)
                .length(5f)
                .width(5f)
                .height(5f)
                .voucherCode(null)
                .build();

        ParcelRule smallParcelRule = ParcelRule.builder()
                .ruleName(ParcelRule.RuleName.SMALL_PARCEL)
                .baseCost(0.03f)
                .build();

        when(parcelRuleRepository.findByRuleName(ParcelRule.RuleName.SMALL_PARCEL))
                .thenReturn(Optional.ofNullable(smallParcelRule));

        ParcelCostDto parcelCostDto = parcelService.computeParcelPrice(parcelDetailsDto);
        assertEquals(3.75f, parcelCostDto.getParcelCost());
    }

    @Test
    public void testMediumParcelWithoutVoucher() {
        ParcelDetailsDto parcelDetailsDto = ParcelDetailsDto.builder()
                .weight(10f)
                .length(25f)
                .width(25f)
                .height(3f)
                .voucherCode(null)
                .build();

        ParcelRule mediumParcelRule = ParcelRule.builder()
                .ruleName(ParcelRule.RuleName.MEDIUM_PARCEL)
                .baseCost(0.04f)
                .build();

        when(parcelRuleRepository.findByRuleName(ParcelRule.RuleName.MEDIUM_PARCEL))
                .thenReturn(Optional.ofNullable(mediumParcelRule));

        ParcelCostDto parcelCostDto = parcelService.computeParcelPrice(parcelDetailsDto);
        assertEquals(75f, parcelCostDto.getParcelCost());
    }

    @Test
    public void testLargeParcelWithoutVoucher() {
        ParcelDetailsDto parcelDetailsDto = ParcelDetailsDto.builder()
                .weight(10f)
                .length(25f)
                .width(25f)
                .height(5f)
                .voucherCode(null)
                .build();

        ParcelRule largeParcelRule = ParcelRule.builder()
                .ruleName(ParcelRule.RuleName.LARGE_PARCEL)
                .baseCost(0.05f)
                .build();

        when(parcelRuleRepository.findByRuleName(ParcelRule.RuleName.LARGE_PARCEL))
                .thenReturn(Optional.ofNullable(largeParcelRule));

        ParcelCostDto parcelCostDto = parcelService.computeParcelPrice(parcelDetailsDto);
        assertEquals(156.25f, parcelCostDto.getParcelCost());
    }
}
