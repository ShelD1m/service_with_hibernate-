package com.dmitry.hibernate_1.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contract_termination", schema = "rental")
public class ContractTermination {

    @Id
    @Column(name = "termination_id")
    private int contractNumberFk;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "contract_id", referencedColumnName = "contract_id")
    private ContractSigning contractSigning;

    @Column(name = "landlord_id")
    private Integer landlordIdSnapshot;

    @Column(name = "tenant_id")
    private Integer tenantIdSnapshot;

    @Column(name = "apartment_id")
    private Integer apartmentIdSnapshot;

    @Column(name = "termination_reason")
    private String reason;

    @Column(name = "termination_date", nullable = false)
    private LocalDate terminationDate;


    public ContractTermination() {
    }

    public int getContractNumberFk() { return contractNumberFk; }
    public void setContractNumberFk(int contractNumberFk) { this.contractNumberFk = contractNumberFk; }

    public ContractSigning getContractSigning() { return contractSigning; }
    public void setContractSigning(ContractSigning contractSigning) {
        this.contractSigning = contractSigning;
        if (contractSigning != null) {
            this.contractNumberFk = contractSigning.getContractNumber();
        }
    }

    public Integer getLandlordIdSnapshot() { return landlordIdSnapshot; }
    public void setLandlordIdSnapshot(Integer landlordIdSnapshot) { this.landlordIdSnapshot = landlordIdSnapshot; }
    public Integer getTenantIdSnapshot() { return tenantIdSnapshot; }
    public void setTenantIdSnapshot(Integer tenantIdSnapshot) { this.tenantIdSnapshot = tenantIdSnapshot; }
    public Integer getApartmentIdSnapshot() { return apartmentIdSnapshot; }
    public void setApartmentIdSnapshot(Integer apartmentIdSnapshot) { this.apartmentIdSnapshot = apartmentIdSnapshot; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDate getTerminationDate() { return terminationDate; }
    public void setTerminationDate(LocalDate terminationDate) { this.terminationDate = terminationDate; }


    @Override
    public String toString() {
        return "ContractTermination{" +
                "contractNumberFk='" + contractNumberFk + '\'' +
                ", terminationDate=" + terminationDate +
                ", reason='" + reason + '\'' +
                '}';
    }
}
