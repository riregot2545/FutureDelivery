package com.nix.futuredelivery.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractStation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated product ID")
    private Long id;

    @Valid
    @NotNull(message = "Station address is null.")
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @NotEmpty(message = "Name of station is empty.")
    @NotNull(message = "Name of station is null.")
    private String name;
}
