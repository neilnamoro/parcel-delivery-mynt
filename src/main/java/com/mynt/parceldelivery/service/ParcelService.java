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


@Service
@AllArgsConstructor
public class ParcelService {

    private final VoucherService voucherService;

    private final ParcelRuleRepository parcelRuleRepository;

    public ParcelCostDto computeParcelPrice(ParcelDetailsDto parcelDetailsDto) {
        float volume = parcelDetailsDto.getHeight() * parcelDetailsDto.getLength() * parcelDetailsDto.getWidth();

        ParcelRule.RuleName ruleName = getRuleName(volume, parcelDetailsDto.getWeight());

        ParcelRule parcelRule = parcelRuleRepository.findByRuleName(ruleName)
                .orElseThrow(() -> new ParcelDetailException(Constants.NO_PARCEL_RULE_FOUND));

        float cost = computeCost(volume, parcelDetailsDto.getWeight(),
                parcelRule.getBaseCost(), ruleName);

        if (StringUtils.hasLength(parcelDetailsDto.getVoucherCode())) {
            cost = getDiscountedPrice(cost, parcelDetailsDto.getVoucherCode());
        }

        return new ParcelCostDto(cost);
    }

    private ParcelRule.RuleName getRuleName(float volume, float weight) {

        ParcelRule.RuleName ruleName;

        if (Float.compare(weight, 10f) > 0) return ParcelRule.RuleName.HEAVY_PARCEL;

        if (volume < 1500f) {
            ruleName = ParcelRule.RuleName.SMALL_PARCEL;
        } else if (volume >= 1500f && volume < 2500f) {
            ruleName = ParcelRule.RuleName.MEDIUM_PARCEL;
        } else {
            ruleName = ParcelRule.RuleName.LARGE_PARCEL;
        }

        return ruleName;
    }

    private float computeCost(float volume, float weight, float baseCost, ParcelRule.RuleName ruleName) {
        float cost;
        if (ruleName == ParcelRule.RuleName.HEAVY_PARCEL) {
            cost = baseCost * weight;
        } else {
            cost = baseCost * volume;
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
