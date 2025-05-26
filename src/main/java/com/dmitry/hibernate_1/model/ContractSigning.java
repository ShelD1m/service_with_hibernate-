package com.dmitry.hibernate_1.model;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sign_contract", schema = "rental")
public class ContractSigning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id", unique = true, nullable = false)
    private int contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", referencedColumnName = "landlord_id", nullable = false)
    private Landlord landlord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", referencedColumnName = "apartment_id", nullable = false)
    private Apartment apartment;

    @Column(name = "contract_date", nullable = false)
    private LocalDate signingDate;

    @Column(name = "contract_duration", nullable = false)
    private String term;

    @OneToOne(mappedBy = "contractSigning", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ContractTermination contractTermination;

    public ContractSigning() {
    }

    public int getContractNumber() { return contractNumber; }
    public void setContractNumber(int contractNumber) { this.contractNumber = contractNumber; }
    public Landlord getLandlord() { return landlord; }
    public void setLandlord(Landlord landlord) { this.landlord = landlord; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Apartment getApartment() { return apartment; }
    public void setApartment(Apartment apartment) { this.apartment = apartment; }
    public LocalDate getSigningDate() { return signingDate; }
    public void setSigningDate(LocalDate signingDate) { this.signingDate = signingDate; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public ContractTermination getContractTermination() { return contractTermination; }
    public void setContractTermination(ContractTermination contractTermination) {
        this.contractTermination = contractTermination;
        if (contractTermination != null) {
            contractTermination.setContractSigning(this);
        }
    }

    @Override
    public String toString() {
        return "ContractSigning{" + "contractNumber='" + contractNumber + '\'' + ", signingDate=" + signingDate + '}';
    }
}
