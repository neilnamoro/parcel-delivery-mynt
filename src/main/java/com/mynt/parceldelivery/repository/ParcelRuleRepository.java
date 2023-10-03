package com.mynt.parceldelivery.repository;

import com.mynt.parceldelivery.domain.ParcelRule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParcelRuleRepository extends CrudRepository<ParcelRule, Long> {

    Optional<ParcelRule> findByRuleName(ParcelRule.RuleName ruleName);

}
