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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import org.apache.poi.ss.usermodel.*;
import java.security.Key;



public class FastagService
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

        public static String generateJwtToken(Key secretKey,int tollID) {
        // Define the token expiration time (e.g., 1 hour from now)
        long expirationMillis = System.currentTimeMillis() + 1800000; // 30 mins in milliseconds

        // Build the JWT token with an expiration date
        return Jwts.builder()
                .setSubject(String.valueOf(tollID)) // You can set any subject or user identifier
                .setExpiration(new Date(expirationMillis))
                .signWith(secretKey)
                .compact();
    }


        public static String login(Key secretKey,boolean islogin[],String response,String passEntered,String state[],String tollPlazaName[],int tollID,Connection connection[],javax.sql.DataSource dataSource)
    {
        try
        {
            if (connection[0] == null || connection[0].isClosed()) 
            {
                connection[0] = dataSource.getConnection();
            }

            String sqlQuery = "SELECT password FROM toll_list WHERE id = ?";
            // this preparedStatement object is used to prepare the sql query
            PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
            preparedStatement.setInt(1, tollID);
            // this resultSet object is used to execute the sql query and store the result of the sql query
            ResultSet resultSet = preparedStatement.executeQuery();

            // this correctpass variable is used to store the correct password of the toll plaza
            String correctpass = null;

            // this if conditon is used to store the correct password of the toll plaza in the correctpass variable
            if (resultSet.next()) 
            {
                correctpass = resultSet.getString("password");
            }

            // now we will create hash code of password entered by user to verify it with has code stored in the database
            // this encryptedEnteredPassword variable is used to store the encrypted password entered by the user in the byte array format
            // the password entered by the user is encrypted using the AES algorithm
            byte[] encryptedEnteredPassword = EncryptDecrypt.encrypt(passEntered);
            // this encryptedUserEnteredString variable is used to store the encrypted password entered by the user in the string format and compare it 
            // with the password stored in the database
            String encryptedUserEnteredString = Base64.getEncoder().encodeToString(encryptedEnteredPassword);

            // this if condition is used to check whether the password entered by the user is correct or not
            // if the password entered by the user is correct then the program will proceed further
            if (encryptedUserEnteredString.equals(correctpass) )
            {
                String sqlQuery_1 = "SELECT state,toll_plaza_name FROM toll_list WHERE id = ?";
                PreparedStatement preparedStatement_1 = connection[0].prepareStatement(sqlQuery_1);
                preparedStatement_1.setInt(1, tollID);
                ResultSet resultSet_1 = preparedStatement_1.executeQuery();


                // this while loop is used to print the state and toll plaza name of the toll plaza
                // the state and toll plaza name of the toll plaza is fetched from the resultSet_1 object
                // this while loop will run only once
                // because there is only one toll plaza with the given tollID
                while (resultSet_1.next()) 
                {
                    state[0] = resultSet_1.getString("state");
                    tollPlazaName[0] = resultSet_1.getString("toll_plaza_name");

                    LoginResponse loginResponse = new LoginResponse();

                    loginResponse.setState(state[0]);
                    loginResponse.setTollPlaza(tollPlazaName[0]);
                    loginResponse.setJwtToken(generateJwtToken(secretKey,tollID));


                    response = objectMapper.writeValueAsString(loginResponse);
                        // response = "{\"State\":\"" + state[0] + "\",\"Toll Plaza\":\"" + tollPlazaName[0] + "\"}";
                }

                islogin[0] = true;
            }
            else
            {
                response = objectMapper.writeValueAsString(new MessageResponse("Invalid credentials"));
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


        public static String forgotPassword(String response,String enteredMailID,int tollID,Connection connection[],int otpRandom[],javax.sql.DataSource dataSource)
    {
        try 
        {
            if (connection[0] == null || connection[0].isClosed()) 
            {
                connection[0] = dataSource.getConnection();
            }

            otpRandom[0] = FastagService.sendOTP(enteredMailID,tollID,connection,dataSource);

            if(otpRandom[0]==-1)
            {
                response = "Invalid Mail ID";
                System.exit(-1);
            }
            else if(otpRandom[0]==-2)
            {
                response = "Error";
                System.exit(-1);
            }
            else
            {
                response = "OTP sent successfully! ";
            }
        }
        catch(Exception e)
        {   
            response = "Error"+e.getMessage();
            System.exit(-1);    
        }
        return response;
    }

        public static int sendOTP(String enteredMailID,int tollID,Connection connection[],DataSource dataSource)
    {
        try
        {
            if (connection[0] == null || connection[0].isClosed()) 
            {
                connection[0] = dataSource.getConnection();
            }
            byte[] encryptedEnteredMailID = EncryptDecrypt.encrypt(enteredMailID);
            String encryptedMailID = Base64.getEncoder().encodeToString(encryptedEnteredMailID);

            String sqlQuery_6 = "SELECT State,toll_plaza_name,mailID FROM toll_list WHERE id = ?";
            PreparedStatement preparedStatement_6 = connection[0].prepareStatement(sqlQuery_6);
            preparedStatement_6.setInt(1, tollID);
            ResultSet resultSet_6 = preparedStatement_6.executeQuery();

            String mailID=null;
            String tollPlazaName=null;
            String state = null;
            if(resultSet_6.next())
            {
                mailID = resultSet_6.getString("mailID");
                tollPlazaName = resultSet_6.getString("toll_plaza_name");
                state = resultSet_6.getString("State");

            }
            if(encryptedMailID.equals(mailID))
            {
                Random random = new Random();
                    
                int otpRandom = random.nextInt(900000)+100000;

                String to = enteredMailID;
                String from = "hardik23555@gmail.com";
                String subject = "FASTMAN TOLL PLAZA PASSWORD RECOVERY";
                String text = null;

                text = "Dear Toll Plaza Owner,\n\nToll Plaza ID:"+tollID +"\nToll Plaza: "+tollPlazaName+"\nState: "+state+"\nYour OTP to change password is: "+otpRandom+"\n\n\nRegards,\nFASTMAN";
                System.out.println("Sending OTP to your registered Email ID");
                try
                {
                    SendMail sm = new SendMail();
                    sm.sendEmail(to, from, subject, text);
                }
                catch(Exception e)
                {
                    System.out.println("Error");
                    // System.exit(-1);
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

    public static String updatePass(String response,String newPass,String confirmNewPass,int tollID,Connection connection[])
    {
        try
        {
            if(newPass.equals(confirmNewPass))
            {
                try{
                byte[] encrypted = EncryptDecrypt.encrypt(newPass);
                String newEncryptedPassByUserString = Base64.getEncoder().encodeToString(encrypted);

                String sqlQuery1 = "UPDATE toll_list SET password = ? WHERE id = ?";
                PreparedStatement preparedStatement1 = connection[0].prepareStatement(sqlQuery1);
                preparedStatement1.setString(1, newEncryptedPassByUserString);
                preparedStatement1.setInt(2, tollID);   
                preparedStatement1.execute();
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
        catch(Exception e)
        {
            response = "Error"+e.getMessage();
            return response;
        }
    }



    // public static List<tollChargesResponse> tollCharges (Connection connection[],int tollID)
    // {
    //     List<tollChargesResponse> responses = new ArrayList<>(); 
    //     try
    //     {
    //         for(int i=1;i<=7;i++)
    //         {
    //             String sqlQuery_10 = "SELECT TollID, CategoryID, Single, Round FROM tariff WHERE TollID = ? AND CategoryID = ?";

    //             PreparedStatement preparedStatement_10 = connection[0].prepareStatement(sqlQuery_10);
    //             preparedStatement_10.setInt(1, tollID);
    //             preparedStatement_10.setInt(2, i);
    //             ResultSet resultSet_10 = preparedStatement_10.executeQuery();
            
    //             while (resultSet_10.next()) 
    //             {             
    //                 int resultTollID = resultSet_10.getInt("TollID");
    //                 int resultCategoryID = resultSet_10.getInt("CategoryID");
    //                 int resultSingle = resultSet_10.getInt("Single");
    //                 int resultRound = resultSet_10.getInt("Round");
                
    //                     String sqlQuery_102 = "SELECT CategoryName FROM category WHERE CategoryID =?";
    //                     PreparedStatement preparedStatement_102 = connection[0].prepareStatement(sqlQuery_102);
    //                     preparedStatement_102.setInt(1, resultCategoryID);

    //                     ResultSet resultSet_102 = preparedStatement_102.executeQuery();

    //                     while (resultSet_102.next()) 
    //                     {
    //                         String categoryName_102 = resultSet_102.getString("CategoryName");  
                            
    //                         tollChargesResponse response = new tollChargesResponse();
    //                         response.setTollID(resultTollID);
    //                         response.setCategory(categoryName_102);
    //                         response.setSingle(resultSingle);
    //                         response.setRound(resultRound);
                
    //                         responses.add(response);
    //                     }
                                        
    //             }
    //         }
    //     }
    //     catch(Exception e)
    //     {
    //         e.printStackTrace();
    //     }
    //     return responses;
    // }

        public static List<tollChargesResponse> tollCharges (Connection connection[],int tollID)
    {
        List<tollChargesResponse> responses = new ArrayList<>(); 
        try
        {
                String sqlQuery = "SELECT t.TollID, t.CategoryID, t.Single, t.Round, c.CategoryName " +
                "FROM tariff t " +
                "INNER JOIN category c ON t.CategoryID = c.CategoryID " +
                "WHERE t.TollID = ?";

                PreparedStatement preparedStatement = connection[0].prepareStatement(sqlQuery);
                preparedStatement.setInt(1, tollID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) 
                {
                    int resultTollID = resultSet.getInt("TollID");
                    int resultSingle = resultSet.getInt("Single");
                    int resultRound = resultSet.getInt("Round");
                    String categoryName = resultSet.getString("CategoryName");

                    tollChargesResponse response = new tollChargesResponse();
                    response.setTollID(resultTollID);
                    response.setCategory(categoryName);
                    response.setSingle(resultSingle);
                    response.setRound(resultRound);

                    responses.add(response);
                }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return responses;
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

    public static String vehicleEntr(String response,String vehicle_number,int tollID,String LightColor,String tollPlazaName[],String state[],Connection connection[])
    {

        try
        {
            int categoryID=0;
            int accountBalance=0;
            String MailID = null;
            // this roundJourneyFlag variable is used to check whether the vehicle is on round journey or not
            // if the vehicle is on round journey then the roundJourneyFlag variable will be 1
            // if the vehicle is not on round journey then the roundJourneyFlag variable will be 0
            int roundJourneyFlag = 0;


            String sqlQuery_2 = "SELECT Category_id,Account_balance,mail_id FROM vehicle_details WHERE Vehicle_Number = ?";
            try(PreparedStatement preparedStatement_2 = connection[0].prepareStatement(sqlQuery_2))
            {
                    preparedStatement_2.setString(1, vehicle_number);
                // this resultSet_2 object is used to execute the sql query and store the result of the sql query
                ResultSet resultSet_2 = preparedStatement_2.executeQuery();

                // used to fetch the category id and account balance of the vehicle from the resultSet_2 object
                if (resultSet_2.next()) 
                {
                    categoryID = resultSet_2.getInt("Category_id");
                    accountBalance = resultSet_2.getInt("Account_balance");
                    MailID = resultSet_2.getString("mail_id");

                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            // if the vehicle number entered by the user is incorrect then the program will terminate
            // because there is no vehicle with the given vehicle number
            // and the loop will not execute further for this iteration
            if(categoryID==0)
            {
                System.out.println("Invalid Vehicle Number");
                // System.out.println();
                response = objectMapper.writeValueAsString(new MessageResponse("Invalid Vehicle Number"));
                // return response;
            }
            else
            {

            // this sqlQuery_31 variable is used to store the sql query
            // this sql query is used to fetch the timestamp and transaction amount of the previous transaction
            // the timestamp and JourneyType is fetched from the resultSet_31 object
            // this query will fetch only one transaction in descending time order because we want the latest transaction


            // VERY IMP MODIFIED THE QUERY TEMPORARY CORRECT IT


            String sqlQuery_31 = "SELECT Date_Time,Journey_Type FROM transactions WHERE toll_plaza_id = ? AND vehicle_number = ?";
            PreparedStatement preparedStatement_31 = connection[0].prepareStatement(sqlQuery_31);
            preparedStatement_31.setInt(1, tollID);
            preparedStatement_31.setString(2, vehicle_number);
            ResultSet resultSet_31 = preparedStatement_31.executeQuery();

            // this previouTimestamp variable is used to store the timestamp of the previous transaction
            java.sql.Timestamp previouTimestamp = new java.sql.Timestamp(0);
            String previousJourneyType = "a";

            // this while loop is used to fetch the timestamp and transaction amount of the previous transaction
            // the timestamp and transaction amount of the previous transaction is fetched from the resultSet_31 object
            // this while loop will run only once
            // because there is only one previous transaction with the given tollID and vehicle number
            while (resultSet_31.next()) 
            {
                previouTimestamp = resultSet_31.getTimestamp("Date_Time");
                previousJourneyType = resultSet_31.getString("Journey_Type");
            }
        
            // this seconds variable is used to store the number of seconds between the current time and the previous transaction time
            long seconds=0;

            // this if condition is used to check whether the previous transaction time is 0 or not
            // if the previous transaction time is 0 then the seconds variable will be 0 that means that vehicle is entering the toll plaza for the first time
            if(!previouTimestamp.equals(new java.sql.Timestamp(0)))
            {
                // this currentDate1 variable is used to store the current date and time
                java.util.Date currentDate1 = new java.util.Date();
            
                // this timestamp1 object of Timestamp class is used to store the current date and time in the timestamp format
                // time method is used to get the current time in milliseconds
        
                java.sql.Timestamp timestamp1 = new java.sql.Timestamp(currentDate1.getTime());

                // bigger timestamp - smaller timestamp = duration
                // 2 nd is bigger 1 st is smaller
                // this duration object is used to store the difference between the current time and the previous transaction time
                Duration duration = Duration.between(previouTimestamp.toInstant(), timestamp1.toInstant());

                // this seconds variable is used to store the number of seconds between the current time and the previous transaction time
                seconds = duration.toSeconds();
            }

            String sqlQuery_3 = "SELECT Single,Round FROM tariff WHERE CategoryID = ? AND TollID = ?";
            PreparedStatement preparedStatement_3 = connection[0].prepareStatement(sqlQuery_3);
            preparedStatement_3.setInt(1, categoryID);
            preparedStatement_3.setInt(2, tollID);
            ResultSet resultSet_3 = preparedStatement_3.executeQuery();

            int applicableTariff=0;
            int singleJourneyTariff=0;

            // this while loop is used to fetch the category name and applicable tariff of the vehicle
            // the category name and applicable tariff and round journey tariff of the vehicle is fetched from the resultSet_3 object
            // this while loop will run only once
            // because there is only one category with the given categoryID
            while (resultSet_3.next()) 
            {
                applicableTariff = resultSet_3.getInt("Single");
                singleJourneyTariff = applicableTariff;
                int roundJourneyTariff = resultSet_3.getInt("Round");

                // this if condition is used to check whether the vehicle is on round journey or not
                // if the vehicle is on round journey then the roundJourneyFlag variable will be set as 1
                // if the vehicle is not on round journey then the roundJourneyFlag variable will not be changed
                // if the vehicle is on round journey then the applicable tariff will be changed to round journey tariff-applicable tariff
                // if seconds>0  is used to check whether the vehicle is entering the toll plaza for the first time or not
                // as if the vehicle is entering the toll plaza for the first time then seconds will be 0
                // if seconds<43200 is used to check whether the vehicle is entering the toll plaza within 12 hours of the previous transaction or not
                // as if the vehicle is entering the toll plaza within 12 hours of the previous transaction then seconds will be less than 43200
                // previousJourneyType.equals("SINGLE") is used to check whether the previous transaction was a single journey or not
                // even if the previous journey was single but if the vehicle is entering the toll plaza after 12 hours of the previous transaction then the vehicle will be considered as a single journey

                if(seconds>0 && seconds<43200 && previousJourneyType.equals("SINGLE"))
                {
                    applicableTariff = roundJourneyTariff-applicableTariff;
                    roundJourneyFlag = 1;
                }
            }
  
            // this sql query is used to fetch the category name of the vehicle
            String sqlQuery_311 = "SELECT CategoryName FROM category WHERE CategoryID = ?";
            PreparedStatement preparedStatement_311 = connection[0].prepareStatement(sqlQuery_311);
            preparedStatement_311.setInt(1, categoryID);
            ResultSet resultSet_311 = preparedStatement_311.executeQuery();

            String categoryName = null;

            // this while loop is used to fetch the category name of the vehicle
            // the category name of the vehicle is fetched from the resultSet_311 object
            while (resultSet_311.next()) 
            {
                categoryName = resultSet_311.getString("CategoryName");
            }

            // remainingBalance of vehicle is calculated by subtracting the applicable tariff from the account balance
            int remainingBalance = accountBalance-applicableTariff;

            // this if condition is used to check whether the remaining balance of the vehicle is greater than or equal to 0 or not
            // as if remainingBalance is less than zero that means the vehicle does not have sufficient balance to pay the toll
            // if the vehicle does not have sufficient balance to pay the toll then the vehicle will not be allowed to pass through the toll plaza
            if(remainingBalance>=0)
            {
                LightColor = "Green";
                // this sqlQuery_4 variable is used to store the sql query
                // this sql query is used to update the account balance of the vehicle in the database
                // the account balance of the vehicle is updated to remainingBalance
                // the account balance of the vehicle is updated in the vehicle_details table of the database

                String sqlQuery_4 = "Update vehicle_details SET Account_balance = ? WHERE Vehicle_Number = ?";
                // this p object is used to prepare the sql query
                PreparedStatement p = connection[0].prepareStatement(sqlQuery_4);
                p.setInt(1, remainingBalance);
                p.setString(2, vehicle_number);
                // this execute method is used to execute the sql query
                p.execute();

                // this currentDate variable is used to store the current date and time
                java.util.Date currentDate = new java.util.Date();
                // this timestamp object of Timestamp class is used to store the current date and time in the timestamp format
                // time method is used to get the current time in milliseconds
                java.sql.Timestamp timestamp = new java.sql.Timestamp(currentDate.getTime());

                // this sqlQuery_5 variable is used to store the sql query
                // the transaction details of the vehicle is inserted in the transactions table of the database
                // the transaction details of the vehicle includes the date and time of the transaction, vehicle number, toll plaza id, closing account balance and transaction amount
                // the closing account balance is the remaining balance of the vehicle after paying the toll
                // the transaction amount is the applicable tariff of the vehicle
                // the transaction details of the vehicle is inserted in the transactions table of the database

                if(roundJourneyFlag==0)
                {
                    String sqlQuery_5 = "INSERT INTO transactions (Date_Time,vehicle_number,toll_plaza_id,Closing_Account_balance,Transaction_amount,Journey_Type) VALUES (?,?,?,?,?,?)";
                    PreparedStatement p_5 = connection[0].prepareStatement(sqlQuery_5);
                    p_5.setTimestamp(1, timestamp);
                    p_5.setString(2, vehicle_number);
                    p_5.setInt(3, tollID);
                    p_5.setInt(4, remainingBalance);
                    p_5.setInt(5, applicableTariff);
                    p_5.setString(6, "SINGLE");
                    p_5.execute();
                }
                else if(roundJourneyFlag==1)
                {
                    String sqlQuery_99 = "INSERT INTO transactions (Date_Time,vehicle_number,toll_plaza_id,Closing_Account_balance,Transaction_amount,Journey_Type) VALUES (?,?,?,?,?,?)";
                    PreparedStatement p_99 = connection[0].prepareStatement(sqlQuery_99);
                    p_99.setTimestamp(1, timestamp);
                    p_99.setString(2, vehicle_number);
                    p_99.setInt(3, tollID);
                    p_99.setInt(4, remainingBalance);
                    p_99.setInt(5, applicableTariff);
                    p_99.setString(6, "RETURN");
                    p_99.execute();   
                }

                System.out.println("Light: "+LightColor);
                System.out.println("Vehicle Type: "+categoryName);
                // this if condition is used to check whether the vehicle is on round journey or not
                String jouneyType = null;
                if(roundJourneyFlag==1)
                {
                    jouneyType = "Return Journey";
                    // System.out.println("Return Journey");
                }
                else if(roundJourneyFlag==0)
                {
                    jouneyType = "Single Journey";
                    // System.out.println("Single Journey");
                }
                System.out.println("Charges: "+applicableTariff+ " Account Balance: "+remainingBalance);
                // this if condition is used to check whether the remaining balance of the vehicle is less than or equal to 2 times the single journey tariff or not
                // if the remaining balance of the vehicle is less than or equal to 2 times the single journey tariff then the user will be alerted to recharge the account
                // as the vehicle will not be able to pay the toll for the next 2 toll plazas

                Date date = new Date(timestamp.getTime());
                // SimpleDateFormat class is used to format the date
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd YYYY   HH:mm:ss  ");
                // formattedDate is used to store the date in the format specified in the constructor of SimpleDateFormat class
                // formattedDate is of type String
                String formattedDate = sdf.format(date);

                StringBuilder text = new StringBuilder();
                text = text.append("Dear FASTMAN USER,\n\n");

                String alert = null;

                if(remainingBalance<=(2*singleJourneyTariff))
                {
                    alert = "ALERT! Low Remaining Balance for next toll plazas! Please Recharge your account immediately to avoid any future interruption!!";
                    // System.out.println("ALERT! Low Remaining Balance for next toll plazas! Please Recharge your account immediately to avoid any future interruption!!");
                    // System.out.println();
                    text = text.append(alert);
                }

                System.out.println("Thank You");
                System.out.println("Happy Journey");
                

                if(roundJourneyFlag==0)
                {
                    text = text.append("Your vehicle with vehicle number\n"+vehicle_number+"\nhas crossed \nToll plaza:: "+tollPlazaName[0]+"\nSTATE:: "+state[0]+ "\nat "+formattedDate+"\nApplicable Charges: "+applicableTariff + "\nJourney Type: SINGLE\nUpdated Account Balance:: "+remainingBalance+" \n\nThank You\nFASTMAN ");
                }
                else if(roundJourneyFlag==1)
                {
                    text = text.append("Your vehicle with vehicle number\n"+vehicle_number+"\nhas crossed \nToll plaza:: "+tollPlazaName[0]+"\nSTATE:: "+state[0]+ "\nat "+formattedDate+"\nApplicable Charges: "+applicableTariff + "\nJourney Type: RETURN\nUpdated Account Balance:: "+remainingBalance+" \n\nThank You\nFASTMAN ");
                }

                // String jsonResponse = "{\"Light\":" + LightColor + ",\"Vehicle Type\":\"" + categoryName + "\",\"Journey Type\":" + jouneyType + ",\"Charges\":\"" + applicableTariff + "\",\"Account Balance\":\"" + remainingBalance + "\"}";

                VehicleEntryResponse vehicleEntryResponse = new VehicleEntryResponse(); 

                vehicleEntryResponse.setLight(LightColor);
                vehicleEntryResponse.setVehicleType(categoryName);
                vehicleEntryResponse.setJourneyType(jouneyType);
                vehicleEntryResponse.setCharges(applicableTariff);
                vehicleEntryResponse.setAccountBalance(remainingBalance);
                vehicleEntryResponse.setAlert(alert);

                response = objectMapper.writeValueAsString(vehicleEntryResponse);
                
                // response = "{" +
                // "\"Light\":\"" + LightColor + "\"," +
                // "\"Vehicle Type\":\"" + categoryName + "\"," +
                // "\"Journey Type\":\"" + jouneyType + "\"," +
                // "\"Charges\":\"" + applicableTariff + "\"," +
                // "\"Account Balance\":\"" + remainingBalance + "\"," +
                // "\"Alert\":\"" + alert + "\"" +
                // "}";

                // IMPORTANT     --------------------------------------------
                // for sending mail commented off for testing purpose

                // String finalText = text.toString();
                // System.out.println("Sending Mail");

                // String decryptedMailID = EncryptDecrypt.decrypt(Base64.getDecoder().decode(MailID));

                // String to = decryptedMailID;
                // String from = "hardik23555@gmail.com";
                // String subject = "FASTAG";

                // try
                // {
                //     SendMail sm = new SendMail();
                //     sm.sendEmail(to, from, subject, finalText);
                // }
                // catch(Exception e)
                // {
                //     System.out.println("Mail Error");
                // }

            }
                // if the vehicle does not have sufficient balance to pay the toll then the vehicle will not be allowed to pass through the toll plaza
                // and the user will be alerted to recharge the account
            else if(remainingBalance<0)
            {
                LightColor = "Red";
                String alert = "ALERT! Low Account Balance! Can not Pay the toll! Please Recharge your account immediately!!";

                VehicleEntryResponse vehicleEntryResponse = new VehicleEntryResponse();

                vehicleEntryResponse.setLight(LightColor);
                vehicleEntryResponse.setAlert(alert);

                response = objectMapper.writeValueAsString(vehicleEntryResponse);

                // response = "{"
                // + "\"Light\":\"" + LightColor + "\","
                // + "\"Alert\":\"" + alert + "\""
                // + "}";

            }
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            try {
                response = objectMapper.writeValueAsString(new MessageResponse("Server error"));
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        }
        return response;
    }


    // public static List<TransactionResponse> transaction (Connection connection[],int tollID)
    // {
    //     List<TransactionResponse> responses = new ArrayList<>(); 
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
                        
    //                     TransactionResponse response = new TransactionResponse();
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


    public static List<TransactionResponse> transaction (Connection connection[],int tollID)
    {
        List<TransactionResponse> responses = new ArrayList<>(); 
        try
        {
            String sqlQuery = "SELECT t.Transaction_Id, t.Date_Time, t.vehicle_number, t.Transaction_amount, t.Journey_Type, vd.Category_id, c.CategoryName " +
            "FROM transactions t " +
            "INNER JOIN vehicle_details vd ON t.vehicle_number = vd.Vehicle_Number " +
            "INNER JOIN category c ON vd.Category_id = c.CategoryID " +
            "WHERE t.toll_plaza_id = ? " +
            "ORDER BY t.Transaction_Id ASC"; // Sort by Transaction_Id in ascending order

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

            TransactionResponse response = new TransactionResponse();
            response.setTransactionId(transactionID);
            response.setDateTime(DateTime);
            response.setVehicleNumber(vehicleNumber);
            response.setVehicleType(categoryName);
            response.setCharges(transactionAmount);
            response.setJourneyType(journeyType);

            responses.add(response);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return responses;
    }


    
    public static ByteArrayOutputStream createPdf(List<TransactionResponse> transactions,int tollID,String tollPlazaName[],String state[]) throws DocumentException {
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
        for (TransactionResponse transaction : transactions) {
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


    public static String generateCsvContent(List<TransactionResponse> transactions) {
        StringBuilder csvContent = new StringBuilder();
    
        // Add CSV header row
        csvContent.append("Transaction ID,Date and Time,Vehicle Number,Vehicle Type,Charges,Journey Type\n");
    
        // Insert rows into the CSV content
        for (TransactionResponse transaction : transactions) {
            csvContent.append(transaction.getTransactionId()).append(",");
            csvContent.append(transaction.getDateTime().toString()).append(",");
            csvContent.append(transaction.getVehicleNumber()).append(",");
            csvContent.append(transaction.getVehicleType()).append(",");
            csvContent.append(transaction.getCharges()).append(",");
            csvContent.append(transaction.getJourneyType()).append("\n");
        }
    
        return csvContent.toString();
    }



    
public static byte[] generateExcelContent(List<TransactionResponse> transactions) {
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
        for (TransactionResponse transaction : transactions) {
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

    public static String generateCaptcha()
    {
        // this is the string which contains all the characters which are used to generate captcha
        String s = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        // this lenCaptcha variable is used to store the length of the captcha
        int lenCaptcha = 4;
        // this random object is used to generate random numbers
        Random rand = new Random();
        // this randomCaptcha object is used to store the generated captcha
        StringBuilder randomCaptcha = new StringBuilder();
        // this for loop is used to generate captcha
        // the characters are selected randomly from the string s
        for(int i=0;i<lenCaptcha;i++)
        {
            // this line is used to generate random numbers from 0 to length of the string s
            int randomDigits = rand.nextInt(s.length());
            // this line is used to append the randomly selected character to the randomCaptcha object
            // the character is selected from the string s at the index randomDigits
            randomCaptcha.append(s.charAt(randomDigits));
        }
        return randomCaptcha.toString();
    }


}
