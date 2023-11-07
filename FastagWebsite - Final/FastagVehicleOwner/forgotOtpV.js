document.addEventListener("DOMContentLoaded", function () {
    const otpForm = document.getElementById("otpForm");

    otpForm.addEventListener("submit", function (e) {
        e.preventDefault();

        const otp = document.getElementById("otp").value;

        const data = {
            intValue: otp 
        };

        const jsonData = JSON.stringify(data);

        const apiUrl = "http://localhost:8080/vehicleOwner/forgotPasswordOTP";

        const headers = {
            "Content-Type": "application/json",
        };


        fetch(apiUrl, {
            method: "POST",
            headers: headers,
            body: jsonData,
        })
        .then((response) => response.json())
        .then((data) => {
                localStorage.setItem("jwtToken", data.jwtToken);
                forgotOtpMessageContainer.innerHTML = "<p>" +data.message + "</p>";
                if(data.message == "OTP verified successfully!"){
                    window.location.href = "newPassV.html";
                }
            })
            .catch((error) => {
                console.error("Error:", error);
                forgotOtpMessageContainer.innerHTML = "<p>Error: An error occurred while processing your request.</p>";
            });
    });

});