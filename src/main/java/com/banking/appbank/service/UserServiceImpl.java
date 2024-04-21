package com.banking.appbank.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banking.appbank.config.JwtTokenProvider;
import com.banking.appbank.dto.AccountInfo;
import com.banking.appbank.dto.BankResponse;
import com.banking.appbank.dto.CreditDebitRequest;
import com.banking.appbank.dto.EmailDetails;
import com.banking.appbank.dto.EnquiryRequest;
import com.banking.appbank.dto.LoginDto;
import com.banking.appbank.dto.TransactionDto;
import com.banking.appbank.dto.TransferRequest;
import com.banking.appbank.dto.UserRequest;
import com.banking.appbank.entity.Role;
import com.banking.appbank.entity.User;
import com.banking.appbank.repository.UserRepository;
import com.banking.appbank.utils.AccountUtils;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

     @Autowired
     JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
      //crear una account es guardar un nuevo user en la BD

      if(userRepository.existsByEmail(userRequest.getEmail())){
        BankResponse response = BankResponse.builder()
                   .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                   .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                   .accountInfo(null)
                   .build();
                   return response; 
      }

      User newUser = User.builder()
            .firstName(userRequest.getFirstName())
            .lastName(userRequest.getLastName())
            .otherName(userRequest.getOtherName())
            .gender(userRequest.getGender())
            .address(userRequest.getAddress())
            .stateOfOrigin(userRequest.getStateOfOrigin())
            .accountNumber(AccountUtils.generateAccountNumber())
            .accountBalance(BigDecimal.ZERO)
            .email(userRequest.getEmail())
            .password(passwordEncoder.encode(userRequest.getPassword()))//2-Inyecto PasswordEncoder passwordEncoder. 3-encripto el pass
            .phoneNumber(userRequest.getPhoneNumber())
            .alternativePhone(userRequest.getAlternativePhone())
            .status("ACTIVE")
            .role(Role.ROLE_ADMIN)
            .build();

       User savedUser= userRepository.save(newUser);
       //send email con alerta
       EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("CUENTA CREADA EN BANCO CUYANO")
                .messageBody("FELICITACIONES! Cuenta creada exitosamente.\nDetalles de su cuenta: \n"+
                             "Nombre de cuenta: " + savedUser.getFirstName()+" "+ savedUser.getLastName()+" "+ "\nNúmero de Cuenta: "+ savedUser.getAccountNumber())
                .build();
       emailService.sendEmailAlert(emailDetails);
       return BankResponse.builder() 
            .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
            .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
            .accountInfo(AccountInfo.builder()
                    .accountBalance(savedUser.getAccountBalance())
                    .accountNumber(savedUser.getAccountNumber())
                    .accountName(savedUser.getFirstName()+" "+ savedUser.getLastName()+" "+ savedUser.getOtherName())
                    .build())
            .build();
    }


public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("HAS INICIADO SESIÓN")
                .recipient(loginDto.getEmail())
                .messageBody("Se ha iniciado sesión en tu cuenta bancaria. Avisanos sino has sido tú.")
                .build();
        emailService.sendEmailAlert(loginAlert);

        return BankResponse.builder()
                .responseCode("Loggin Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
}


@Override  //consulta de saldo
public BankResponse balanceEnquirty(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }



@Override //paso en numero de cuenta y me trae el nombre del titular
public String nameEnquirty(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

@Override
public BankResponse creditAccount(CreditDebitRequest request) {
         //checking if the account exists
         boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
         if (!isAccountExist){
             return BankResponse.builder()
                     .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                     .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                     .accountInfo(null)
                     .build();
         }
 
         User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
         userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
         userRepository.save(userToCredit);

         //save transaction
         TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CRÉDITO")
                .amount(request.getAmount())
                .build();
         transactionService.saveTransaction(transactionDto);       
         //fin save transaction
 
         return BankResponse.builder()
                 .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                 .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                 .accountInfo(AccountInfo.builder()
                         .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                         .accountBalance(userToCredit.getAccountBalance())
                         .accountNumber(request.getAccountNumber())
                         .build())
                 .build();
     }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        //check if the amount you intend to withdraw is not more than the current account balance
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance =userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if ( availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
             //save transaction
            TransactionDto transactionDto = TransactionDto.builder()
                   .accountNumber(userToDebit.getAccountNumber())
                   .transactionType("DÉBITO")
                   .amount(request.getAmount())
                   .build();
            transactionService.saveTransaction(transactionDto);       
         //fin save transaction
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }

    }

@Override
public BankResponse transfer(TransferRequest request) {
      
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getNumeroCuentaDestino());
        
        if(!isDestinationAccountExists){
                  return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(request.getNumeroCuentaOrigen());
        if(request.getCantidad().compareTo(sourceAccountUser.getAccountBalance()) > 0){
         return BankResponse.builder()
                 .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                 .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                 .accountInfo(null)
                 .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getCantidad()));
        String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName();
        userRepository.save(sourceAccountUser);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("ALERTA DE DÉBITO")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("La suma del importe "+ request.getCantidad()+ " ha sido deducida de su cuenta.\nSu saldo actual es "+ sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);
        
        User destinationAccountUser = userRepository.findByAccountNumber(request.getNumeroCuentaDestino());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getCantidad()));
       // String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName();
        userRepository.save(destinationAccountUser);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("ALERTA DE CRÉDITO")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("La suma del importe "+ request.getCantidad()+ " ha sido enviada desde la cuenta a nombre "+ sourceUsername+
                             "\nSu saldo actual es "+ destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);
         //save transaction
         TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CRÉDITO")
                .amount(request.getCantidad())
                .build();
         transactionService.saveTransaction(transactionDto);       
         //fin save transaction

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
}        

    
}
