package com.dmitry.hibernate_1.model;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment", schema = "rental")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_code", referencedColumnName = "service_code", nullable = false)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "payment_date", nullable = false)
    private LocalDate date;



    public Payment() {
    }

    public Payment(Tenant tenant, Service service, Organization organization, LocalDate date) {
        this.tenant = tenant;
        this.service = service;
        this.organization = organization;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toString() {
        return "Payment{" + "id=" + id + ", date=" + date + '}';
    }
}
