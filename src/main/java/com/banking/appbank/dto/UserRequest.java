package com.banking.appbank.dto;



import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    private String accountNumber;
    private String email;
    private String password;
    private String phoneNumber;
    private String alternativePhone;    
}
