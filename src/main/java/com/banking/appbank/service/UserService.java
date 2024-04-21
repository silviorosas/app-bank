package com.banking.appbank.service;

import com.banking.appbank.dto.BankResponse;
import com.banking.appbank.dto.CreditDebitRequest;
import com.banking.appbank.dto.EnquiryRequest;
import com.banking.appbank.dto.TransferRequest;
import com.banking.appbank.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);

    //consulta de saldo
    BankResponse balanceEnquirty(EnquiryRequest request);

    //nombre de la consulta
    String nameEnquirty(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transfer(TransferRequest request);
}
