package com.nix.futuredelivery.entity.value;


import javax.persistence.AttributeConverter;
import java.util.Arrays;

public class EmailConverter implements AttributeConverter<EmailAddress, String> {
    @Override
    public String convertToDatabaseColumn(EmailAddress emailAddress) {
        return emailAddress.getLogin() +"@"+ emailAddress.getEmailDomainName().getDomainsAsString();
    }

    @Override
    public EmailAddress convertToEntityAttribute(String s) {
        String[] parts = s.split("@");
        String login = parts[0];
        EmailDomainName domain = new EmailDomainName(Arrays.asList(parts[1].split("\\.")));
        return new EmailAddress(login,domain);
    }
}
