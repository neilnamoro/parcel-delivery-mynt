package com.mynt.parceldelivery.service;

import com.mynt.parceldelivery.constant.Constants;
import com.mynt.parceldelivery.controller.request.ParcelDetailsDto;
import com.mynt.parceldelivery.controller.response.ParcelCostDto;
import com.mynt.parceldelivery.domain.ParcelRule;
import com.mynt.parceldelivery.exception.ParcelDetailException;
import com.mynt.parceldelivery.exception.VoucherCodeException;
import com.mynt.parceldelivery.repository.ParcelRuleRepository;
import com.mynt.parceldelivery.webservice.VoucherDto;
import com.mynt.parceldelivery.webservice.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

/**
 *
 */
@Service
@AllArgsConstructor
public class ParcelService {

    private final VoucherService voucherService;

    private final ParcelRuleRepository parcelRuleRepository;

    public ParcelCostDto computeParcelPrice(ParcelDetailsDto parcelDetailsDto) {

        ParcelRule.RuleName ruleName =
                ParcelRule.getParcelRule(parcelDetailsDto.getVolume(), parcelDetailsDto.getWeight());

        ParcelRule parcelRule = parcelRuleRepository.findByRuleName(ruleName)
                .orElseThrow(() -> new ParcelDetailException(Constants.NO_PARCEL_RULE_FOUND));

        float cost = computeCost(parcelDetailsDto, parcelRule);

        if (StringUtils.hasLength(parcelDetailsDto.getVoucherCode())) {
            cost = getDiscountedPrice(cost, parcelDetailsDto.getVoucherCode());
        }

        return new ParcelCostDto(cost);
    }

    private float computeCost(ParcelDetailsDto parcelDetailsDto, ParcelRule parcelRule) {
        float cost;
        if (parcelRule.getRuleName() == ParcelRule.RuleName.HEAVY_PARCEL) {
            cost = parcelRule.getBaseCost() * parcelDetailsDto.getWeight();
        } else {
            cost = parcelRule.getBaseCost() * parcelDetailsDto.getVolume();
        }
        return cost;
    }

    private float getDiscountedPrice(float originalCost, String voucherCode) {
        float discounterPrice = 0;
        VoucherDto voucherDto;
        try{
            voucherDto = voucherService.getVoucherDetails(voucherCode);
        } catch (HttpClientErrorException exception) {
            throw new VoucherCodeException(Constants.VOUCHER_IS_INVALID);
        }

        if (voucherDto != null) {
            LocalDate expiryDate = LocalDate.parse(voucherDto.getExpiry());
            if (LocalDate.now().isAfter(expiryDate)) {
                throw new VoucherCodeException(Constants.VOUCHER_IS_EXPIRED);
            }
            discounterPrice = originalCost - (originalCost * Float.parseFloat(voucherDto.getDiscount()));
        }
        return discounterPrice;
    }
}
