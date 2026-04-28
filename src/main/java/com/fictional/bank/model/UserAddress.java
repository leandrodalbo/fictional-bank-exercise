package com.fictional.bank.model;

import jakarta.persistence.Embeddable;
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
    private String line1;
    private String line2;
    private String line3;
    private String town;
    private String county;
    private String postcode;
}
