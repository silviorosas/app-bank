package com.banking.appbank.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.banking.appbank.dto.EmailDetails;
import com.banking.appbank.entity.Transaction;
import com.banking.appbank.entity.User;
import com.banking.appbank.repository.TransactionRepository;
import com.banking.appbank.repository.UserRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
   
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    private static final String FILE ="c:\\Users\\pc\\Documents\\MyStatement.pdf";


    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException{
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
         List<Transaction> trList= transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber)
                                    ).filter(transaction -> transaction.getCreatedAt().isEqual(start)).filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();
        
        //Generar PDF
        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName()+ " "+ user.getLastName();

        Rectangle statementSize = new Rectangle(PageSize.A3);
        Document document = new Document(statementSize);
        log.info("Setting size of document");
        FileOutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfotable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Banco Cuyano", new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.WHITE)));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);       
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("Mitre 102 - San Rafael - Mendoza - Argentina"));
        bankAddress.setBorder(0);
        bankInfotable.addCell(bankName);
        bankInfotable.addCell(bankAddress);
        bankInfotable.addCell(new Phrase());

        
        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Estado de Cuenta de: " + customerName));
        customerInfo.setBorder(0);        
   
        PdfPCell espacio = new PdfPCell();//espacio en blanco
        espacio.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Dirección del Cliente: " + user.getAddress()));
        address.setBorder(0);

        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("Fecha", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        
        PdfPCell transactionType = new PdfPCell(new Phrase("Tipo de Transacción", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("Cantidad de Transacción", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);

        PdfPCell status = new PdfPCell(new Phrase("Status", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);     

       

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);
        


        trList.forEach(transaction ->{
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));

        });

        statementInfo.addCell(customerInfo);       
        statementInfo.addCell(address);

     

        document.add(bankInfotable);
        // Agregar espacio en blanco horizontal entre las tablas
        document.add(new Paragraph("\n", new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL, BaseColor.WHITE)));

        document.add(statementInfo);
        document.add(transactionTable);

        PdfPTable saldoActualTable = new PdfPTable(1);

       // Añadir encabezado "Saldo Actual"
       PdfPCell saldoActualHeaderCell = new PdfPCell(new Phrase("Saldo Actual: "+ user.getAccountBalance(), new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
       saldoActualHeaderCell.setBackgroundColor(BaseColor.BLUE);
       saldoActualHeaderCell.setBorder(0);
       saldoActualTable.addCell(saldoActualHeaderCell);


       // Agregar la nueva tabla al final del documento
       document.add(saldoActualTable);

        document.close();
    
        EmailDetails emailDetails = EmailDetails.builder()
                
                .recipient(user.getEmail())
                .subject("ESTADO DE CUENTA")
                .messageBody("Encuentre su solicitud de estado de cuenta en el archivo adjunto.")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);
        

        return trList;
    }

   
}
