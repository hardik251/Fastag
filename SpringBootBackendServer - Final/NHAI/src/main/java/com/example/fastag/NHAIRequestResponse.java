package com.example.fastag;

public class NHAIRequestResponse {
    
}

class TransactionResponseT {
    private int transactionId;
    private java.sql.Timestamp dateTime;
    private String vehicleNumber;
    private String vehicleType;
    private int charges;
    private String journeyType;
    
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public java.sql.Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(java.sql.Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }

    public String getJourneyType() {
        return journeyType;
    }

    public void setJourneyType(String journeyType) {
        this.journeyType = journeyType;
    }
}

class TransactionResponse{
    int transactionID;
    java.sql.Timestamp dateTime;
    int tollID;
    String tollPlaza;
    String state;
    String journeyType;
    int charges;
    int closingAccountBalance;
    String vehicleNumber;
    String vehicleType;

    public int getClosingAccountBalance() {
        return closingAccountBalance;
    }
    public void setClosingAccountBalance(int closingAccountBalance) {
        this.closingAccountBalance = closingAccountBalance;
    }

    public int getTransactionID() {
        return transactionID;
    }
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }
    public java.sql.Timestamp getDateTime() {
        return dateTime;
    }
    public void setDateTime(java.sql.Timestamp dateTime) {
        this.dateTime = dateTime;
    }
    public int getTollID() {
        return tollID;
    }
    public void setTollID(int tollID) {
        this.tollID = tollID;
    }
    public String getTollPlaza() {
        return tollPlaza;
    }
    public void setTollPlaza(String tollPlaza) {
        this.tollPlaza = tollPlaza;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getJourneyType() {
        return journeyType;
    }
    public void setJourneyType(String journeyType) {
        this.journeyType = journeyType;
    }
    public int getCharges() {
        return charges;
    }
    public void setCharges(int charges) {
        this.charges = charges;
    }
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

}


class TollListResponse{
    int id;
    String state;
    String tollName;
    String mailId;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getTollName() {
        return tollName;
    }
    public void setTollName(String tollName) {
        this.tollName = tollName;
    }
    public String getMailId() {
        return mailId;
    }
    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    
}


class ChargesListResponse{
    int id;
    int categoryID;
    int single;
    int round;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCategoryID() {
        return categoryID;
    }
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
    public int getSingle() {
        return single;
    }
    public void setSingle(int single) {
        this.single = single;
    }
    public int getRound() {
        return round;
    }
    public void setRound(int round) {
        this.round = round;
    }




}

class tollChargesResponse{
    int tollID;
    String category;
    int single;
    int round;

    public int getTollID() {
        return tollID;
    }
    public void setTollID(int tollID) {
        this.tollID = tollID;
    }

    public String getCategory() {
    return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    
    public int getSingle() {
        return single;
    }
    public void setSingle(int single) {
        this.single = single;
    }
    
    public int getRound() {
        return round;
    }
    public void setRound(int round) {
        this.round = round;
    }

    
}

class TransactionResponseV {
    private int transactionId;
    private String dateTime;
    private String tollPlaza;
    private String state;
    private String journeyType;
    private int charges;
    private int closingAccountBalance;

    
    public int getClosingAccountBalance() {
        return closingAccountBalance;
    }

    public void setClosingAccountBalance(int closingAccountBalance) {
        this.closingAccountBalance = closingAccountBalance;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTollPlaza() {
        return tollPlaza;
    }

    public void setTollPlaza(String tollPlaza) {
        this.tollPlaza = tollPlaza;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }

    public String getJourneyType() {
        return journeyType;
    }

    public void setJourneyType(String journeyType) {
        this.journeyType = journeyType;
    }
}

class OtpRequest {
    private int intValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    
}


class InputRequest {
    // private int intValue;
    private String username;
    private String stringValue;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}

class VehicleLoginResponse{
    private String vehicleNumber;
    private String emailId;
    private String vehicleType;
    private int accountBalance;
    
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    public String getEmailId() {
        return emailId;
    }
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public int getAccountBalance() {
        return accountBalance;
    }
    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }
}


class TollInfo {
    private String state;
    private String tollPlazaName;
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getTollPlazaName() {
        return tollPlazaName;
    }
    public void setTollPlazaName(String tollPlazaName) {
        this.tollPlazaName = tollPlazaName;
    }

}


class LoginResponse{
    private String jwtToken;
    private String message;
    
    public String getJwtToken() {
        return jwtToken;
    }
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

class transactionRequestT {
    private int tollID;

    public int getTollID() {
        return tollID;
    }

    public void setTollID(int tollID) {
        this.tollID = tollID;
    }

}

class transactionRequestV {
    private String vehicleNumber;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

}

class TollRequest{
    private int tollID;

    public int getTollID() {
        return tollID;
    }

    public void setTollID(int tollID) {
        this.tollID = tollID;
    }

}

class UpdateTollRequest{
    private String state;
    private String tollPlaza;
    int single[] = new int[7];
    int round[] = new int[7];

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getTollPlaza() {
        return tollPlaza;
    }
    public void setTollPlaza(String tollPlaza) {
        this.tollPlaza = tollPlaza;
    }

    public int[] getSingle() {
        return single;
    }
    public void setSingle(int[] single) {
        this.single = single;
    }
    public int[] getRound() {
        return round;
    }
    public void setRound(int[] round) {
        this.round = round;
    }
}

class AddTollRequest{
    private String state;
    private String tollPlaza;
    private String password;
    int single[] = new int[7];
    int round[] = new int[7];


    public int[] getSingle() {
        return single;
    }
    public void setSingle(int[] single) {
        this.single = single;
    }
    // private String email;

    public int[] getRound() {
        return round;
    }
    public void setRound(int[] round) {
        this.round = round;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    
    public String getTollPlaza() {
        return tollPlaza;
    }
    public void setTollPlaza(String tollPlaza) {
        this.tollPlaza = tollPlaza;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    // public String getEmail() {
    //     return email;
    // }
    // public void setEmail(String email) {
    //     this.email = email;
    // }
 
}


class AddTollResponse{
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String message;

        public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

        public AddTollResponse(int id, String message) {
        this.id = id;
        this.message = message;
    }
 
}

class NewTollMailRequest{
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class VehicleRequest{
    private String vehicleNumber;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber= vehicleNumber;
    }

}


class InpRequest {
    private String stringValue;
    
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}

class ForgotPassUpdate {
    private String newPass;
    private String confirmNewPass;

    public String getnewPass() {
        return newPass;
    }

    public void setnewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getconfirmNewPass() {
        return confirmNewPass;
    }

    public void setconfirmNewPass(String confirmNewPass) {
        this.confirmNewPass = confirmNewPass;
    }
}


class ForgotPass {
    private String stringValue;
    private int intValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    } 
}


class MessageResponse {
    private String message;

    // constructor
    public MessageResponse(String message) {
        this.message = message;
    }

    // getter and setter methods
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}







