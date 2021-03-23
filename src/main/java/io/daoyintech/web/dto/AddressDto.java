package io.daoyintech.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String address1;
    private String address2;
    private String city;
    private String postcode;

    @Size(min = 2, max = 2)
    private String country;
}
