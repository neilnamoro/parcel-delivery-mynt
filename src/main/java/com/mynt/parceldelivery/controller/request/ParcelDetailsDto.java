package com.mynt.parceldelivery.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelDetailsDto {

    @NotNull(message = "Please enter proper weight value.")
    @Min(value = 1, message = "Weight should be at least 1 kg.")
    @Max(value = 50, message = "Weight should not exceed 50 kgs.")
    private Float weight;

    @NotNull(message = "Please enter proper height value.")
    @Min(value = 1, message = "Weight should be at least 1 cm.")
    private Float height;

    @NotNull(message = "Please enter proper width value.")
    @Min(value = 1, message = "Weight should be at least 1 cm.")
    private Float width;

    @NotNull(message = "Please enter proper length value.")
    @Min(value = 1, message = "Weight should be at least 1 cm.")
    private Float length;

    private String voucherCode;

    public Float getVolume() {
        return this.height * this.length * this.width;
    }

}
