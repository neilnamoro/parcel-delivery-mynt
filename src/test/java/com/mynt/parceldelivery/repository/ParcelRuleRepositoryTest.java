package com.mynt.parceldelivery.repository;

import com.mynt.parceldelivery.domain.ParcelRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ParcelRuleRepositoryTest {

    @Autowired
    private ParcelRuleRepository parcelRuleRepository;

    @BeforeEach
    public void setUp() {
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

    @Test
    public void testFindByRuleName() {
        Optional<ParcelRule> heavyParcel = parcelRuleRepository.findByRuleName(ParcelRule.RuleName.HEAVY_PARCEL);
        assertTrue(heavyParcel.isPresent());
        assertEquals(20f, heavyParcel.get().getBaseCost());

        Optional<ParcelRule> smallParcel = parcelRuleRepository.findByRuleName(ParcelRule.RuleName.SMALL_PARCEL);
        assertTrue(smallParcel.isPresent());
        assertEquals(0.03f, smallParcel.get().getBaseCost());

        Optional<ParcelRule> mediumParcel = parcelRuleRepository.findByRuleName(ParcelRule.RuleName.MEDIUM_PARCEL);
        assertTrue(mediumParcel.isPresent());
        assertEquals(0.04f, mediumParcel.get().getBaseCost());

        Optional<ParcelRule> largeParcel = parcelRuleRepository.findByRuleName(ParcelRule.RuleName.LARGE_PARCEL);
        assertTrue(largeParcel.isPresent());
        assertEquals(0.05f, largeParcel.get().getBaseCost());
    }

}
