package com.dmitry.hibernate_1.model;


import javax.persistence.*;
import java.math.BigDecimal; // Используем BigDecimal для стоимости
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "service", schema = "rental")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_code")
    private int id;

    @Column(name = "service_name", nullable = false)
    private String name;

    @Column(name = "service_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "service_type")
    private String type;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    public Service() {
    }

    public Service(String name, BigDecimal cost, String type) {
        this.name = name;
        this.cost = cost;
        this.type = type;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setService(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setService(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return id == service.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + cost + " руб.)";
    }
}