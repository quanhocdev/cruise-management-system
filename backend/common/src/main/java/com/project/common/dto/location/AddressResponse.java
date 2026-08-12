package com.project.common.dto.location;

public class AddressResponse {

    private String fullAddress;
    private String city;
    private String country;

    public AddressResponse() {
    }

    public AddressResponse(
            String fullAddress,
            String city,
            String country) {
        this.fullAddress = fullAddress;
        this.city = city;
        this.country = country;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}