package services.invoicesender.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Invoice {
    private String seller;
    
    private String customer;

    @JsonCreator
    public Invoice(@JsonProperty("seller") String seller, @JsonProperty("customer") String customer) {
        this.seller = seller;
        this.customer = customer;
    }

    public String getSeller() {
        return seller;
    }

    public String getCustomer() {
        return customer;
    }
}
