package com.banking.appbank.dto;

import java.math.BigDecimal;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {

    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
    private BigDecimal cantidad;
    
}
