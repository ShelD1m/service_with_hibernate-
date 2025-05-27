package com.dmitry.hibernate_1.model;

import com.dmitry.hibernate_1.model.Address;
import com.dmitry.hibernate_1.model.ContractSigning;
import com.dmitry.hibernate_1.model.Landlord;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "apartment", schema = "rental")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apartment_id")
    private int apartmentId;

    @Column(name = "room_count")
    private Integer roomCount;

    @Column(name = "square_meters")
    private Double squareMeters;

    @OneToOne(mappedBy = "apartment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContractSigning> contractSignings = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", referencedColumnName = "landlord_id")
    private Landlord landlordId;

    public Apartment() {}

    // Геттеры и сеттеры
    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int id) {
        this.apartmentId = id;
    }

    public Integer getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(Integer numberOfRooms) {
        this.roomCount = numberOfRooms;
    }

    public Double getSquareMeters() {
        return squareMeters;
    }

    public void setSquareMeters(Double squareMeters) {
        this.squareMeters = squareMeters;
    }

    /*public Address getAddress() {
        return address;
    }*/

    /*public void setAddress(Address address) {
        this.address = address;
        if (address != null) {
            address.setApartment(this);
        }
    }*/

    public List<ContractSigning> getContractSignings() {
        return contractSignings;
    }

    public void setContractSignings(List<ContractSigning> contractSignings) {
        this.contractSignings = contractSignings;
    }

    public Landlord getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(Landlord owner) {
        this.landlordId = owner;
    }


    public void addContractSigning(ContractSigning contractSigning) {
        contractSignings.add(contractSigning);
        contractSigning.setApartment(this);
    }

    public void removeContractSigning(ContractSigning contractSigning) {
        contractSignings.remove(contractSigning);
        contractSigning.setApartment(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Apartment)) return false;
        Apartment that = (Apartment) o;
        return apartmentId == that.apartmentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(apartmentId);
    }

    @Override
    public String toString() {
        return "Apartment{id=" + apartmentId +
                /*(address != null ? ", address=" + address.toString() : "") +*/
                ", rooms=" + roomCount +
                '}';
    }
}
