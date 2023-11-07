document.addEventListener("DOMContentLoaded", function () {

    const jwtToken = localStorage.getItem("jwtToken");


    const UpdateNewMailForm = document.getElementById("UpdateNewMailForm");
    const messageContainer = document.getElementById("messageContainer");
    
    UpdateNewMailForm.addEventListener("submit", function (e) {
        e.preventDefault();
    
        const newEmail = document.getElementById("newEmail").value;
    
        // Display "Sending OTP" message
        messageContainer.innerHTML = "<p>Sending OTP</p>";
    
        const data = {
            newMailId: newEmail
        };
    
        const jsonData = JSON.stringify(data);
    
        const apiUrl = "http://localhost:8080/vehicleOwner/updateEnterNewEmail";
    
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
            messageContainer.innerHTML = "<p>" + responseText + "</p>";
            if(responseText == "OTP sent successfully! ") {
                window.location.href = "newMailOtp.html";
            }
        })
        .catch((error) => {
            console.error("Error:", error);
            // Display the error message
            messageContainer.innerHTML = "<p>Error: An error occurred while processing your request.</p>";
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


