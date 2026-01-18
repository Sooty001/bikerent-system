package com.example.bikerentapi.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Relation(collectionRelation = "customers", itemRelation = "customer")
public class CustomerResponse extends RepresentationModel<CustomerResponse> {
    private UUID id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer loyaltyPoints;
    private String phoneNumber;
    private String email;
    private LocalDateTime registrationDate;
    private Boolean isDeleted;

    public CustomerResponse() {
    }

    public CustomerResponse(UUID id, String firstName, String lastName, String patronymic, Integer loyaltyPoints, String phoneNumber, String email, LocalDateTime registrationDate, Boolean isDeleted) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.loyaltyPoints = loyaltyPoints;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.registrationDate = registrationDate;
        this.isDeleted = isDeleted;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomerResponse that = (CustomerResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(patronymic, that.patronymic) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(email, that.email) && Objects.equals(registrationDate, that.registrationDate) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, patronymic, phoneNumber, email, registrationDate, isDeleted);
    }
}
