package com.example.vehicleowner;

public class FastagVehicleOwnerRequestResponse {
    
}


class LoginResponse{
    private String vehicleNumber;
    private String mailId;
    private String vehicleType;
    private int accountBalance;
    private String jwtToken;
    
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    public String getMailId() {
        return mailId;
    }
    public void setMailId(String mailId) {
        this.mailId = mailId;
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
    public String getJwtToken() {
        return jwtToken;
    }
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}

class ForgoPasswordOTPResponse{
    private String message;
    private String jwtToken;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getJwtToken() {
        return jwtToken;
    }
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}

class TransactionResponse {
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
    private String vehicleNumber;
    private String stringValue;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

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

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getConfirmNewPass() {
        return confirmNewPass;
    }

    public void setConfirmNewPass(String confirmNewPass) {
        this.confirmNewPass = confirmNewPass;
    }
}

class ForgotPass {
    private String mailId;
    private String vehicleNumber;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    
    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    } 
}

class NewMail {
    private String newMailId;

    public String getNewMailId() {
        return newMailId;
    }

    public void setNewMailId(String newMailId) {
        this.newMailId = newMailId;
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