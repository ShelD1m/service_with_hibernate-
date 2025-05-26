package com.dmitry.hibernate_1.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tenant", schema = "rental")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private int id;

    @Column(name = "passport_number", unique = true, nullable = false)
    private String passportNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContractSigning> contractSignings = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    public Tenant() {
    }

    public Tenant(String passportSerialAndNumber, String fullName, String phoneNumber) {
        this.passportNumber = passportSerialAndNumber;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportSerialAndNumber) { this.passportNumber = passportSerialAndNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public List<ContractSigning> getContractSignings() { return contractSignings; }
    public void setContractSignings(List<ContractSigning> contractSignings) { this.contractSignings = contractSignings; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public void addContractSigning(ContractSigning contractSigning) {
        contractSignings.add(contractSigning);
        contractSigning.setTenant(this);
    }

    public void removeContractSigning(ContractSigning contractSigning) {
        contractSignings.remove(contractSigning);
        contractSigning.setTenant(null);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setTenant(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setTenant(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return id == tenant.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return fullName + " (Паспорт: " + passportNumber + ")";
    }
}