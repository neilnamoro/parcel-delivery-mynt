package com.mynt.parceldelivery.webservice;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDto {
    private String code;
    private String discount;
    private String expiry;
}
