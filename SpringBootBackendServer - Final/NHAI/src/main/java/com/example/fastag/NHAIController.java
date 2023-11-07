package com.example.fastag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.sql.DataSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;



@RestController
@RequestMapping("/NHAI")
public class NHAIController 
{
    int tollID;
    String vehicleNumber;
    String [] tollPlazaName = new String[1];
    // String tollPlazaName = null;
    String [] state = new String[1];
    // String state = null;
    int otpRandom = 0;
    boolean [] islogin = new boolean[1];
    // islogin[0] = false;
    // boolean islogin = false;
    boolean [] isloginForgot = new boolean[1];
    // boolean isloginForgot = false;
    int otpToDelete = 0;
        String newMail = null;
    boolean newMailVerified = false;
    String username = null;


    private DataSource dataSource;

     private static final ObjectMapper objectMapper = new ObjectMapper();


    public NHAIController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection[] connection = new Connection[1];


            private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // private Key secretKey2 = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private boolean isValidJwtToken(String authorizationHeader, Key secretKey, String username) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7); // Remove "Bearer " prefix
            try {
                    Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

                    String subject = claims.getSubject();
                    Date expirationDate = claims.getExpiration();
                    Date currentDate = new Date();

                    // Check if the subject matches the expected value
                    if (username.equals(subject) && !expirationDate.before(currentDate)) 
                    {
                        return true; 
                    }// Token is valid
                    // Add your JWT validation logic here
                    // Return true if the token is valid; otherwise, return false
                } 
                catch (Exception e) 
                {
                    // Token validation failed
                    return false;
                }
        }
        return false; // Token is missing or not in the correct format
    }


    
    private static String generateJwtToken(Key secretKey,String username) {
        // Define the token expiration time (e.g., 1 hour from now)
        long expirationMillis = System.currentTimeMillis() + 1800000; // 30 mins in milliseconds

        // Build the JWT token with an expiration date
        return Jwts.builder()
                .setSubject(username) // You can set any subject or user identifier
                .setExpiration(new Date(expirationMillis))
                .signWith(secretKey)
                .compact();
    }


        @PostMapping("/login")
    public String processInput(@RequestBody InputRequest input) throws JsonProcessingException 
    {
        islogin[0] = false;
        username = input.getUsername();
        String passEntered = input.getStringValue();
        String response="";
        final String correctPass = "H";
        LoginResponse loginResponse = new LoginResponse();
        if(passEntered.equals(correctPass)&& username.equals("Hardik"))
        {
            islogin[0] = true;
            try{
                    if (connection[0] == null || connection[0].isClosed()) 
                    {
                        connection[0] = dataSource.getConnection();
                    }
                }
            catch(Exception e)
                {
                    response = "Error: " + e.getMessage();
                }

                loginResponse.setJwtToken(generateJwtToken(secretKey, username));
                loginResponse.setMessage("Login Successful");
        }
        else
        {
            loginResponse.setMessage("Invalid Credentials");
        }

        response = objectMapper.writeValueAsString(loginResponse);

        return response;
    }


    @PostMapping("/tollLogin")
    public String tollLogin(
            @RequestBody TollRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        tollID = input.getTollID();
        String response="";
        response = NHAIService.tollInp(tollID,state,tollPlazaName,connection);
        return response;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

    @GetMapping("/transactionT")
    public String transactionT(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String jsonResponse=null;
        if(islogin[0]==true)
        {
            List<TransactionResponseT> responses = new ArrayList<>(); 
            responses = NHAIService.transactionT(connection,tollID);

                jsonResponse = objectMapper.writeValueAsString(responses);
        }
        else
        {
            jsonResponse = "Please login first";
        }
        return jsonResponse;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

    // This is the code for generating PDF and show it in the browser
    @GetMapping("/createPdfT")
    public ResponseEntity<InputStreamResource> createPdfT(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) throws IOException, DocumentException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        ByteArrayOutputStream pdf_ = NHAIService.createPdfT(NHAIService.transactionT(connection,tollID),tollID,tollPlazaName,state);
        ByteArrayInputStream pdf = new ByteArrayInputStream(pdf_.toByteArray());

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition","inline; filename=transactions.pdf");
        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(pdf));
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/createCsvT")
    public ResponseEntity<String> createCsvT(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
    List<TransactionResponseT> transactions = NHAIService.transactionT(connection, tollID);
    String csvContent = NHAIService.generateCsvContentT(transactions);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=transactions.csv");

    return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.TEXT_PLAIN)
            .body(csvContent);
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    
    @GetMapping("/createExcelT")
    public ResponseEntity<byte[]> createExcelT(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required".getBytes());
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        List<TransactionResponseT> transactions = NHAIService.transactionT(connection, tollID);
        byte[] excelContent = NHAIService.generateExcelContentT(transactions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=transactions.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
            } else {
                // Invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token".getBytes());
            }
    }

        @GetMapping("/deleteTollOTP")
    public String deleteTollOTP(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String response="";
        String nhaiMail = "hardik21555@gmail.com";
        otpToDelete = NHAIService.deleteTollOTP(connection,nhaiMail,tollID);
        if(otpToDelete>=100000)
        {
            response = "OTP sent successfully!";
        }
        else
        {
            response = "Error in sending OTP";
        }
        return response;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

    @PostMapping("/deleteTollOTPVerification")
    public String deleteTollOTPVerification(
            @RequestBody OtpRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        int otpEntered = input.getIntValue();
        String response;

        if(otpEntered==otpToDelete)
        {
            response = "OTP verified successfully!";
        }
        else
        {
            response = "Invalid OTP";
        }
        
        return response;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

    @DeleteMapping("/deleteToll")
    public String deleteToll(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String response="";
        response = NHAIService.deleteToll(tollID,connection);
        return response;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }


    //  vehicle transaction details

    @PostMapping("/vehicleLogin")
    public String vehicleLogin(
            @RequestBody VehicleRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        vehicleNumber = input.getVehicleNumber();

        String response="";
        response = NHAIService.vehicleLogin(vehicleNumber, connection);        
        return response;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

    @GetMapping("/transactionV")
    public String transactionV(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String jsonResponse = null;

        if(islogin[0]==true)
        {
            List<TransactionResponseV> responses = new ArrayList<>();
            responses = NHAIService.transactionV(vehicleNumber, connection);
            try 
            {
                jsonResponse = objectMapper.writeValueAsString(responses);
            } catch (JsonProcessingException e) 
            {
                e.printStackTrace();
            }
        }
        else
        {
            jsonResponse = "Please login first";
        }
        return jsonResponse;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

    @GetMapping("/createPdfV")
        public ResponseEntity<InputStreamResource> createPdfV(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) throws IOException, DocumentException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        ByteArrayOutputStream pdf_ = NHAIService.createPdfV(NHAIService.transactionV(vehicleNumber, connection),vehicleNumber);
        ByteArrayInputStream pdf = new ByteArrayInputStream(pdf_.toByteArray());

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition","inline; filename=transactions.pdf");
        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(pdf));
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/createCsvV")
        public ResponseEntity<String> createCsvV(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
    List<TransactionResponseV> transactions = NHAIService.transactionV(vehicleNumber,connection);
    String csvContent = NHAIService.generateCsvContentV(transactions);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=transactions.csv");

    return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.TEXT_PLAIN)
            .body(csvContent);
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

    }

    @GetMapping("/createExcelV")
        public ResponseEntity<byte[]> createExcelV(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required".getBytes());
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        List<TransactionResponseV> transactions = NHAIService.transactionV(vehicleNumber, connection);
        byte[] excelContent = NHAIService.generateExcelContentV(transactions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=transactions.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
                            } else {
                // Invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token".getBytes());
            }

    }

    @PostMapping("/newTollMail")
    public String vehicleLogin(
            @RequestBody NewTollMailRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        newMail = input.getEmail();
        
        String response = null;
        // AddTollResponse response = null;

        if(islogin[0]==true)
        {
            otpRandom = NHAIService.sendOTP(newMail,connection);
            if(otpRandom!=-1)
            {
                response = "OTP sent successfully! ";
            }
            else
            {
                response = "Error in sending OTP";
            }
            // response = NHAIService.addToll(newState,newTollPlazaName,newPass,newEmail,connection);
        }
        else
        {
            response = "Please login first";
            // response = new AddTollResponse(-1,"Please login first");
        }
        return response;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }


    @PostMapping("/newTollMailOTPVerification")
    public String newTollMailOTPVerification(
            @RequestBody OtpRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        int otpEntered = input.getIntValue();
        String response;

        if(otpEntered==otpRandom && otpEntered!=0)
        {
            response = "OTP verified successfully! ";
            newMailVerified = true;
        }
        else
        {
            response = "Invalid OTP";
        }
        
        return response;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }


    @PostMapping("/addToll")
    public AddTollResponse addToll(
            @RequestBody AddTollRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        AddTollResponse response = null;
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            response = new AddTollResponse(-1,"Access denied. Authentication Required");
            // return "Access denied. Authentication Required";
            return response;
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        state[0] = input.getState();
        tollPlazaName[0] = input.getTollPlaza();
        String newPass = input.getPassword();
        int single[] = input.getSingle();
        int round[] = input.getRound();
        

        if(islogin[0]==true)
        {
            if(newMailVerified==true)
            {
                response = NHAIService.addToll(state,tollPlazaName,newPass,newMail,single,round,connection);
                tollID = response.getId();
            }
            else
            {
                response = new AddTollResponse(-1,"Please verify email first");
            }
        }
        else
        {
            response = new AddTollResponse(-1,"Please login first");
        }
        }
        else
        {
            response = new AddTollResponse(-1,"Please login first");
            // return "Access denied. Authentication Required";
        }
        return response;
    }

    @GetMapping("/tollCharges")
    public String TollCharges(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String jsonResponse = null;
        // String response = null;
        List<tollChargesResponse> response = null;
        if(islogin[0]==true)
        {
            response = NHAIService.tollCharges(connection,tollID);
            try 
            {
                jsonResponse = objectMapper.writeValueAsString(response);
            } 
            catch (JsonProcessingException e) 
            {
                e.printStackTrace();
            }
        }
        else
        {
            jsonResponse = "Please login first";
        }
        return jsonResponse;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }
    
    @PutMapping("/updateToll")
    public String updateToll(
            @RequestBody UpdateTollRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String state = input.getState();
        String tollPlazaName = input.getTollPlaza();
        int single[] = input.getSingle();
        int round[] = input.getRound();
        
        String response = null; 
        String jsonresponse = null;

        if(islogin[0]==true)
        {

            response = NHAIService.updateToll(tollID,state,tollPlazaName,single,round,connection);
            jsonresponse = objectMapper.writeValueAsString(response);
        }
        else
        {
            response = "Please login first";
            jsonresponse = objectMapper.writeValueAsString(response);
        }
        return jsonresponse;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }


    @GetMapping("/createPdfCharges")
            public ResponseEntity<InputStreamResource> createPdfCharges(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) throws IOException, DocumentException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        ByteArrayOutputStream pdf_ = NHAIService.createPdfCharges(NHAIService.tollCharges(connection,tollID),tollID,tollPlazaName,state);
        ByteArrayInputStream pdf = new ByteArrayInputStream(pdf_.toByteArray());

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition","inline; filename=charges.pdf");
        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(pdf));
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/sendPdfCharges")
            public ResponseEntity<String> sendPdfCharges(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) throws IOException, DocumentException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
    try {
        ByteArrayOutputStream pdf_ = NHAIService.createPdfCharges(NHAIService.tollCharges(connection, tollID), tollID, tollPlazaName, state);
        byte[] pdfData = pdf_.toByteArray();

        // String to = 
        String from = "hardik23555@gmail.com";
        String subject = "Toll Registed Successfully";
        String text = "Please refer to the attached PDF for the details";

        // Save the PDF as a temporary file
        File tempFile = File.createTempFile("charges_", ".pdf");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(pdfData);
        fos.close();

        // Send the email with the PDF attachment
        SendMail sm = new SendMail();
        sm.sendEmailWithAttachment(newMail, from, subject, text, tempFile);

        // Delete the temporary file after sending
        tempFile.delete();

        return ResponseEntity.ok("Email sent with PDF attachment.");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email with PDF attachment.");
    }
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @GetMapping("/transactionN")
    public String transactions(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String jsonResponse = null;
        List<TransactionResponse> response = null;
        if(islogin[0]==true)
        {
            response = NHAIService.transaction(connection);
            jsonResponse = objectMapper.writeValueAsString(response);
        }
        else
        {
            jsonResponse = "Please login first";
            // response = new TransactionResponse(-1,"Please login first");
        }
        return jsonResponse;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

        @GetMapping("/transactionList")
    public String transactionsList(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String jsonResponse = null;
        List<TransactionResponse> response = null;
        if(islogin[0]==true)
        {
            response = NHAIService.transaction(connection);

            jsonResponse = objectMapper.writeValueAsString(response);
        }
        else
        {

            jsonResponse = "Please login first";
        }
        return jsonResponse;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }


        @GetMapping("/createPdfN")
        public ResponseEntity<InputStreamResource> createPdfN(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) throws IOException, DocumentException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        ByteArrayOutputStream pdf_ = NHAIService.createPdf(NHAIService.transaction(connection));
        ByteArrayInputStream pdf = new ByteArrayInputStream(pdf_.toByteArray());

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition","inline; filename=transactions.pdf");
        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(pdf));
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/createCsvN")
            public ResponseEntity<String> createCsvN(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
    List<TransactionResponse> transactions = NHAIService.transaction(connection);
    String csvContent = NHAIService.generateCsv(transactions);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=transactions.csv");

    return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.TEXT_PLAIN)
            .body(csvContent);
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

    }

    @GetMapping("/createExcelN")
            public ResponseEntity<byte[]> createExcelN(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required".getBytes());
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        List<TransactionResponse> transactions = NHAIService.transaction(connection);
        byte[] excelContent = NHAIService.generateExcel(transactions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=transactions.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
            } else {
                // Invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token".getBytes());
            }
            
    }

        @GetMapping("/tollList")
    public String tollList(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String jsonResponse = null;
        List<TollListResponse> response = null;
        if(islogin[0]==true)
        {
            response = NHAIService.tollList(connection);
            jsonResponse = objectMapper.writeValueAsString(response);
        }
        else
        {
            jsonResponse = "Please login first";
            // response = new TransactionResponse(-1,"Please login first");
        }
        return jsonResponse;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

    
        @GetMapping("/tollListPdf")
        public ResponseEntity<InputStreamResource> tollListPdf(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) throws IOException, DocumentException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        ByteArrayOutputStream pdf_ = NHAIService.createTollListPdf(NHAIService.tollList(connection));
        ByteArrayInputStream pdf = new ByteArrayInputStream(pdf_.toByteArray());

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition","inline; filename=Tolls.pdf");
        return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(pdf));
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

        @GetMapping("/tollListCsv")
                public ResponseEntity<String> TollListCsv(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
    List<TollListResponse> tolls = NHAIService.tollList(connection);
    String csvContent = NHAIService.createTollListCsv(tolls);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=Tolls.csv");

    return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.TEXT_PLAIN)
            .body(csvContent);
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

        @GetMapping("/tollListExcel")
                public ResponseEntity<byte[]> tollListExcel(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required".getBytes());
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        List<TollListResponse> tolls = NHAIService.tollList(connection);
        byte[] excelContent = NHAIService.createTollListExcel(tolls);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Tolls.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
            } else {
                // Invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token".getBytes());
            }
    }

            @GetMapping("/chargesListCsv")
                public ResponseEntity<String> chargesListCsv(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
    List<ChargesListResponse> charges = NHAIService.chargesList(connection);
    String csvContent = NHAIService.chargesListCsv(charges);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=Charges.csv");

    return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.TEXT_PLAIN)
            .body(csvContent);
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

            @GetMapping("/chargesListExcel")
                public ResponseEntity<byte[]> chargesListExcel(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required".getBytes());
    }

    if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        List<ChargesListResponse> charges = NHAIService.chargesList(connection);
        byte[] excelContent = NHAIService.chargesListExcel(charges);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Charges.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
            } else {
                // Invalid token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token".getBytes());
            }
    }

    
    @DeleteMapping("/logout")
    public String logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, username)) {
        String response="";
        try 
        {
            if (connection[0] != null && !connection[0].isClosed()) 
            {
                connection[0].close();
                connection[0] = null; // Set the connection to null after closing
            }
            islogin[0] = false;
            isloginForgot[0] = false;
            response =  "Logged out successfully.";
        } 
        catch (SQLException e) 
        {
            response =  "Error: " + e.getMessage();
        }
        return response;
        }
        else
        {
            return "Access denied. Authentication Required";
        }
    }

}


