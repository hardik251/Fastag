package com.example.fastag;

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
@RequestMapping("/fastag")
public class FastagController 
{
    int tollID;
    String [] tollPlazaName = new String[1];
    // String tollPlazaName = null;
    String [] state = new String[1];
    // String state = null;
    int [] otpRandom = new int[1];
    boolean [] islogin = new boolean[1];
    // islogin[0] = false;
    // boolean islogin = false;
    boolean [] isloginForgot = new boolean[1];
    // boolean isloginForgot = false;


    
    private DataSource dataSource;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public FastagController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection[] connection = new Connection[1];


        private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // private Key secretKey2 = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private boolean isValidJwtToken(String authorizationHeader, Key secretKey, int tollID) {
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
                    if (String.valueOf(tollID).equals(subject) && !expirationDate.before(currentDate)) 
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
        islogin[0] = false;
        tollID = input.getIntValue();
        String passEntered = input.getStringValue();
        String response="";
        response = FastagService.login(secretKey,islogin,response,passEntered,state,tollPlazaName,tollID,connection,dataSource);
        return response;
    }


    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestBody ForgotPass input) 
    {
        isloginForgot[0] = false;
        tollID = input.getIntValue();
        String enteredMailID = input.getStringValue();
        String response="";
        response = FastagService.forgotPassword(response,enteredMailID,tollID,connection,otpRandom,dataSource);
        return response;
    }


    @PostMapping("/forgotPasswordOTP")
    public String forgotPasswordOTP(@RequestBody OtpRequest input) throws JsonProcessingException
    {
        int otpEntered = input.getIntValue();
        String response;

        ForgoPasswordOTPResponse forgoPasswordOTPResponse = new ForgoPasswordOTPResponse();
        if(otpEntered==otpRandom[0])
        {
            // response = "OTP verified successfully! ";
            forgoPasswordOTPResponse.setMessage("OTP verified successfully!");
            // response = "OTP verified successfully! ";
            forgoPasswordOTPResponse.setJwtToken(FastagService.generateJwtToken(secretKey, tollID));
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


    @PostMapping("/forgotPasswordUpdatePass")
    public String forgotPasswordUpdatePass(
            @RequestBody ForgotPassUpdate input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
        String newPass = input.getnewPass();
        String confirmNewPass = input.getconfirmNewPass();
        String response="";

        if(isloginForgot[0]==true)
        {
            response = FastagService.updatePass(response,newPass,confirmNewPass,tollID,connection);
        }
        else
        {
            response = "Please login first";
        }
        isloginForgot[0] = false;
        return response;
        } else {
            // Invalid token
            return "Invalid token";
        }
    }


        @GetMapping("/tollCharges")
    public ResponseEntity<String> getTollCharges(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
        }

        if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
        String jsonResponse = null;
        List<tollChargesResponse> response = null;
        if(islogin[0]==true)
        {
            response = FastagService.tollCharges(connection,tollID);
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
        return ResponseEntity.ok(jsonResponse);
    } else {
        // Invalid token
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
    }

    @GetMapping("/chargesPdf")
    public ResponseEntity<InputStreamResource> chargesPdf(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) throws IOException, DocumentException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
        ByteArrayOutputStream pdf_ = FastagService.createPdfCharges(FastagService.tollCharges(connection,tollID),tollID,tollPlazaName,state);
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

    @PostMapping("/vehicleEntry")
    public String vehicleEntry(
            @RequestBody InpRequest input,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
        String vehicle_number = input.getStringValue();
        String LightColor = null;
        String response = null;
        
        if(islogin[0]==true)
        {
            response = FastagService.vehicleEntr(response, vehicle_number, tollID, LightColor,tollPlazaName, state, connection);
        }
        else
        {
            response = "Please login first";
        }
        return response;
        } else {
            // Invalid token
            return "Invalid token";
        }
    }

    @GetMapping("/transaction")
    public ResponseEntity<String> getTransaction(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
        }

        String jsonResponse=null;

        if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
        if(islogin[0]==true)
        {
            List<TransactionResponse> responses = new ArrayList<>();
            responses = FastagService.transaction(connection, tollID);
            try 
            {
                jsonResponse = objectMapper.writeValueAsString(responses);
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
        return ResponseEntity.ok(jsonResponse);
    } else {
        // Invalid token
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
    }

    @GetMapping("/createPdf")
    public ResponseEntity<InputStreamResource> createPdf(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) throws IOException, DocumentException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
        ByteArrayOutputStream pdf_ = FastagService.createPdf(FastagService.transaction(connection,tollID),tollID,tollPlazaName,state);
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

    @GetMapping("/createCsv")
    public ResponseEntity<String> createCsv(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required");
    }

    if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
    List<TransactionResponse> transactions = FastagService.transaction(connection, tollID);
    String csvContent = FastagService.generateCsvContent(transactions);

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

    
    @GetMapping("/createExcel")
    public ResponseEntity<byte[]> createExcel(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        // User didn't send a valid JWT token in the header
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Authentication Required".getBytes());
    }

    if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
        List<TransactionResponse> transactions = FastagService.transaction(connection, tollID);
        byte[] excelContent = FastagService.generateExcelContent(transactions);

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


    @DeleteMapping("/logout")
    public String logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ) throws JsonProcessingException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // User didn't send a valid JWT token in the header
            return "Access denied. Authentication Required";
        }

        if (isValidJwtToken(authorizationHeader, secretKey, tollID)) {
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


