package se.sensera.banking.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import se.sensera.banking.User;

@Data
@AllArgsConstructor
public class UserImpl implements User {
    String id;
    String name;
    String personalIdentificationNumber;
    boolean active;

}
