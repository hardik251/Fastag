document.addEventListener("DOMContentLoaded", function () {
    const mailForm = document.getElementById("MailForm");
    const jwtToken = localStorage.getItem("jwtToken");

    mailForm.addEventListener("submit", function (e) {
        e.preventDefault();


        const email = document.getElementById("email").value;

        const data = {
            email: email
        };

        const jsonData = JSON.stringify(data);

        const apiUrl = "http://localhost:8080/NHAI/newTollMail";

        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        };

        
        messageContainer.innerHTML = "<p>" + "Sending OTP" + "</p>";

        fetch(apiUrl, {
            method: "POST",
            headers: headers,
            body: jsonData,
        })
            .then((response) => response.text())
            .then((responseText) => {

                messageContainer.innerHTML = "<p>" +responseText + "</p>";
                if(responseText == "OTP sent successfully! ") {
                    window.location.href = `verifyOtp.html`;
                }
            })
            .catch((error) => {
                // Handle any errors
                console.error("Error:", error);
                messageContainer.innerHTML = "<p>Error: An error occurred while processing your request.</p>";
            });
    });

    
const logout = document.getElementById('logout');

logout.addEventListener('click', function() {

    localStorage.removeItem("jwtToken");
    if (localStorage.getItem('newTollId') !== null) {
        localStorage.removeItem('newTollId');
    }
    // remove newTollmessage
    if (localStorage.getItem('newTollmessage') !== null) {
        localStorage.removeItem('newTollmessage');
    }

if (localStorage.getItem('tollId') !== null) {
    localStorage.removeItem('tollId');
}
// remove state
if (localStorage.getItem('state') !== null) {
    localStorage.removeItem('state');
}
// remove tollPlaza
if (localStorage.getItem('tollPlaza') !== null) {
    localStorage.removeItem('tollPlaza');
}
if (localStorage.getItem('vehicleNumber') !== null) {
    localStorage.removeItem('vehicleNumber');
}
if (localStorage.getItem('emailID') !== null) {
    localStorage.removeItem('emailID');
}
if (localStorage.getItem('vehicleType') !== null) {
    localStorage.removeItem('vehicleType');
}
if (localStorage.getItem('accountBalance') !== null) {
    localStorage.removeItem('accountBalance');
}

if (localStorage.getItem('updatetollId') !== null) {
    localStorage.removeItem('updatetollId');
}
if (localStorage.getItem('updatestate') !== null) {
    localStorage.removeItem('updatestate');
}
if (localStorage.getItem('updatetollPlaza') !== null) {
    localStorage.removeItem('updatetollPlaza');
}

if (localStorage.getItem('single1') !== null) {
    localStorage.removeItem('single1');
    localStorage.removeItem('single2');
    localStorage.removeItem('single3');
    localStorage.removeItem('single4');
    localStorage.removeItem('single5');
    localStorage.removeItem('single6');
    localStorage.removeItem('single7');
    localStorage.removeItem('round1');
    localStorage.removeItem('round2');
    localStorage.removeItem('round3');
    localStorage.removeItem('round4');
    localStorage.removeItem('round5');
    localStorage.removeItem('round6');
    localStorage.removeItem('round7');
}

        const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
    };

    fetch('http://localhost:8080/NHAI/logout', {
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
    window.location.href = `loginN.html`;
})
.catch(error => {
    console.error('Error:', error);
    const errorMessage = encodeURIComponent(error);
    window.location.href = `loginN.html`;
});

});

});