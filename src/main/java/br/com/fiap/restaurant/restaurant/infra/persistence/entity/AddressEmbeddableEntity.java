package br.com.fiap.restaurant.restaurant.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Embeddable
public class AddressEmbeddableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -7560528619565358826L;

    @Column
    private String street;

    @Column
    private String number;

    @Column
    private String city;

    @Column
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column
    private String complement;
}
