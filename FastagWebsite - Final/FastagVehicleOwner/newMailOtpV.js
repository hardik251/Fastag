document.addEventListener("DOMContentLoaded", function () {
    const jwtToken = localStorage.getItem("jwtToken");

    const SubmitNewEmailOtpForm = document.getElementById("SubmitNewEmailOtpForm");
    const newOtpMessageContainer = document.getElementById("newOtpMessageContainer");
    
    SubmitNewEmailOtpForm.addEventListener("submit", function (e) {
        e.preventDefault();
    
        const newOTP = document.getElementById("otpN").value;
    
        const data = {
            intValue: newOTP
        };
    
        const jsonData = JSON.stringify(data);
    
        const apiUrl = "http://localhost:8080/vehicleOwner/updateNewEmailOTP";
    
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
            // Display the response message
            newOtpMessageContainer.innerHTML = "<p>" + responseText + "</p>";
        })
        .catch((error) => {
            console.error("Error:", error);
            // Display the error message
            newOtpMessageContainer.innerHTML = "<p>Error: An error occurred while processing your request.</p>";
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

