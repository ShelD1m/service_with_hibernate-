package com.dmitry.hibernate_1.model;

import com.dmitry.hibernate_1.model.Apartment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "address", schema = "rental")
public class Address implements Serializable {

    @Id
    @Column(name = "address_id")
    private int addressId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "floor_number")
    private Integer floor;

    @Column(name = "apartment_number")
    private String apartmentNumber;

    @Column(name = "region_name")
    private String region;

    @Column(name = "city_name")
    private String cityName;

    public Address() {}

    // Геттеры и сеттеры
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int id) {
        this.addressId = id;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String street) {
        this.streetName = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String city) {
        this.cityName = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return addressId == address.addressId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId);
    }

    @Override
    public String toString() {
        return (cityName != null ? cityName + ", " : "") +
                (streetName != null ? streetName + ", " : "") +
                (houseNumber != null ? "д. " + houseNumber : "") +
                (apartmentNumber != null ? ", кв. " + apartmentNumber : "");
    }
}
