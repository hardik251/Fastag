package com.example.vehicleowner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.sql.DataSource;
import java.sql.Connection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import io.jsonwebtoken.Jwts;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class VehicleOwnerService 
{

    private static final ObjectMapper objectMapper = new ObjectMapper();


        public static String generateJwtToken(Key secretKey,String vehicleNumber) {
        // Define the token expiration time (e.g., 1 hour from now)
        long expirationMillis = System.currentTimeMillis() + 1800000; // 30 mins in milliseconds

        // Build the JWT token with an expiration date
        return Jwts.builder()
                .setSubject(vehicleNumber) // You can set any subject or user identifier
                .setExpiration(new Date(expirationMillis))
                .signWith(secretKey)
                .compact();
    }


        public static String login(Key secretKey,boolean islogin[], String response, String vehicleNumber, String passEntered, String decryptedMailID[], DataSource dataSource, Connection connection[])
    {
        try
        {
            if (connection[0] == null || connection[0].isClosed()) 
            {
                connection[0] = dataSource.getConnection();
            }

            // String sqlQuery = "SELECT password FROM vehicle_details WHERE Vehicle_Number ='"+ vehicleNumber+"'";
            // PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            // ResultSet resultSet = preparedStatement.executeQuery();

            String sqlQuery = "SELECT password FROM vehicle_details WHERE Vehicle_Number = ?";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            preparedStatement.setString(1, vehicleNumber); // Set the parameter

            ResultSet resultSet = preparedStatement.executeQuery();

            String correctpass = null;
            if (resultSet.next()) 
            {
                correctpass = resultSet.getString("password");
            }

            // now we will convert the password entered by the user into encrypted form 
            // and then we will compare it with the password stored in the database
            byte[] encrypted = EncryptDecrypt.encrypt(passEntered);
            String encryptedPassByUserString = Base64.getEncoder().encodeToString(encrypted);

            // if the password entered by the user is correct then the program will continue
            if (encryptedPassByUserString.equals(correctpass) )
            {
                String sqlQuery_1 = "SELECT Account_balance,Category_id,mail_id FROM vehicle_details WHERE Vehicle_Number = ?";
                PreparedStatement preparedStatement_1 = connection[0].prepareStatement(sqlQuery_1);
                preparedStatement_1.setString(1, vehicleNumber); // Set the parameter

                ResultSet resultSet_1 = preparedStatement_1.executeQuery();

                String mailId = null;

                while (resultSet_1.next()) 
                {
                    int accountBalance = resultSet_1.getInt("Account_balance");
                    int categoryID = resultSet_1.getInt("Category_id");
                    mailId = resultSet_1.getString("mail_id");

                    decryptedMailID[0] = EncryptDecrypt.decrypt(Base64.getDecoder().decode(mailId));

                    String sqlQuery_4 = "SELECT CategoryName FROM category WHERE CategoryID = ?";
                    PreparedStatement preparedStatement_4 = connection[0].prepareStatement(sqlQuery_4);
                    preparedStatement_4.setInt(1, categoryID);
                    ResultSet resultSet_4 = preparedStatement_4.executeQuery();

                    while(resultSet_4.next())
                    {
                        String category = resultSet_4.getString("CategoryName");
                        System.out.println();

                        LoginResponse loginResponse = new LoginResponse();
                        // String jwtToken = generateJwtToken(secretKey,vehicleNumber);

                        loginResponse.setVehicleNumber(vehicleNumber);
                        loginResponse.setMailId(decryptedMailID[0]);
                        loginResponse.setVehicleType(category);
                        loginResponse.setAccountBalance(accountBalance);
                        loginResponse.setJwtToken(generateJwtToken(secretKey,vehicleNumber));

                        response = objectMapper.writeValueAsString(loginResponse);


                        // response = "{" +
                        // "\"Vehicle Number\":\"" + vehicleNumber + "\"," +
                        // "\"EMail ID\":\"" + decryptedMailID[0] + "\"," +
                        // "\"Vehicle Type\":\"" + category + "\"," +
                        // "\"Account Balance\":\"" + accountBalance + "\"" + // Remove the comma here
                        // "}";
                    
                    }

                }
                islogin[0]=true;
            }
            else
            {
        
                response = objectMapper.writeValueAsString(new MessageResponse("Invalid credentials"));
            }
        }
        catch(Exception e)
        {
            try {
                response = objectMapper.writeValueAsString(new MessageResponse("Server Error"));
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        }
        return response;
    }


        public static String forgoPassword(String response, String vehicleNumber, String enteredMailID, int otpRandom[], DataSource dataSource, Connection connection[])
    {
        try
        {
            if (connection[0] == null || connection[0].isClosed()) 
            {
                connection[0] = dataSource.getConnection();
            }

            otpRandom[0] = sendOTP(enteredMailID,vehicleNumber,connection,dataSource);


            if(otpRandom[0]==-1)
            {

                response = "Invalid Mail ID";
            }
            else if(otpRandom[0]==-2)
            {
                // forgoPasswordResponse.setMessage("Error");
                // forgoPasswordResponse.setJwtToken(null);
                response = "Error";
            }
            else
            {
                response = "OTP sent successfully! ";
                // forgoPasswordResponse.setMessage("OTP sent successfully!");
                // forgoPasswordResponse.setJwtToken(generateJwtToken(secretKey,vehicleNumber));
            }
        }
        catch(Exception e)
        {   
                // forgoPasswordResponse.setMessage("Error"+e.getMessage());
                // forgoPasswordResponse.setJwtToken(null);
                response = "Error"+e.getMessage();
        }
        return response;
    }

    

        public static int sendOTP(String enteredMailID,String vehicle_number,Connection connection[],DataSource dataSource)
    {
        try{
            if (connection[0] == null || connection[0].isClosed()) 
            {
                connection[0] = dataSource.getConnection();
            }
                byte[] encryptedEnteredMailID = EncryptDecrypt.encrypt(enteredMailID);
                String encryptedMailID = Base64.getEncoder().encodeToString(encryptedEnteredMailID);


                String sqlQuery = "SELECT mail_id FROM vehicle_details WHERE Vehicle_Number = ?";
                PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
                preparedStatement.setString(1, vehicle_number);
                ResultSet resultSet = preparedStatement.executeQuery();
                
                String mailId = null;
                if (resultSet.next()) 
                {
                    mailId = resultSet.getString("mail_id");
                }

                if(encryptedMailID.equals(mailId))
                {

                    
                    Random random = new Random();
                    
                    int otpRandom = random.nextInt(900000)+100000;

                    // now we will send the password to the user on his registered mail ID
                    String to = enteredMailID;
                    String from = "hardik23555@gmail.com";
                    String subject = "FASTMAN PASSWORD RECOVERY";
                    String text = null;

                    text = "Dear FastMAN User,\n\nVehicle Number:  "+vehicle_number+"\nYour OTP to change password is: "+otpRandom+"\nReport back if not requested by you\n\nRegards,\nFASTMAN";
                    System.out.println("Sending OTP to your registered Email ID");
                    try
                    {
                        // SendMail sm = new SendMail();
                        SendMail.sendEmail(to, from, subject, text);
                    }
                    catch(Exception e)
                    {
                        System.out.println("Error");
                        System.exit(-1);
                    }
                    return otpRandom;
                }
                else
                {
                    return -1;
                }
            }
            catch(Exception e)
            {
                return -2;
            }
    }


    

        public static String updatePass(boolean isloginForgot[], String newPass,String confirmNewPass,String vehicle_number,Connection connection[])
    {
        if(newPass.equals(confirmNewPass))
        {
            try
            {
                byte[] encrypted = EncryptDecrypt.encrypt(newPass);
                String newEncryptedPassByUserString = Base64.getEncoder().encodeToString(encrypted);

                String sqlQuery1 = "UPDATE vehicle_details SET password = ? WHERE Vehicle_Number = ?";
                PreparedStatement preparedStatement1 = connection[0].prepareStatement(sqlQuery1);
                preparedStatement1.setString(1, newEncryptedPassByUserString);
                preparedStatement1.setString(2, vehicle_number);
                preparedStatement1.execute();
                isloginForgot[0] = false;
                return "Password changed successfully!";
            }
            catch(Exception e)
            {
                return "Error";
            }
        }
        else
        {
            return "Passwords do not match";
        }
    }


    public static List<TransactionResponse> transaction (String vehicleNumber, Connection connection[])
    {
        String tollPlazaName = null;
        String state = null;
        List<TransactionResponse> responses = new ArrayList<>();
        try
        {

            String sqlQuery = "SELECT Transaction_Id,toll_plaza_id,Date_Time,Transaction_amount,Closing_Account_balance,Journey_Type FROM transactions WHERE vehicle_number = ?";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            preparedStatement.setString(1, vehicleNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) 
            {
                int transactionID = resultSet.getInt("Transaction_Id");
                int tollPlazaId = resultSet.getInt("toll_plaza_id");
                java.sql.Timestamp DateTime = resultSet.getTimestamp("Date_Time");
                // Date class is used to convert the timestamp into normal date time format
                Date date = new Date(DateTime.getTime());
                // SimpleDateFormat class is used to format the date
                SimpleDateFormat sdf = new SimpleDateFormat("EEE YYYY MMM dd HH:mm:ss  ");
                // formattedDate is used to store the date in the format specified in the constructor of SimpleDateFormat class
                // formattedDate is of type String
                String formattedDate = sdf.format(date);
                int transactionAmount = resultSet.getInt("Transaction_amount");
                int closingAccountBalance = resultSet.getInt("Closing_Account_balance");
                String journeyType = resultSet.getString("Journey_Type");
                    
                String sqlQuery_1 = "SELECT toll_plaza_name,state FROM toll_list WHERE id = ?";
                PreparedStatement preparedStatement_1 = connection[0].prepareStatement(sqlQuery_1);
                preparedStatement_1.setInt(1, tollPlazaId);
                ResultSet resultSet_1 = preparedStatement_1.executeQuery();

                while (resultSet_1.next())
                {
                    tollPlazaName = resultSet_1.getString("toll_plaza_name");
                    state = resultSet_1.getString("state");

                    TransactionResponse response = new TransactionResponse();
                    response.setTransactionId(transactionID);
                    response.setDateTime(formattedDate);
                    response.setTollPlaza(tollPlazaName);
                    response.setState(state);
                    response.setJourneyType(journeyType);
                    response.setCharges(transactionAmount);
                    response.setClosingAccountBalance(closingAccountBalance);

                    responses.add(response);
                }

            }

        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
        return responses;
    }




    public static ByteArrayOutputStream createPdf(List<TransactionResponse> transactions,String vehicleNumber) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,25);

        Paragraph titlePara = new Paragraph("Fastag Transaction History", font);
        titlePara.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titlePara);

        document.add(new Paragraph("\n\n\n"));
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA,12);
        Paragraph bodyPara = new Paragraph("Vehicle Number: "+vehicleNumber, bodyFont);
        document.add(bodyPara);

        document.add(new Paragraph("\n\n\n\n"));


        // Create a table
        PdfPTable table = new PdfPTable(7); // 7 columns for the transaction details
        table.setWidthPercentage(100);

        // Add table headers
        table.addCell("ID");
        table.addCell("Date Time");
        table.addCell("Toll Plaza");
        table.addCell("State");
        table.addCell("Journey Type");
        table.addCell("Charges");
        table.addCell("Closing Balance");

        // Insert rows into the table
        for (TransactionResponse transaction : transactions) {
            table.addCell(String.valueOf(transaction.getTransactionId()));
            table.addCell(transaction.getDateTime().toString());
            table.addCell(transaction.getTollPlaza());
            table.addCell(transaction.getState());
            table.addCell(transaction.getJourneyType());
            table.addCell(String.valueOf(transaction.getCharges()));
            table.addCell(String.valueOf(transaction.getClosingAccountBalance()));
        }

        // Add the table to the document
        document.add(table);

        document.close();

        return out;
    }

    public static String generateCsvContent(List<TransactionResponse> transactions) {
        StringBuilder csvContent = new StringBuilder();
    
        // Add CSV header row
        csvContent.append("Transaction ID,Date and Time,Toll Plaza,State,Journey Type,Charges,Closing Account Balance\n");
    
        // Insert rows into the CSV content
        for (TransactionResponse transaction : transactions) {
            csvContent.append(transaction.getTransactionId()).append(",");
            csvContent.append(transaction.getDateTime().toString()).append(",");
            csvContent.append(transaction.getTollPlaza()).append(",");
            csvContent.append(transaction.getState()).append(",");
            csvContent.append(transaction.getJourneyType()).append(",");
            csvContent.append(transaction.getCharges()).append(",");
            csvContent.append(transaction.getClosingAccountBalance()).append("\n");
        }
    
        return csvContent.toString();
    }

    public static byte[] generateExcelContent(List<TransactionResponse> transactions) {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Transactions");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Transaction ID", "Date and Time", "Toll Plaza", "State", "Journey Type", "Charges", "Closing Account Balance"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Insert rows into the Excel sheet
        int rowNum = 1;
        for (TransactionResponse transaction : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getTransactionId());
            row.createCell(1).setCellValue(transaction.getDateTime().toString());
            row.createCell(2).setCellValue(transaction.getTollPlaza());
            row.createCell(3).setCellValue(transaction.getState());
            row.createCell(4).setCellValue(transaction.getJourneyType());
            row.createCell(5).setCellValue(transaction.getCharges());
            row.createCell(6).setCellValue(transaction.getClosingAccountBalance());
        }

        // Convert the workbook to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
        e.printStackTrace();
        return new byte[0]; // Handle the error appropriately
    }
}


    public static String updateMail( int otpRandom[], String vehicleNumber, String decryptedMailID[])
    {
        Random random = new Random();                  
        otpRandom[1] = random.nextInt(900000)+100000;

        String to = decryptedMailID[0];
        String from = "hardik23555@gmail.com";
        String subject = "FASTMAN EMAIL ID UPDATE";
        String text = null;

        text = "Dear FastMAN User,\n\nVehicle Number:  "+vehicleNumber+"\nYour OTP to change your EmailID is: "+otpRandom[1]+"\nReport back if not requested by you\n\nRegards,\nFASTMAN";
        // System.out.println("Sending OTP to your registered Email ID");
        System.out.println("Hello2");
        // OTP on original mail ID is required to prevent unauthorized access to the account and to prevent misuse of the account if somehow someone gets access to the password
        // he should not be able to change the mail ID without OTP on original mail ID
        try
        {
            // SendMail sm = new SendMail();
            boolean res = SendMail.sendEmail(to, from, subject, text);
            System.out.println("Hello14");
            if(res==true)
            {
                System.out.println("Hello15");
                return "OTP sent successfully! ";
            }
            else
            {
                System.out.println("Hello16");
                return "Error";
            }
        }
        catch(Exception e)
        {
            return "Error"+e.getMessage();
        }
        
    }

    public static String updateNewMail(String response, String vehicleNumber, String newMail, int otpRandom[])
    {
        Random random = new Random();
        otpRandom[2] = random.nextInt(900000)+100000;

        String to = newMail;
        String from = "hardik23555@gmail.com";
        String subject = "FASTMAN EMAIL ID UPDATE";
        String text = null;

        text = "Dear FastMAN User,\n\nVehicle Number:  "+vehicleNumber+"\nVerify your email to set as EmailID for this vehicle Number\nUse the following verification code: "+otpRandom[2]+"\n"+" You can safely ignore if not requested by you. Someone else might have entered you mail ID by mistake \n\nRegards,\nFASTMAN";
        System.out.println("Sending OTP to new Email ID");
        try
        {
            // SendMail sm = new SendMail();
            boolean res = SendMail.sendEmail(to, from, subject, text);
            if(res==true)
            {
                response = "OTP sent successfully! ";
            }
            else
            {
                response = "Error";
            }
        }
        catch(Exception e)
        {
        response = "Error"+e.getMessage();
        }
        return response;
    }

    public static String updateNewOTP(boolean islogin[],String response,String vehicleNumber,String newMail,int otpEntered, int otpRandom[], Connection connection[])
    {
        if(otpEntered==otpRandom[2])
        {
            try
            {
                byte[] encryptedNewMailID = EncryptDecrypt.encrypt(newMail);
                String newEncryptedMailID = Base64.getEncoder().encodeToString(encryptedNewMailID);

                String sqlQuery1 = "UPDATE vehicle_details SET mail_id = ? WHERE Vehicle_Number = ?";
                PreparedStatement preparedStatement1 = connection[0].prepareStatement(sqlQuery1);
                preparedStatement1.setString(1, newEncryptedMailID);
                preparedStatement1.setString(2, vehicleNumber);
                preparedStatement1.execute();
                response = ("Email ID changed successfully!");
            }
            catch(Exception e)
            {
                response = ("Error"+e.getMessage());
            }
        }
        else
        {
            response = ("Wrong OTP");
            islogin[0] = false;
        }
        return response;
    }

}



