package com.anyspotsleft.anyspotsleft.payments;

import lombok.Data;
//data file to initialize stripe required data on charge proccess.
@Data
public class ChargeRequest {
    public enum Currency {
        CAD, USD;
    }

    private String description;
    private long amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
}

