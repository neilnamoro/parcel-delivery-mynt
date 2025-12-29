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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final VoucherService voucherService;
    private final ParcelRuleRepository parcelRuleRepository;

    public ParcelCostDto computeParcelPrice(ParcelDetailsDto parcelDetailsDto) {
        ParcelRule parcelRule = getParcelRule(parcelDetailsDto);
        float baseCost = calculateBaseCost(parcelDetailsDto, parcelRule);

        if (!StringUtils.hasLength(parcelDetailsDto.getVoucherCode())) {
            return new ParcelCostDto(baseCost);
        }

        float discountedCost = applyVoucherDiscount(baseCost, parcelDetailsDto.getVoucherCode());
        return new ParcelCostDto(discountedCost);
    }

    private ParcelRule getParcelRule(ParcelDetailsDto details) {
        ParcelRule.RuleName ruleName = ParcelRule.getParcelRule(details.getVolume(), details.getWeight());
        return parcelRuleRepository.findByRuleName(ruleName)
                .orElseThrow(() -> new ParcelDetailException(Constants.NO_PARCEL_RULE_FOUND));
    }

    private float calculateBaseCost(ParcelDetailsDto details, ParcelRule rule) {
        float multiplier = rule.getRuleName() == ParcelRule.RuleName.HEAVY_PARCEL
                ? details.getWeight()
                : details.getVolume();
        return rule.getBaseCost() * multiplier;
    }

    private float applyVoucherDiscount(float originalCost, String voucherCode) {
        VoucherDto voucher = fetchValidVoucher(voucherCode);
        float discountRate = Float.parseFloat(voucher.getDiscount());
        return originalCost * (1 - discountRate);
    }

    private VoucherDto fetchValidVoucher(String voucherCode) {
        try {
            VoucherDto voucher = voucherService.getVoucherDetails(voucherCode);
            if (voucher == null || isExpired(voucher.getExpiry())) {
                throw new VoucherCodeException(Constants.VOUCHER_IS_EXPIRED);
            }
            return voucher;
        } catch (HttpClientErrorException e) {
            throw new VoucherCodeException(Constants.VOUCHER_IS_INVALID);
        }
    }

    private boolean isExpired(String expiryDateStr) {
        LocalDate expiryDate = LocalDate.parse(expiryDateStr);
        return LocalDate.now().isAfter(expiryDate);
    }
}