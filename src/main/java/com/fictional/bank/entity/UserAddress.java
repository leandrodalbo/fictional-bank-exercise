package com.fictional.bank.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAddress {
    @NotBlank
    private String line1;
    private String line2;
    private String line3;
    @NotBlank
    private String town;
    @NotBlank
    private String county;
    @NotBlank
    private String postcode;
}
