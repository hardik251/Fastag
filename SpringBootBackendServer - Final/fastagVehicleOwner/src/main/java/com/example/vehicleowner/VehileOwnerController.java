package com.example.vehicleowner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.sql.DataSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;


@RestController
@RequestMapping("/vehicleOwner")
public class VehileOwnerController {
    String vehicleNumber;
    // String tollPlazaName = null;
    // String state = null;
    int [] otpRandom = {0,0,0};
    String [] decryptedMailID = {null};
    boolean [] islogin = new boolean[1];
    // boolean islogin = false;
    boolean [] isloginForgot = new boolean[1];
    // boolean isloginForgot = false;
    boolean [] correctOTP = new boolean[1];
    String newMail = null;

    private DataSource dataSource;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public VehileOwnerController(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    private Connection[] connection = new Connection[1];

    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // private Key secretKey2 = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private boolean isValidJwtToken(String authorizationHeader, Key secretKey, String vehicleNumber) {
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
                    if (vehicleNumber.equals(subject) && !expirationDate.before(currentDate)) 
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



        @PostMapping("/login")
    public String login(@RequestBody InputRequest input) 
    {
        vehicleNumber = input.getVehicleNumber();
        String passEntered = input.getStringValue();
        String response="";
        // String decryptedMailID = "";

        System.out.println("Hello111");
        response = VehicleOwnerService.login(secretKey,islogin,response, vehicleNumber, passEntered, decryptedMailID, dataSource, connection);        
        return response;
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestBody ForgotPass input) 
    {
        isloginForgot[0] = false;
        vehicleNumber = input.getVehicleNumber();
        String enteredMailID = input.getMailId();

        String response="";

        response = VehicleOwnerService.forgoPassword(response, vehicleNumber, enteredMailID, otpRandom, dataSource, connection);  
        return response;
    }

    @PostMapping("/forgotPasswordOTP")
    public String forgotPasswordOTP(@RequestBody OtpRequest input) throws JsonProcessingException 
    {
        int otpEntered = input.getIntValue();
        String response;

        ForgoPasswordOTPResponse forgoPasswordOTPResponse = new ForgoPasswordOTPResponse();
        if(otpEntered==otpRandom[0]&&otpEntered!=0)
        {
            forgoPasswordOTPResponse.setMessage("OTP verified successfully!");
            // response = "OTP verified successfully! ";
            forgoPasswordOTPResponse.setJwtToken(VehicleOwnerService.generateJwtToken(secretKey, vehicleNumber));
            // String jwtToken = VehicleOwnerService.generateJwtToken(secretKey2, vehicleNumber);
            isloginForgot[0] = true;
        }
        else
        {
            forgoPasswordOTPResponse.setMessage("Invalid OTP");
            // response = "Invalid OTP";
        }

        response = objectMapper.writeValueAsString(forgoPasswordOTPResponse);
        
        return response;
    }

    // @PostMapping("/forgotPasswordUpdatePass")
    // public String forgotPasswordUpdatePass(@RequestBody ForgotPassUpdate input)
    // {
    //     String newPass = input.getNewPass();
    //     String confirmNewPass = input.getConfirmNewPass();
    //     String response;
    //     if(isloginForgot[0]==true)
    //     {
    //         try
    //         {
    //             response = VehicleOwnerService.updatePass(isloginForgot,newPass,confirmNewPass,vehicleNumber,connection);

    //         }
    //         catch(Exception e)
    //         {
    //             response = "Error"+e.getMessage();
    //             System.exit(-1);
    //         }

    //         try 
    //         {
    //             connection[0].close();
    //         } 
    //         catch (SQLException e) 
    //         {
    //             e.printStackTrace();
    //         }
    //     }
    //     else
    //     {
    //         response = "Please login first";
    //     }

    //     return response;
    // }

    @PostMapping("/forgotPasswordUpdatePass")
    public String forgotPasswordUpdatePass(
            @RequestBody ForgotPassUpdate input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            String newPass = input.getNewPass();
            String confirmNewPass = input.getConfirmNewPass();
            String response;

            if (isloginForgot[0]) {
                try {
                    response = VehicleOwnerService.updatePass(isloginForgot, newPass, confirmNewPass, vehicleNumber, connection);

                } catch (Exception e) {
                    response = "Error" + e.getMessage();
                    System.exit(-1);
                }

                try {
                    connection[0].close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                response = "Please login first";
            }

            return response;
        } else {
            // Invalid token
            return "Invalid token";
        }
    }


    // @GetMapping("/transaction")
    // public String getTransaction(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) 
    // {
    //     String jsonResponse = null;


    //     if (isValidJwtToken(authorizationHeader,secretKey,vehicleNumber)) 
    //     {
    //         if(islogin[0]==true)
    //         {
    //             List<TransactionResponse> responses = new ArrayList<>();
    //             responses = VehicleOwnerService.transaction(vehicleNumber, connection);
    //             try 
    //             {
    //                 jsonResponse = objectMapper.writeValueAsString(responses);
    //             } catch (JsonProcessingException e) 
    //             {
    //                 e.printStackTrace();
    //             }

    //         }
    //         else
    //         {
    //             jsonResponse = "Please login first";
    //         }
    //         return jsonResponse;
    //     }
    //     else
    //     {
    //         return "Invalid token";
    //     }
    // }

    @GetMapping("/transaction")
    public ResponseEntity<String> getTransaction(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
        }

        String jsonResponse;

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            if (islogin[0]) {
                List<TransactionResponse> responses = new ArrayList<>();
                responses = VehicleOwnerService.transaction(vehicleNumber, connection);
                try {
                    jsonResponse = objectMapper.writeValueAsString(responses);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    jsonResponse = "An error occurred while processing the response.";
                }
                return ResponseEntity.ok(jsonResponse);
            } else {
                jsonResponse = "Please login first";
                return ResponseEntity.ok(jsonResponse);
            }
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


    // // This is the code for generating PDF and show it in the browser
    // @GetMapping("/createPdf")
    // public ResponseEntity<InputStreamResource> createPdf() throws IOException, DocumentException {
    //     ByteArrayOutputStream pdf_ = VehicleOwnerService.createPdf(VehicleOwnerService.transaction(vehicleNumber, connection),vehicleNumber);
    //     ByteArrayInputStream pdf = new ByteArrayInputStream(pdf_.toByteArray());

    //     HttpHeaders headers = new HttpHeaders();

    //     headers.add("Content-Disposition","inline; filename=transactions.pdf");
    //     return ResponseEntity
    //         .ok()
    //         .headers(headers)
    //         .contentType(MediaType.APPLICATION_PDF)
    //         .body(new InputStreamResource(pdf));
    // }

    @GetMapping("/createPdf")
    public ResponseEntity<InputStreamResource> createPdf(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws IOException, DocumentException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            String jsonResponse;
            List<TransactionResponse> responses = VehicleOwnerService.transaction(vehicleNumber, connection);

            try {
                ByteArrayOutputStream pdf_ = VehicleOwnerService.createPdf(responses, vehicleNumber);
                ByteArrayInputStream pdf = new ByteArrayInputStream(pdf_.toByteArray());

                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "inline; filename=transactions.pdf");

                return ResponseEntity
                        .ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(pdf));
            } catch (Exception e) {
                // Handle PDF generation or other exceptions
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // @GetMapping("/createCsv")
    // public ResponseEntity<String> createCsv() {
    // List<TransactionResponse> transactions = VehicleOwnerService.transaction(vehicleNumber, connection);
    // String csvContent = VehicleOwnerService.generateCsvContent(transactions);

    // HttpHeaders headers = new HttpHeaders();
    // headers.add("Content-Disposition", "attachment; filename=transactions.csv");

    // return ResponseEntity
    //         .ok()
    //         .headers(headers)
    //         .contentType(MediaType.TEXT_PLAIN)
    //         .body(csvContent);
    // }

    @GetMapping("/createCsv")
    public ResponseEntity<String> createCsv(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
        }

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            List<TransactionResponse> transactions = VehicleOwnerService.transaction(vehicleNumber, connection);
            String csvContent = VehicleOwnerService.generateCsvContent(transactions);

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

    // @GetMapping("/createExcel")
    // public ResponseEntity<byte[]> createExcel() {
    //     List<TransactionResponse> transactions = VehicleOwnerService.transaction(vehicleNumber, connection);
    //     byte[] excelContent = VehicleOwnerService.generateExcelContent(transactions);

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.add("Content-Disposition", "attachment; filename=transactions.xlsx");

    //     return ResponseEntity
    //             .ok()
    //             .headers(headers)
    //             .contentType(MediaType.APPLICATION_OCTET_STREAM)
    //             .body(excelContent);
    // }

    @GetMapping("/createExcel")
    public ResponseEntity<byte[]> createExcel(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required".getBytes());
        }

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            List<TransactionResponse> transactions = VehicleOwnerService.transaction(vehicleNumber, connection);
            byte[] excelContent = VehicleOwnerService.generateExcelContent(transactions);

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


    // @GetMapping("/updateEmail")
    // public String getUpdateEmail()
    // {
    //     String response = null;
    //     if(islogin[0]==true)
    //     {
    //         System.out.println("Hello1");
    //         response = VehicleOwnerService.updateMail(otpRandom,vehicleNumber,decryptedMailID);
    //     }
    //     else
    //     {
    //         response =  "Please login first";
    //     }
    //     return response;
    // }

    @GetMapping("/updateEmail")
    public String getUpdateEmail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            String response = null;

            if (islogin[0]) {
                System.out.println("Hello1");
                response = VehicleOwnerService.updateMail(otpRandom, vehicleNumber, decryptedMailID);
            } else {
                response = "Please login first";
            }
            return response;
        } else {
            // Invalid token
            return "Invalid token";
        }
    }


    // @PostMapping("/updateEmailOTP")
    // public String updateEmailOTP(@RequestBody OtpRequest input) 
    // {
    //     correctOTP[0] = false;
    //     int otpEntered = input.getIntValue();
    //     String response=null;
    //     if(islogin[0]==true)
    //     {
    //         if(otpEntered==otpRandom[1])
    //         {
    //             correctOTP[0] = true;
    //             System.out.println("original: "+correctOTP[0]);
    //             response = "OTP verification successful! ";
    //         }
    //         else
    //         {
    //             islogin[0] = false;
    //             response = "Invalid OTP";
    //         }
    //     }
    //     else
    //     {
    //         response =  "Please login first";
    //     }
        
    //     return response;
    // }

    @PostMapping("/updateEmailOTP")
    public String updateEmailOTP(
            @RequestBody OtpRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            correctOTP[0] = false;
            int otpEntered = input.getIntValue();
            String response = null;

            if (islogin[0]) {
                if (otpEntered == otpRandom[1]) {
                    correctOTP[0] = true;
                    System.out.println("original: " + correctOTP[0]);
                    response = "OTP verification successful!";
                } else {
                    islogin[0] = false;
                    response = "Invalid OTP";
                }
            } else {
                response = "Please login first";
            }
            return response;
        } else {
            // Invalid token
            return "Invalid token";
        }
    }


    // @PostMapping("/updateEnterNewEmail")
    // public String updateEnterNewEmail(@RequestBody NewMail input) 
    // {
    //     String response=null;
    //     System.out.println("after: "+correctOTP[0]);
    //     if(correctOTP[0]==true)
    //     {
    //         newMail = input.getNewMailId();

    //         if(islogin[0]==true)
    //         {
    //             response = VehicleOwnerService.updateNewMail(response, vehicleNumber, newMail,otpRandom);
    //         }
    //         else
    //         {
    //             response = "Please login first";
    //         }
    //     }
    //     else
    //     {
    //         response = "Please enter correct OTP";
    //     }
    
    //     return response;
    // }

    @PostMapping("/updateEnterNewEmail")
    public String updateEnterNewEmail(
            @RequestBody NewMail input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            String response = null;

            System.out.println("after: " + correctOTP[0]);
            if (correctOTP[0]) {
                newMail = input.getNewMailId();

                if (islogin[0]) {
                    response = VehicleOwnerService.updateNewMail(response, vehicleNumber, newMail, otpRandom);
                } else {
                    response = "Please login first";
                }
            } else {
                response = "Please enter correct OTP";
            }

            return response;
        } else {
            // Invalid token
            return "Invalid token";
        }
    }

    // @PostMapping("/updateNewEmailOTP")
    // public String updateNewEmailOTP(@RequestBody OtpRequest input)
    // {

    //     int otpEntered = input.getIntValue();
    //     String response=null;

    //     if(islogin[0]==true)
    //     {
    //         response = VehicleOwnerService.updateNewOTP(islogin,response, vehicleNumber, newMail, otpEntered, otpRandom, connection);
    //     }
    //     else
    //     {
    //         response = ("Please login first");
    //     }
        
    //     return response;
    // }

    @PostMapping("/updateNewEmailOTP")
    public String updateNewEmailOTP(
            @RequestBody OtpRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
            String response = null;

            int otpEntered = input.getIntValue();

            if (islogin[0]) {
                response = VehicleOwnerService.updateNewOTP(islogin, response, vehicleNumber, newMail, otpEntered, otpRandom, connection);
            } else {
                response = "Please login first";
            }

            return response;
        } else {
            // Invalid token
            return "Invalid token";
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

        if (isValidJwtToken(authorizationHeader, secretKey, vehicleNumber)) {
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
