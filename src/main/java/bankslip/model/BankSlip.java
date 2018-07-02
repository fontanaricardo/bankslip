package bankslip.model;

import bankslip.view.View;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table(name = "bankslips")
public class BankSlip implements Serializable {

    private static final long serialVersionUID = -3009157732242241606L;
    private static final BigDecimal ONE_PERCENT = new BigDecimal(0.01);
    private static final BigDecimal HALF_PERCENT = new BigDecimal(0.005);

    @Id
    @Column(name = "id")
    @JsonView(View.BankSlip.class)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @JsonView(View.BankSlip.class)
    @JsonProperty("due_date")
    @Column(name = "dueDate")
    private Date dueDate;

    @NotNull
    @Min(1)
    @JsonView(View.BankSlip.class)
    @JsonProperty("total_in_cents")
    @Column(name = "totalInCents")
    private BigDecimal totalInCents;

    @NotNull
    @NotBlank
    @JsonView(View.BankSlip.class)
    @JsonProperty("customer")
    @Column(name = "customer")
    private String customer;

    @Pattern(regexp = "PENDING|PAID|CANCELED")
    @JsonView(View.BankSlip.class)
    @JsonProperty("status")
    @Column(name = "status")
    private String status;

    public UUID getId() {
        return this.id;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date attribute) {
        this.dueDate = attribute;
    }

    public BigDecimal getTotalInCents() {
        return this.totalInCents;
    }

    public void setTotalInCents(BigDecimal attribute) {
        this.totalInCents = attribute;
    }

    public String getCustomer() {
        return this.customer.trim();
    }

    public void setCustomer(String attribute) {
        this.customer = attribute;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String attribute) {
        this.status = attribute;
    }

    @JsonView(View.BankSlipWithFine.class)
    @JsonProperty("fine")
    public BigDecimal getFine() {
        BigDecimal result = BigDecimal.ZERO;

        if (getStatus() == "PENDING") {
            if (now().isAfter(dueDatePlusDays(10))) {
                result = getTotalInCents().multiply(ONE_PERCENT);
            } else if (now().isAfter(dueDatePlusDays(5))) {
                result = getTotalInCents().multiply(HALF_PERCENT);
            }
        }

		return result.setScale(0,BigDecimal.ROUND_DOWN);
    }

    @JsonIgnore
    private LocalDate dueDatePlusDays(int days) {
        LocalDate localDate = getDueDate()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        return localDate.plusDays(days);
    }

    @JsonIgnore
    private LocalDate now() {
        return LocalDate.now();
    }
}