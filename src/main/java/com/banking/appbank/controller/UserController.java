package com.banking.appbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.appbank.dto.BankResponse;
import com.banking.appbank.dto.CreditDebitRequest;
import com.banking.appbank.dto.EnquiryRequest;
import com.banking.appbank.dto.LoginDto;
import com.banking.appbank.dto.TransferRequest;
import com.banking.appbank.dto.UserRequest;
import com.banking.appbank.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "API de gestión de cuentas de usuario")
public class UserController {
    
    @Autowired
    UserServiceImpl userServiceImpl;

    @Operation(
        summary = "Crear una nueva cuenta de usuario",
        description = "Crear un new user y asignarle un número de cuenta"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Http status 201 CREATED"
    )
    @PostMapping
    public BankResponse addAccount(@RequestBody UserRequest userRequest){
        return userServiceImpl.createAccount(userRequest);
    }


    @Operation(
        summary = "Consulta de saldo",
        description = "Verificar saldo  por número de cuenta"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Http status 200 SUCCESS"
    )
     @GetMapping("balanceEnquiry")//buscar por num cuenta una consulta de saldo
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userServiceImpl.balanceEnquirty(request);
    }

    @GetMapping("nameEnquiry") // find by numero cuenta el nombre del titular 
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userServiceImpl.nameEnquirty(request);
    }

    @PostMapping("login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userServiceImpl.login(loginDto);
    }

     @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userServiceImpl.creditAccount(request);
    }

    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userServiceImpl.debitAccount(request);
    }

    @PostMapping("transfer")
    public BankResponse transferAccount(@RequestBody TransferRequest request){
        return userServiceImpl.transfer(request);
    }
}
