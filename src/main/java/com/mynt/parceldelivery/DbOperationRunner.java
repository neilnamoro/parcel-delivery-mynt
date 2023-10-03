package com.mynt.parceldelivery;

import com.mynt.parceldelivery.domain.ParcelRule;
import com.mynt.parceldelivery.repository.ParcelRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This component is just to insert the default parcel rules in the memory db when the application runs.
 * Helpful when testing in swagger ui.
 */
@Component
public class DbOperationRunner implements CommandLineRunner {

    @Autowired
    private ParcelRuleRepository parcelRuleRepository;
    @Override
    public void run(String... args) throws Exception {
        ParcelRule heavyParcel = ParcelRule.builder()
                .baseCost(20f)
                .ruleName(ParcelRule.RuleName.HEAVY_PARCEL)
                .build();

        ParcelRule smallParcel = ParcelRule.builder()
                .baseCost(0.03f)
                .ruleName(ParcelRule.RuleName.SMALL_PARCEL)
                .build();

        ParcelRule mediumParcel = ParcelRule.builder()
                .baseCost(0.04f)
                .ruleName(ParcelRule.RuleName.MEDIUM_PARCEL)
                .build();

        ParcelRule largeParcel = ParcelRule.builder()
                .baseCost(0.05f)
                .ruleName(ParcelRule.RuleName.LARGE_PARCEL)
                .build();

        List<ParcelRule> parcelRules = List.of(heavyParcel, smallParcel, mediumParcel, largeParcel);
        parcelRuleRepository.saveAll(parcelRules);
    }
}
