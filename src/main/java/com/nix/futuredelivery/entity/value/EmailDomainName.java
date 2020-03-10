package com.nix.futuredelivery.entity.value;

import lombok.Data;

import javax.persistence.Embeddable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Embeddable
public class EmailDomainName {
    private final List<String> domains;

    public String getDomainsAsString(){
        return String.join(".", domains);
    }
}
