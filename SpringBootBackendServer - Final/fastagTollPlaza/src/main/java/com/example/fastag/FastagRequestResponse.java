package com.example.fastag;

public class FastagRequestResponse {
    
}

class LoginResponse{
    private String jwtToken;
    private String state;
    private String tollPlaza;

    public String getJwtToken() {
        return jwtToken;
    }
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
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

class tollChargesResponse{
    private int tollID;
    private String category;
    private int single;
    private int round;

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


class TransactionResponse {
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
    private int intValue;
    private String stringValue;

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

class InpRequest {
    private String stringValue;
    
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}

class VehicleEntryResponse{
    private String light;
    private String vehicleType;
    private String journeyType;
    private int charges;
    private int accountBalance;
    private String alert;
    
    public String getLight() {
        return light;
    }
    public void setLight(String light) {
        this.light = light;
    }
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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
    public int getAccountBalance() {
        return accountBalance;
    }
    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }
    public String getAlert() {
        return alert;
    }
    public void setAlert(String alert) {
        this.alert = alert;
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







