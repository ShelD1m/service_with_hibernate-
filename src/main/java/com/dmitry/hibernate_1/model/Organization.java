package com.dmitry.hibernate_1.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "organization", schema = "rental")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private int id;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "tax_id", unique = true)
    private String taxId;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    public Organization() {
    }

    public Organization(String name, String inn, String website) {
        this.organizationName = name;
        this.taxId = inn;
        this.websiteUrl = website;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String website) { this.websiteUrl = website; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String name) { this.organizationName = name; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String inn) { this.taxId = inn; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setOrganization(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setOrganization(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return organizationName + " (ИНН: " + taxId + ")";
    }
}
