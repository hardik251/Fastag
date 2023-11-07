document.addEventListener("DOMContentLoaded", function () {

    const jwtToken = localStorage.getItem("jwtToken");


    const RequestOtpForm = document.getElementById("RequestOtpForm");
    const requestOtpMessageContainer = document.getElementById("requestOtpMessageContainer");
    
    RequestOtpForm.addEventListener("submit", function (e) {
        e.preventDefault();
    
        // Display "Sending OTP" message
        requestOtpMessageContainer.innerHTML = "<p>Sending OTP</p>";
    
        // Define the URL for the GET request
        const apiUrl = "http://localhost:8080/vehicleOwner/updateEmail";
    
        // Make the GET request
        fetch(apiUrl, {
            method: "GET",
            headers: {
                Authorization: `Bearer ${jwtToken}`,
            },
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.text(); // Read the response body as text
        })
        .then(data => {
            console.log("Response:", data); // Print the response in the console
            // Display the response message as a success message
            requestOtpMessageContainer.innerHTML = "<p class='success'>" + data + "</p>";
        })
        .catch(error => {
            console.error("Error:", error); // Handle any errors
            // Display the error message
            requestOtpMessageContainer.innerHTML = "<p class='error'>Error: An error occurred while processing your request.</p>";
        });
    });
    

    const SubmitOtpForm = document.getElementById("SubmitOtpForm");
    const otpMessageContainer = document.getElementById("otpMessageContainer");
    
    SubmitOtpForm.addEventListener("submit", function (e) {
        e.preventDefault();
    
        const otp = document.getElementById("otp").value;
    
        // Display "Sending OTP" message
        otpMessageContainer.innerHTML = "<p>Sending OTP</p>";
    
        const data = {
            intValue: otp
        };
    
        const jsonData = JSON.stringify(data);
    
        const apiUrl = "http://localhost:8080/vehicleOwner/updateEmailOTP";
    
        fetch(apiUrl, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${jwtToken}`,
                "Content-Type": "application/json"
            },
            body: jsonData
        })
        .then((response) => response.text())
        .then((responseText) => {
            // Display the response message as a success message
            otpMessageContainer.innerHTML = "<p class='success'>" + responseText + "</p>";
            if(responseText == "OTP verification successful!"){
                window.location.href = "newEmailV.html";
            }
        })
        .catch((error) => {
            console.error("Error:", error); // Handle any errors
            // Display the error message
            otpMessageContainer.innerHTML = "<p class='error'>Error: An error occurred while processing your request.</p>";
        });
    });

    const logout = document.getElementById('logout');

logout.addEventListener('click', function() {

    localStorage.removeItem("jwtToken");
    localStorage.removeItem("vehicleNumber");
    localStorage.removeItem("emailID");
    localStorage.removeItem("vehicleType");
    localStorage.removeItem("accountBalance");

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
    };
    
    fetch('http://localhost:8080/vehicleOwner/logout', {
    method: 'DELETE',
    headers: headers
})
.then(response => {
    if (!response.ok) {
        throw new Error('Network response was not ok');
    }
    return response.text(); 
})
.then(data => {
    console.log('Response:', data);
    const dataMessage = encodeURIComponent(data);
    window.location.href = `loginV.html`;
})
.catch(error => {
    console.error('Error:', error);
    const errorMessage = encodeURIComponent(error);
    window.location.href = `loginV.html`;
});

});
    
});

