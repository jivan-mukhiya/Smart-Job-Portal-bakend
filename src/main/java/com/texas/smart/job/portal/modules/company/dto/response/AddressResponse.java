package com.texas.smart.job.portal.modules.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}