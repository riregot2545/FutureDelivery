package com.nix.futuredelivery.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractStation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated product ID")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    private String name;
}
