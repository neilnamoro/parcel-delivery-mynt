package com.mynt.parceldelivery.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mynt.parceldelivery.controller.request.ParcelDetailsDto;
import com.mynt.parceldelivery.controller.response.ParcelCostDto;
import com.mynt.parceldelivery.service.ParcelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@WebMvcTest
@AutoConfigureMockMvc
public class ParcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParcelController parcelController;

    @MockBean
    private ParcelService parcelService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetParcelCostWithInvalidRequestBody_returnBadRequest() throws Exception {
        String jsonContent = """
            {
                 "weight":"",
                 "height":"",
                 "length":"",
                 "width":"",
                 "voucher":""
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/parcels/parcel-cost")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.weight").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.weight").value("Please enter proper weight value."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.height").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.height").value("Please enter proper height value."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length").value("Please enter proper length value."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.width").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.width").value("Please enter proper width value."));
    }

    @Test
    public void testGetParcelCostWithInvalidWeightValue_returnBadRequest() throws Exception {
        String jsonContent = """
            {
                 "weight":"51",
                 "height":"1",
                 "length":"1",
                 "width":"1",
                 "voucher":""
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/parcels/parcel-cost")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.weight").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.weight").value("Weight should not exceed 50 kgs."));
    }

    @Test
    public void testGetParcel_returnOk() throws Exception {
        ParcelDetailsDto parcelDetailsDto = ParcelDetailsDto.builder()
                .weight(40f)
                .width(1f)
                .length(1f)
                .height(1f)
                .voucherCode("")
                .build();

        ParcelCostDto parcelCostDto = new ParcelCostDto(2f);


        when(parcelService.computeParcelPrice(parcelDetailsDto)).thenReturn(parcelCostDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/parcels/parcel-cost")
                        .content(convertToJson(parcelDetailsDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parcelCost").value("2.0"));
    }

    private String convertToJson(ParcelDetailsDto parcelDetailsDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(parcelDetailsDto);
    }
}
