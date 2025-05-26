package com.dmitry.hibernate_1.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "landlord", schema = "rental")
public class Landlord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "landlord_id")
    private int landlordId;

    @Column(name = "passport_number", unique = true, nullable = false)
    private String passportNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "landlord", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContractSigning> contractSignings = new ArrayList<>();

    @OneToMany(mappedBy = "landlordId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
    private List<Apartment> ownedApartments = new ArrayList<>();

    public Landlord(String passportSerialAndNumber, String fullName, String phoneNumber) {
        this.passportNumber = passportSerialAndNumber;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }
    public Landlord() {
    }

    public int getLandlordId() { return landlordId; }
    public void setLandlordId(int id) { this.landlordId = id; }
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportSerialAndNumber) { this.passportNumber = passportSerialAndNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public List<ContractSigning> getContractSignings() { return contractSignings; }
    public void setContractSignings(List<ContractSigning> contractSignings) { this.contractSignings = contractSignings; }
    public List<Apartment> getOwnedApartments() { return ownedApartments; }
    public void setOwnedApartments(List<Apartment> ownedApartments) { this.ownedApartments = ownedApartments; }

    public void addContractSigning(ContractSigning contractSigning) {
        contractSignings.add(contractSigning);
        contractSigning.setLandlord(this);
    }

    public void removeContractSigning(ContractSigning contractSigning) {
        contractSignings.remove(contractSigning);
        contractSigning.setLandlord(null);
    }

    public void addOwnedApartment(Apartment apartment) {
        ownedApartments.add(apartment);
        apartment.setLandlordId(this);
    }

    public void removeOwnedApartment(Apartment apartment) {
        ownedApartments.remove(apartment);
        apartment.setLandlordId(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Landlord landlord = (Landlord) o;
        return landlordId == landlord.landlordId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(landlordId);
    }

    @Override
    public String toString() {
        return "Landlord{" +
                "id=" + landlordId +
                ", fullName='" + fullName + '\'' +
                ", passportSerialAndNumber='" + passportNumber + '\'' +
                '}';
    }
}
