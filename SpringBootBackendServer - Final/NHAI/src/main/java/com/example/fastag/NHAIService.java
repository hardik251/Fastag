package com.example.fastag;

import java.util.Random;
import javax.sql.DataSource;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import io.jsonwebtoken.Jwts;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.sql.Statement;
import org.apache.poi.ss.usermodel.*;
import java.security.Key;


public class NHAIService
{

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static String tollInp(int tollID,String state[],String tollPlazaName[],Connection connection[])
    {
        String response = null;
        try
        {
            String sqlQuery = "SELECT state, toll_plaza_name FROM toll_list WHERE id = ?";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            preparedStatement.setInt(1, tollID); // Set the parameter

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                state[0] = resultSet.getString("state");
                tollPlazaName[0] = resultSet.getString("toll_plaza_name");

                // Create a TollInfo object
                TollInfo tollInfo = new TollInfo();

                tollInfo.setState(state[0]);
                tollInfo.setTollPlazaName(tollPlazaName[0]);

                // Serialize the object to JSON using Jackson
                // ObjectMapper objectMapper = new ObjectMapper();
                response = objectMapper.writeValueAsString(tollInfo);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        try {
            response = objectMapper.writeValueAsString(new MessageResponse("Server error"));
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        }
        return response;
    }

    // public static List<TransactionResponseT> transactionT (Connection connection[],int tollID)
    // {
    //     List<TransactionResponseT> responses = new ArrayList<>(); 
    //     try
    //     {
    //         String sqlQuery_10 = "SELECT Transaction_Id,vehicle_number,Date_Time,Transaction_amount,Journey_Type FROM transactions WHERE toll_plaza_id ="+tollID;
    //         PreparedStatement preparedStatement_10 = connection[0].prepareStatement(sqlQuery_10);
    //         ResultSet resultSet_10 = preparedStatement_10.executeQuery();
        
    //         while (resultSet_10.next()) 
    //         {             
    //             int transactionID = resultSet_10.getInt("Transaction_Id");
    //             // this DateTime variable is used to store the date and time of the transaction in the timestamp format
    //             java.sql.Timestamp DateTime = resultSet_10.getTimestamp("Date_Time");
    //             String vehicleNumber = resultSet_10.getString("vehicle_number");
    //             int transactionAmount = resultSet_10.getInt("Transaction_amount");
    //             String journeyType = resultSet_10.getString("Journey_Type");
            
    //             String sqlQuery_101 = "SELECT Category_id FROM vehicle_details WHERE Vehicle_Number ='"+vehicleNumber+"'";
    //             PreparedStatement preparedStatement_101 = connection[0].prepareStatement(sqlQuery_101);
    //             ResultSet resultSet_101 = preparedStatement_101.executeQuery();

    //             String categoryName_102 = null;

    //             while (resultSet_101.next())
    //             {
    //                 int categoryID_101 = resultSet_101.getInt("Category_id");
    //                 String sqlQuery_102 = "SELECT CategoryName FROM category WHERE CategoryID ="+ categoryID_101;
    //                 PreparedStatement preparedStatement_102 = connection[0].prepareStatement(sqlQuery_102);
    //                 ResultSet resultSet_102 = preparedStatement_102.executeQuery();

    //                 while (resultSet_102.next()) 
    //                 {
    //                     categoryName_102 = resultSet_102.getString("CategoryName");  
                        
    //                     TransactionResponseT response = new TransactionResponseT();
    //                     response.setTransactionId(transactionID);
    //                     response.setDateTime(DateTime);
    //                     response.setVehicleNumber(vehicleNumber);
    //                     response.setVehicleType(categoryName_102);
    //                     response.setCharges(transactionAmount);
    //                     response.setJourneyType(journeyType);
            
    //                     responses.add(response);
    //                 }
    //             }                     
    //         }
    //     }
    //     catch(Exception e)
    //     {
    //         e.printStackTrace();
    //     }

    //     return responses;
    // }

    public static List<TransactionResponseT> transactionT(Connection connection[], int tollID) {
        List<TransactionResponseT> responses = new ArrayList<>();
        try {
            String sqlQuery = "SELECT t.Transaction_Id, t.Date_Time, t.vehicle_number, t.Transaction_amount, t.Journey_Type, vd.Category_id, c.CategoryName " +
                              "FROM transactions t " +
                              "INNER JOIN vehicle_details vd ON t.vehicle_number = vd.Vehicle_Number " +
                              "INNER JOIN category c ON vd.Category_id = c.CategoryID " +
                              "WHERE t.toll_plaza_id = ? "+
                              "ORDER BY t.Transaction_Id ASC";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            preparedStatement.setInt(1, tollID);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                int transactionID = resultSet.getInt("Transaction_Id");
                java.sql.Timestamp DateTime = resultSet.getTimestamp("Date_Time");
                String vehicleNumber = resultSet.getString("vehicle_number");
                int transactionAmount = resultSet.getInt("Transaction_amount");
                String journeyType = resultSet.getString("Journey_Type");
                // int categoryID = resultSet.getInt("Category_id");
                String categoryName = resultSet.getString("CategoryName");
    
                TransactionResponseT response = new TransactionResponseT();
                response.setTransactionId(transactionID);
                response.setDateTime(DateTime);
                response.setVehicleNumber(vehicleNumber);
                response.setVehicleType(categoryName);
                response.setCharges(transactionAmount);
                response.setJourneyType(journeyType);
    
                responses.add(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return responses;
    }
    
    
    public static ByteArrayOutputStream createPdfT(List<TransactionResponseT> transactions,int tollID,String tollPlazaName[],String state[]) throws DocumentException {
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
        Paragraph bodyPara = new Paragraph("Toll ID: "+tollID, bodyFont);
        document.add(bodyPara);
        Paragraph bodyPara2 = new Paragraph("Toll Plaza Name: "+tollPlazaName[0], bodyFont);
        document.add(bodyPara2);
        Paragraph bodyPara3 = new Paragraph("State: "+state[0], bodyFont);
        document.add(bodyPara3);

        document.add(new Paragraph("\n\n\n\n"));


        // Create a table
        PdfPTable table = new PdfPTable(6); // 6 columns for the transaction details
        table.setWidthPercentage(100);

        // Add table headers
        table.addCell("Transaction ID");
        table.addCell("Date and Time");
        table.addCell("Vehicle Number");
        table.addCell("Vehicle Type");
        table.addCell("Charges");
        table.addCell("Journey Type");

        // Insert rows into the table
        for (TransactionResponseT transaction : transactions) {
            table.addCell(String.valueOf(transaction.getTransactionId()));
            table.addCell(transaction.getDateTime().toString());
            table.addCell(transaction.getVehicleNumber());
            table.addCell(transaction.getVehicleType());
            table.addCell(String.valueOf(transaction.getCharges()));
            table.addCell(transaction.getJourneyType());
        }

        // Add the table to the document
        document.add(table);

        document.close();

        return out;
    }


    public static String generateCsvContentT(List<TransactionResponseT> transactions) {
        StringBuilder csvContent = new StringBuilder();
    
        // Add CSV header row
        csvContent.append("Transaction ID,Date and Time,Vehicle Number,Vehicle Type,Charges,Journey Type\n");
    
        // Insert rows into the CSV content
        for (TransactionResponseT transaction : transactions) {
            csvContent.append(transaction.getTransactionId()).append(",");
            csvContent.append(transaction.getDateTime().toString()).append(",");
            csvContent.append(transaction.getVehicleNumber()).append(",");
            csvContent.append(transaction.getVehicleType()).append(",");
            csvContent.append(transaction.getCharges()).append(",");
            csvContent.append(transaction.getJourneyType()).append("\n");
        }
    
        return csvContent.toString();
    }



    
public static byte[] generateExcelContentT(List<TransactionResponseT> transactions) {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Transactions");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Transaction ID", "Date and Time", "Vehicle Number", "Vehicle Type", "Charges", "Journey Type"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Insert rows into the Excel sheet
        int rowNum = 1;
        for (TransactionResponseT transaction : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getTransactionId());
            row.createCell(1).setCellValue(transaction.getDateTime().toString());
            row.createCell(2).setCellValue(transaction.getVehicleNumber());
            row.createCell(3).setCellValue(transaction.getVehicleType());
            row.createCell(4).setCellValue(transaction.getCharges());
            row.createCell(5).setCellValue(transaction.getJourneyType());
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

   public static int deleteTollOTP(Connection connection[],String nhaiMail,int tollID)
    {

        int otpDelete = sendOTPDelete(nhaiMail,connection,tollID);

        return otpDelete;
    }

    public static int sendOTPDelete(String enteredMailID,Connection connection[],int tollID)
    {
        try
        {
            Random random = new Random();
            int otpRandom = random.nextInt(900000)+100000;

            String to = enteredMailID;
            String from = "hardik23555@gmail.com";
            String subject = "FASTMAN TOLL PLAZA DELETE";
            String text = null;

            text = "This action is irreversible. Enter OTP only if you are sure to delete the toll.\n Your OTP to delete  Toll ID: "+tollID + " is: "+otpRandom+"\n\nRegards,\nFASTMAN";
            System.out.println("Sending OTP to your registered Email ID");
            try
            {
                SendMail sm = new SendMail();
                sm.sendEmail_(to, from, subject, text);
            }
            catch(Exception e)
            {
                System.out.println("Error");
                // System.exit(-1);
            }
            return otpRandom;
        }
        catch(Exception e)
        {
            return -1;
        }
    }

    public static String deleteToll(int tollID,Connection connection[])
    {
        String response = null;
        try
        {
            String sqlQuery = "DELETE FROM toll_list WHERE id = ?";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            preparedStatement.setInt(1, tollID); // Set the parameter

            preparedStatement.executeUpdate();
            response = "Toll deleted successfully";
        }
        catch(Exception e)
        {
            response = "Error"+e.getMessage();
        }
        return response;
    }


       //   vehicle transactions

    // public static String vehicleLogin(String vehicleNumber,Connection connection[])
    // {
    //     String response = null;
    //     String decryptedMailID[] = new String[1];
    //     try
    //     {
    //             String sqlQuery_1 = "SELECT Account_balance,Category_id,mail_id FROM vehicle_details WHERE Vehicle_Number ='"+vehicleNumber+"'";
    //             PreparedStatement preparedStatement_1 = connection[0].prepareStatement(sqlQuery_1);
    //             ResultSet resultSet_1 = preparedStatement_1.executeQuery();

    //             String mailId = null;

    //             while (resultSet_1.next()) 
    //             {
    //                 int accountBalance = resultSet_1.getInt("Account_balance");
    //                 int categoryID = resultSet_1.getInt("Category_id");
    //                 mailId = resultSet_1.getString("mail_id");

    //                 decryptedMailID[0] = EncryptDecrypt.decrypt(Base64.getDecoder().decode(mailId));

    //                 String sqlQuery_4 = "SELECT CategoryName FROM category WHERE CategoryID ="+categoryID;
    //                 PreparedStatement preparedStatement_4 = connection[0].prepareStatement(sqlQuery_4);
    //                 ResultSet resultSet_4 = preparedStatement_4.executeQuery();

    //                 while(resultSet_4.next())
    //                 {
    //                     String category = resultSet_4.getString("CategoryName");
    //                     System.out.println();
    //                     response = "{" +
    //                     "\"Vehicle Number\":\"" + vehicleNumber + "\"," +
    //                     "\"EMail ID\":\"" + decryptedMailID[0] + "\"," +
    //                     "\"Vehicle Type\":\"" + category + "\"," +
    //                     "\"Account Balance\":\"" + accountBalance + "\"" + // Remove the comma here
    //                     "}";
                        
    //                 }

    //             }
    //     }
    //     catch(Exception e)
    //     {

    //         try {
    //             response = objectMapper.writeValueAsString(new MessageResponse("Server Error"));
    //         } catch (JsonProcessingException e1) {
    //             e1.printStackTrace();
    //         }
    //     }
    //     return response;
    // }


    public static String vehicleLogin(String vehicleNumber, Connection connection[]) {
        String response = null;
        try {
            String sqlQuery = "SELECT vd.Account_balance, vd.Category_id, vd.mail_id, c.CategoryName " +
                              "FROM vehicle_details vd " +
                              "INNER JOIN category c ON vd.Category_id = c.CategoryID " +
                              "WHERE vd.Vehicle_Number = ?";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            preparedStatement.setString(1, vehicleNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            String mailId = null;
    
            while (resultSet.next()) {
                int accountBalance = resultSet.getInt("Account_balance");
                // int categoryID = resultSet.getInt("Category_id");
                mailId = resultSet.getString("mail_id");
                String categoryName = resultSet.getString("CategoryName");
    
                String decryptedMailID = EncryptDecrypt.decrypt(Base64.getDecoder().decode(mailId));
    

                VehicleLoginResponse vehicleLoginResponse = new VehicleLoginResponse();

                vehicleLoginResponse.setVehicleNumber(vehicleNumber);
                vehicleLoginResponse.setEmailId(decryptedMailID);
                vehicleLoginResponse.setVehicleType(categoryName);
                vehicleLoginResponse.setAccountBalance(accountBalance);

                response = objectMapper.writeValueAsString(vehicleLoginResponse);

                // response = "{" +
                //     "\"Vehicle Number\":\"" + vehicleNumber + "\"," +
                //     "\"EMail ID\":\"" + decryptedMailID + "\"," +
                //     "\"Vehicle Type\":\"" + categoryName + "\"," +
                //     "\"Account Balance\":\"" + accountBalance + "\"" + // Remove the comma here
                //     "}";
            }
        } catch (Exception e) {
            try {
                response = objectMapper.writeValueAsString(new MessageResponse("Server Error"));
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        }
        return response;
    }
    

    // public static List<TransactionResponseV> transactionV (String vehicleNumber, Connection connection[])
    // {
    //     String tollPlazaName = null;
    //     String state = null;
    //     List<TransactionResponseV> responses = new ArrayList<>();
    //     try
    //     {

    //         String sqlQuery = "SELECT Transaction_Id,toll_plaza_id,Date_Time,Transaction_amount,Closing_Account_balance,Journey_Type FROM transactions WHERE vehicle_number ='"+vehicleNumber+"'";
    //         PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
    //         ResultSet resultSet = preparedStatement.executeQuery();

    //         while (resultSet.next()) 
    //         {
    //             int transactionID = resultSet.getInt("Transaction_Id");
    //             int tollPlazaId = resultSet.getInt("toll_plaza_id");
    //             java.sql.Timestamp DateTime = resultSet.getTimestamp("Date_Time");
    //             // Date class is used to convert the timestamp into normal date time format
    //             Date date = new Date(DateTime.getTime());
    //             // SimpleDateFormat class is used to format the date
    //             SimpleDateFormat sdf = new SimpleDateFormat("EEE YYYY MMM dd HH:mm:ss  ");
    //             // formattedDate is used to store the date in the format specified in the constructor of SimpleDateFormat class
    //             // formattedDate is of type String
    //             String formattedDate = sdf.format(date);
    //             int transactionAmount = resultSet.getInt("Transaction_amount");
    //             int closingAccountBalance = resultSet.getInt("Closing_Account_balance");
    //             String journeyType = resultSet.getString("Journey_Type");
                    
    //             String sqlQuery_1 = "SELECT toll_plaza_name,state FROM toll_list WHERE id ="+tollPlazaId;
    //             PreparedStatement preparedStatement_1 = connection[0].prepareStatement(sqlQuery_1);
    //             ResultSet resultSet_1 = preparedStatement_1.executeQuery();

    //             while (resultSet_1.next())
    //             {
    //                 tollPlazaName = resultSet_1.getString("toll_plaza_name");
    //                 state = resultSet_1.getString("state");

    //                 TransactionResponseV response = new TransactionResponseV();
    //                 response.setTransactionId(transactionID);
    //                 response.setDateTime(formattedDate);
    //                 response.setTollPlaza(tollPlazaName);
    //                 response.setState(state);
    //                 response.setJourneyType(journeyType);
    //                 response.setCharges(transactionAmount);
    //                 response.setClosingAccountBalance(closingAccountBalance);

    //                 responses.add(response);
    //             }

    //         }

    //     }
    //     catch(Exception e)
    //     {
    //         e.printStackTrace();
    //         // response = "Error"+e.getMessage();
    //     }
    //     return responses;
    // }

    public static List<TransactionResponseV> transactionV(String vehicleNumber, Connection connection[]) {
        List<TransactionResponseV> responses = new ArrayList<>();
        try {
            String sqlQuery = "SELECT t.Transaction_Id, t.toll_plaza_id, t.Date_Time, t.Transaction_amount, t.Closing_Account_balance, t.Journey_Type, tl.toll_plaza_name, tl.state " +
                              "FROM transactions t " +
                              "INNER JOIN toll_list tl ON t.toll_plaza_id = tl.id " +
                              "WHERE t.vehicle_number = ? "+
                              "ORDER BY t.Transaction_Id ASC";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            preparedStatement.setString(1, vehicleNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                int transactionID = resultSet.getInt("Transaction_Id");
                java.sql.Timestamp DateTime = resultSet.getTimestamp("Date_Time");
                int transactionAmount = resultSet.getInt("Transaction_amount");
                int closingAccountBalance = resultSet.getInt("Closing_Account_balance");
                String journeyType = resultSet.getString("Journey_Type");
                String tollPlazaName = resultSet.getString("toll_plaza_name");
                String state = resultSet.getString("state");
    
                // Format the timestamp
                SimpleDateFormat sdf = new SimpleDateFormat("EEE YYYY MMM dd HH:mm:ss");
                String formattedDate = sdf.format(DateTime);
    
                TransactionResponseV response = new TransactionResponseV();
                response.setTransactionId(transactionID);
                response.setDateTime(formattedDate);
                response.setTollPlaza(tollPlazaName);
                response.setState(state);
                response.setJourneyType(journeyType);
                response.setCharges(transactionAmount);
                response.setClosingAccountBalance(closingAccountBalance);
    
                responses.add(response);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responses;
    }
    

    public static ByteArrayOutputStream createPdfV(List<TransactionResponseV> transactions,String vehicleNumber) throws DocumentException {
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
        for (TransactionResponseV transaction : transactions) {
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

    public static String generateCsvContentV(List<TransactionResponseV> transactions) {
        StringBuilder csvContent = new StringBuilder();
    
        // Add CSV header row
        csvContent.append("Transaction ID,Date and Time,Toll Plaza,State,Journey Type,Charges,Closing Account Balance\n");
    
        // Insert rows into the CSV content
        for (TransactionResponseV transaction : transactions) {
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

    public static byte[] generateExcelContentV(List<TransactionResponseV> transactions) {
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
        for (TransactionResponseV transaction : transactions) {
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


   public static int sendOTP(String enteredMailID,Connection connection[])
    {
        try
        {
            Random random = new Random();
            int otpRandom = random.nextInt(900000)+100000;

            String to = enteredMailID;
            String from = "hardik23555@gmail.com";
            String subject = "FASTMAN NEW TOLL PLAZA REGISTRATION";
            String text = null;

            text = "Your OTP to set  "+enteredMailID + " for new toll is: "+otpRandom+"\n\nRegards,\nFASTMAN";
            System.out.println("Sending OTP to your registered Email ID");
            try
            {
                SendMail sm = new SendMail();
                sm.sendEmail_(to, from, subject, text);
            }
            catch(Exception e)
            {
                System.out.println("Error");
                // System.exit(-1);
            }
            return otpRandom;
        }
        catch(Exception e)
        {
            return -1;
        }
    }


    public static AddTollResponse addToll(String state[],String tollPlazaName[],String password,String email,int single[],int round[],Connection connection[])
    {
        String encryptedPass = null;
        String encryptedMail = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        AddTollResponse addTollResponse = null;


        try {
            byte[] encrypted = EncryptDecrypt.encrypt(password);
            encryptedPass = Base64.getEncoder().encodeToString(encrypted);

            byte[] encryptedM = EncryptDecrypt.encrypt(email);
            encryptedMail = Base64.getEncoder().encodeToString(encryptedM);            

        } catch (Exception e) {
            e.printStackTrace();
        }


        String sql = "INSERT INTO toll_list (state, toll_plaza_name, password, mailID) VALUES (?, ?, ?, ?)";

        try
        {
            preparedStatement = connection[0].prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, state[0]);
            preparedStatement.setString(2, tollPlazaName[0]);
            preparedStatement.setString(3, encryptedPass);
            preparedStatement.setString(4, encryptedMail);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) 
            {
                // Retrieve the generated keys (in this case, only the auto-incremented ID)
                generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next()) 
                {
                    int generatedId = generatedKeys.getInt(1);
                    addTollResponse = new AddTollResponse(generatedId,"Success");
                    // response  = "Data inserted successfully with ID: " + generatedId;

        
                    for(int i=0;i<7;i++){
                    String sql2 = "INSERT INTO tariff (TollID, CategoryID, Single, Round) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement1 = connection[0].prepareStatement(sql2);

                    preparedStatement1.setInt(1, generatedId);
                    preparedStatement1.setInt(2, i+1);
                    preparedStatement1.setInt(3, single[i]);
                    preparedStatement1.setInt(4, round[i]);
                    preparedStatement1.execute();
                    }
                }
                else 
                {
                    addTollResponse = new AddTollResponse(-1,"Fail");
                    // response = "Data insertion failed!";
                }
            }   
        }
        catch(Exception e)
        {
            // response = "Error"+e.getMessage();
            addTollResponse = new AddTollResponse(-1,"Error"+e.getMessage());
        }

        return addTollResponse;
    }


//    public static List<tollChargesResponse> tollCharges (Connection connection[],int tollID)
//     {
//         List<tollChargesResponse> responses = new ArrayList<>(); 
//         try
//         {
//             for(int i=1;i<=7;i++)
//             {
//                 String sqlQuery_10 = "SELECT TollID, CategoryID, Single, Round FROM tariff WHERE TollID = ? AND CategoryID = ?";

//                 PreparedStatement preparedStatement_10 = connection[0].prepareStatement(sqlQuery_10);
//                 preparedStatement_10.setInt(1, tollID);
//                 preparedStatement_10.setInt(2, i);
//                 ResultSet resultSet_10 = preparedStatement_10.executeQuery();
            
//                 while (resultSet_10.next()) 
//                 {             
//                     int resultTollID = resultSet_10.getInt("TollID");
//                     int resultCategoryID = resultSet_10.getInt("CategoryID");
//                     int resultSingle = resultSet_10.getInt("Single");
//                     int resultRound = resultSet_10.getInt("Round");
                
//                         String sqlQuery_102 = "SELECT CategoryName FROM category WHERE CategoryID =?";
//                         PreparedStatement preparedStatement_102 = connection[0].prepareStatement(sqlQuery_102);
//                         preparedStatement_102.setInt(1, resultCategoryID);

//                         ResultSet resultSet_102 = preparedStatement_102.executeQuery();

//                         while (resultSet_102.next()) 
//                         {
//                             String categoryName_102 = resultSet_102.getString("CategoryName");  
                            
//                             tollChargesResponse response = new tollChargesResponse();
//                             response.setTollID(resultTollID);
//                             response.setCategory(categoryName_102);
//                             response.setSingle(resultSingle);
//                             response.setRound(resultRound);
                
//                             responses.add(response);
//                         }
                                        
//                 }
//             }
//         }
//         catch(Exception e)
//         {
//             e.printStackTrace();
//         }

//         return responses;
//     }

public static List<tollChargesResponse> tollCharges(Connection connection[], int tollID) {
    List<tollChargesResponse> responses = new ArrayList<>();
    try {
        String sqlQuery = "SELECT t.TollID, t.CategoryID, t.Single, t.Round, c.CategoryName " +
                          "FROM tariff t " +
                          "INNER JOIN category c ON t.CategoryID = c.CategoryID " +
                          "WHERE t.TollID = ?";
        PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
        preparedStatement.setInt(1, tollID);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int resultTollID = resultSet.getInt("TollID");
            // int resultCategoryID = resultSet.getInt("CategoryID");
            int resultSingle = resultSet.getInt("Single");
            int resultRound = resultSet.getInt("Round");
            String categoryName_102 = resultSet.getString("CategoryName");

            tollChargesResponse response = new tollChargesResponse();
            response.setTollID(resultTollID);
            response.setCategory(categoryName_102);
            response.setSingle(resultSingle);
            response.setRound(resultRound);

            responses.add(response);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return responses;
}


    public static String updateToll(int tollId,String state,String tollPlazaName,int single[],int round[],Connection connection[])
    {
        String response = null;

        try
        { 
            String sql = "UPDATE toll_list SET state = ?, toll_plaza_name = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sql);

            preparedStatement.setString(1, state);
            preparedStatement.setString(2, tollPlazaName);
            preparedStatement.setInt(3, tollId);

            preparedStatement.executeUpdate(); // Use executeUpdate for UPDATE statements

            for (int i = 0; i < 7; i++) {
                String sql2 = "UPDATE tariff SET Single = ?, Round = ? WHERE TollID = ? AND CategoryID = ?";
                PreparedStatement preparedStatement1 = connection[0].prepareStatement(sql2);
            
                preparedStatement1.setInt(1, single[i]);
                preparedStatement1.setInt(2, round[i]);
                preparedStatement1.setInt(3, tollId);
                preparedStatement1.setInt(4, i + 1);
            
                preparedStatement1.executeUpdate(); 
            }
            response = "Success";
        }
        catch(Exception e)
        {
            response = "Error"+e.getMessage();
        }

        return response;
    }


    public static ByteArrayOutputStream createPdfCharges(List<tollChargesResponse> charges,int tollID,String tollPlazaName[],String state[]) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,25);

        Paragraph titlePara = new Paragraph("Toll Details", font);
        titlePara.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titlePara);

        document.add(new Paragraph("\n\n\n"));
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA,12);
        Paragraph bodyPara = new Paragraph("Toll ID: "+tollID, bodyFont);
        document.add(bodyPara);
        Paragraph bodyPara2 = new Paragraph("Toll Plaza Name: "+tollPlazaName[0], bodyFont);
        document.add(bodyPara2);
        Paragraph bodyPara3 = new Paragraph("State: "+state[0], bodyFont);
        document.add(bodyPara3);

        document.add(new Paragraph("\n\n\n\n"));


        // Create a table
        PdfPTable table = new PdfPTable(3); // 6 columns for the transaction details
        table.setWidthPercentage(100);

        table.addCell("Category");
        table.addCell("Single");
        table.addCell("Round");

        // Insert rows into the table
        for (tollChargesResponse charge : charges) {
            table.addCell(charge.getCategory());
            table.addCell(String.valueOf(charge.getSingle()));
            table.addCell(String.valueOf(charge.getRound()));
        }

        // Add the table to the document
        document.add(table);

        document.close();

        return out;
    }


    public static List<ChargesListResponse> chargesList(Connection connection[])
    {
        List<ChargesListResponse> responses = new ArrayList<>(); 
        try
        {
            String sqlQuery_1 = "SELECT TollID,CategoryID,Single,Round FROM tariff";
            PreparedStatement preparedStatement_1 = connection[0].prepareStatement(sqlQuery_1);
            ResultSet resultSet_1 = preparedStatement_1.executeQuery();

            while (resultSet_1.next()) 
            {
                int tollID = resultSet_1.getInt("TollID");
                int categoryID = resultSet_1.getInt("CategoryID");
                int single = resultSet_1.getInt("Single");
                int round = resultSet_1.getInt("Round");

        
                    ChargesListResponse response = new ChargesListResponse();
                    response.setId(tollID);
                    response.setCategoryID(categoryID);
                    response.setSingle(single);
                    response.setRound(round);

                    responses.add(response);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return responses;
    }


            public static String chargesListCsv(List<ChargesListResponse> charges) {
        StringBuilder csvContent = new StringBuilder();
    
        // Add CSV header row
        csvContent.append("Toll ID,CategoryID,Single,Round\n");
    
        // Insert rows into the CSV content
        for (ChargesListResponse charge : charges) {
            csvContent.append(charge.getId()).append(",");
            csvContent.append(charge.getCategoryID()).append(",");
            csvContent.append(charge.getSingle()).append(",");
            csvContent.append(charge.getRound()).append("\n");
        }
    
        return csvContent.toString();
    }


            public static byte[] chargesListExcel(List<ChargesListResponse> charges) {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Charges");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Toll ID","CategoryID","Single", "Round"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Insert rows into the Excel sheet
        int rowNum = 1;
        for (ChargesListResponse charge : charges) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(charge.getId());
            row.createCell(1).setCellValue(charge.getCategoryID());
            row.createCell(2).setCellValue(charge.getSingle());
            row.createCell(3).setCellValue(charge.getRound());
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


    public static List<TollListResponse> tollList(Connection connection[])
    {
        List<TollListResponse> responses = new ArrayList<>(); 
        try
        {
            String sqlQuery_1 = "SELECT id,state,toll_plaza_name,mailID FROM toll_list";
            PreparedStatement preparedStatement_1 = connection[0].prepareStatement(sqlQuery_1);
            ResultSet resultSet_1 = preparedStatement_1.executeQuery();

            while (resultSet_1.next()) 
            {
                int tollID = resultSet_1.getInt("id");
                String state = resultSet_1.getString("state");
                String tollPlazaName = resultSet_1.getString("toll_plaza_name");
                String mailId = resultSet_1.getString("mailID");
                String decryptedMailID = EncryptDecrypt.decryptT(Base64.getDecoder().decode(mailId));

        
                    TollListResponse response = new TollListResponse();
                    response.setId(tollID);
                    response.setState(state);
                    response.setTollName(tollPlazaName);
                    response.setMailId(decryptedMailID);
        
                    responses.add(response);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return responses;
    }


        public static ByteArrayOutputStream createTollListPdf(List<TollListResponse> tolls) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,25);

        Paragraph titlePara = new Paragraph("Toll List", font);
        titlePara.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titlePara);

        document.add(new Paragraph("\n\n\n\n"));


        // Create a table
        PdfPTable table = new PdfPTable(4); 
        table.setWidthPercentage(100);

        // Add table headers
        table.addCell("Toll ID");
        table.addCell("State");
        table.addCell("Toll Plaza");
        table.addCell("Email ID");

        // Insert rows into the table
        for (TollListResponse toll : tolls) {
            table.addCell(String.valueOf(toll.getId()));
            table.addCell(toll.getState());
            table.addCell(toll.getTollName());
            table.addCell(toll.getMailId());
        }

        // Add the table to the document
        document.add(table);

        document.close();

        return out;
    }


        public static String createTollListCsv(List<TollListResponse> tolls) {
        StringBuilder csvContent = new StringBuilder();
    
        // Add CSV header row
        csvContent.append("Toll ID,State,Toll Plaza,Email ID\n");
    
        // Insert rows into the CSV content
        for (TollListResponse toll : tolls) {
            csvContent.append(toll.getId()).append(",");
            csvContent.append(toll.getState()).append(",");
            csvContent.append(toll.getTollName()).append(",");
            csvContent.append(toll.getMailId()).append("\n");
        }
    
        return csvContent.toString();
    }

        public static byte[] createTollListExcel(List<TollListResponse> tolls) {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Tolls");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Toll ID","State","Toll Plaza", "Email ID"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Insert rows into the Excel sheet
        int rowNum = 1;
        for (TollListResponse toll : tolls) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(toll.getId());
            row.createCell(1).setCellValue(toll.getState());
            row.createCell(2).setCellValue(toll.getTollName());
            row.createCell(3).setCellValue(toll.getMailId());
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

    // public static List<TransactionResponse> transaction(Connection connection[])
    // {
    //     List<TransactionResponse> responses = new ArrayList<>(); 
    //     try
    //     {
    //         String sqlQuery_10 = "SELECT Transaction_Id,date_time,vehicle_number,toll_plaza_id,Closing_Account_balance,Transaction_amount,Journey_Type FROM transactions";
    //         PreparedStatement preparedStatement_10 = connection[0].prepareStatement(sqlQuery_10);
    //         ResultSet resultSet_10 = preparedStatement_10.executeQuery();
        
    //         while (resultSet_10.next()) 
    //         {             
    //             int transactionID = resultSet_10.getInt("Transaction_Id");
    //             // this DateTime variable is used to store the date and time of the transaction in the timestamp format
    //             java.sql.Timestamp DateTime = resultSet_10.getTimestamp("date_time");
    //             String vehicleNumber = resultSet_10.getString("vehicle_number");
    //             int tollID = resultSet_10.getInt("toll_plaza_id");
    //             int closingAccountBalance = resultSet_10.getInt("Closing_Account_balance");
    //             int transactionAmount = resultSet_10.getInt("Transaction_amount");
    //             String journeyType = resultSet_10.getString("Journey_Type");
            
    //             String sqlQuery_101 = "SELECT Category_id FROM vehicle_details WHERE Vehicle_Number ='"+vehicleNumber+"'";
    //             PreparedStatement preparedStatement_101 = connection[0].prepareStatement(sqlQuery_101);
    //             ResultSet resultSet_101 = preparedStatement_101.executeQuery();

    //             String categoryName_102 = null;

    //             while (resultSet_101.next())
    //             {
    //                 int categoryID_101 = resultSet_101.getInt("Category_id");
    //                 String sqlQuery_102 = "SELECT CategoryName FROM category WHERE CategoryID ="+ categoryID_101;
    //                 PreparedStatement preparedStatement_102 = connection[0].prepareStatement(sqlQuery_102);
    //                 ResultSet resultSet_102 = preparedStatement_102.executeQuery();

    //                 while (resultSet_102.next()) 
    //                 {
    //                     categoryName_102 = resultSet_102.getString("CategoryName");  

    //                     String sqlQuery_1 = "SELECT state,toll_plaza_name FROM toll_list WHERE id = "+ tollID;
    //                     PreparedStatement preparedStatement_1 = connection[0].prepareStatement(sqlQuery_1);
    //                     ResultSet resultSet_1 = preparedStatement_1.executeQuery();

    //                     while (resultSet_1.next()) 
    //                     {
    //                         String state = resultSet_1.getString("state");
    //                         String tollPlazaName = resultSet_1.getString("toll_plaza_name");
                    
    //                             TransactionResponse response = new TransactionResponse();
    //                             response.setTransactionID(transactionID);
    //                             response.setDateTime(DateTime);
    //                             response.setTollID(tollID);
    //                             response.setTollPlaza(tollPlazaName);
    //                             response.setState(state);
    //                             response.setVehicleNumber(vehicleNumber);
    //                             response.setVehicleType(categoryName_102);
    //                             response.setClosingAccountBalance(closingAccountBalance);
    //                             response.setCharges(transactionAmount);
    //                             response.setJourneyType(journeyType);
                    
    //                             responses.add(response);
    //                     }
    //                 }
    //             }                     
    //         }
    //     }
    //     catch(Exception e)
    //     {
    //         e.printStackTrace();

    //     }

    //     return responses;
    // }

    public static List<TransactionResponse> transaction(Connection connection[]) {
        List<TransactionResponse> responses = new ArrayList<>();
        try {
            String sqlQuery = "SELECT t.Transaction_Id, t.date_time, t.vehicle_number, t.toll_plaza_id, t.Closing_Account_balance, t.Transaction_amount, t.Journey_Type, vd.Category_id, c.CategoryName, tl.state, tl.toll_plaza_name " +
                              "FROM transactions t " +
                              "INNER JOIN vehicle_details vd ON t.vehicle_number = vd.Vehicle_Number " +
                              "INNER JOIN category c ON vd.Category_id = c.CategoryID " +
                              "INNER JOIN toll_list tl ON t.toll_plaza_id = tl.id "+
                              "ORDER BY t.Transaction_Id ASC";
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                int transactionID = resultSet.getInt("Transaction_Id");
                java.sql.Timestamp DateTime = resultSet.getTimestamp("date_time");
                String vehicleNumber = resultSet.getString("vehicle_number");
                int tollID = resultSet.getInt("toll_plaza_id");
                int closingAccountBalance = resultSet.getInt("Closing_Account_balance");
                int transactionAmount = resultSet.getInt("Transaction_amount");
                String journeyType = resultSet.getString("Journey_Type");
                // int categoryID_101 = resultSet.getInt("Category_id");
                String categoryName_102 = resultSet.getString("CategoryName");
                String state = resultSet.getString("state");
                String tollPlazaName = resultSet.getString("toll_plaza_name");
    
                TransactionResponse response = new TransactionResponse();
                response.setTransactionID(transactionID);
                response.setDateTime(DateTime);
                response.setTollID(tollID);
                response.setTollPlaza(tollPlazaName);
                response.setState(state);
                response.setVehicleNumber(vehicleNumber);
                response.setVehicleType(categoryName_102);
                response.setClosingAccountBalance(closingAccountBalance);
                response.setCharges(transactionAmount);
                response.setJourneyType(journeyType);
    
                responses.add(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return responses;
    }
    


       public static ByteArrayOutputStream createPdf(List<TransactionResponse> transactions) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD,25);

        Paragraph titlePara = new Paragraph("Fastag Transaction History", font);
        titlePara.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titlePara);

        document.add(new Paragraph("\n\n\n\n"));


        // Create a table
        PdfPTable table = new PdfPTable(10); 
        table.setWidthPercentage(100);

        // Add table headers
        table.addCell("Transaction ID");
        table.addCell("Date and Time");
        table.addCell("Toll ID");
        table.addCell("Toll Plaza");
        table.addCell("State");
        table.addCell("Vehicle Number");
        table.addCell("Vehicle Type");
        table.addCell("Closing Balance");
        table.addCell("Charges");
        table.addCell("Journey Type");

        // Insert rows into the table
        for (TransactionResponse transaction : transactions) {
            table.addCell(String.valueOf(transaction.getTransactionID()));
            table.addCell(transaction.getDateTime().toString());
            table.addCell(String.valueOf(transaction.getTollID()));
            table.addCell(transaction.getTollPlaza());
            table.addCell(transaction.getState());
            table.addCell(transaction.getVehicleNumber());
            table.addCell(transaction.getVehicleType());
            table.addCell(String.valueOf(transaction.getClosingAccountBalance()));
            table.addCell(String.valueOf(transaction.getCharges()));
            table.addCell(transaction.getJourneyType());
        }

        // Add the table to the document
        document.add(table);

        document.close();

        return out;
    }


    public static String generateCsv(List<TransactionResponse> transactions) {
        StringBuilder csvContent = new StringBuilder();
    
        // Add CSV header row
        csvContent.append("Transaction ID,Date and Time,Toll ID,Toll Plaza,State,Vehicle Number,Vehicle Type,Vehicle Closing Balance,Charges,Journey Type\n");
    
        // Insert rows into the CSV content
        for (TransactionResponse transaction : transactions) {
            csvContent.append(transaction.getTransactionID()).append(",");
            csvContent.append(transaction.getDateTime().toString()).append(",");
            csvContent.append(transaction.getTollID()).append(",");
            csvContent.append(transaction.getTollPlaza()).append(",");
            csvContent.append(transaction.getState()).append(",");
            csvContent.append(transaction.getVehicleNumber()).append(",");
            csvContent.append(transaction.getVehicleType()).append(",");
            csvContent.append(transaction.getClosingAccountBalance()).append(",");
            csvContent.append(transaction.getCharges()).append(",");
            csvContent.append(transaction.getJourneyType()).append("\n");
        }
    
        return csvContent.toString();
    }

    public static byte[] generateExcel(List<TransactionResponse> transactions) {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Transactions");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Transaction ID", "Date and Time","Toll ID","Toll Plaza","State", "Vehicle Number", "Vehicle Type","Vehicle Closing Balance", "Charges", "Journey Type"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Insert rows into the Excel sheet
        int rowNum = 1;
        for (TransactionResponse transaction : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getTransactionID());
            row.createCell(1).setCellValue(transaction.getDateTime().toString());
            row.createCell(2).setCellValue(transaction.getTollID());
            row.createCell(3).setCellValue(transaction.getTollPlaza());
            row.createCell(4).setCellValue(transaction.getState());
            row.createCell(5).setCellValue(transaction.getVehicleNumber());
            row.createCell(6).setCellValue(transaction.getVehicleType());
            row.createCell(7).setCellValue(transaction.getClosingAccountBalance());
            row.createCell(8).setCellValue(transaction.getCharges());
            row.createCell(9).setCellValue(transaction.getJourneyType());
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


}
