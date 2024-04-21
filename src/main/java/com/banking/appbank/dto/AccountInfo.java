package com.banking.appbank.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {

    @Schema(name = "Nombre de cuenta del usuario")
    private String accountName;

    @Schema(name = "Saldo de cuenta del usuario")
    private BigDecimal accountBalance;

    @Schema(name = "Número de cuenta del usuario")
    private String accountNumber;
}
