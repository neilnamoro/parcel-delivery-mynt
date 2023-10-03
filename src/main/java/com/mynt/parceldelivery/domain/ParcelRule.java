package com.mynt.parceldelivery.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ParcelRule {

    public enum RuleName {
        HEAVY_PARCEL,
        SMALL_PARCEL,
        MEDIUM_PARCEL,
        LARGE_PARCEL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated
    @Column(name = "rule_name", unique = true)
    private RuleName ruleName;

    @Column(name = "base_cost")
    private float baseCost;

}
