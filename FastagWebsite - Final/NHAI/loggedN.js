const view = document.getElementById('view');

view.addEventListener('click', function() {
    window.location.href = 'transactionN.html';

});

const jwtToken = localStorage.getItem("jwtToken");


document.getElementById('charges').addEventListener('click', function(e) {
    console.log("Form submitted")
    const tollId = document.getElementById('tollId').value;
    const data = {
        tollID: tollId
    };

        console.log(data);
        // Convert the data to JSON format
        const jsonData = JSON.stringify(data);

        console.log(jsonData);

        // Define the URL for the API
        const apiUrl = "http://localhost:8080/NHAI/tollLogin";

        // Define the headers for the request
        const headers = {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${jwtToken}`
        };

        fetch(apiUrl, {
            method: "POST",
            headers: headers,
            body: jsonData
        })
            .then(response => response.json())
            .then(responseData => {
                    console.log(responseData);
                    localStorage.setItem("tollId", tollId);
                    localStorage.setItem("state", responseData.state);
                    localStorage.setItem("tollPlaza", responseData['tollPlazaName']);
                        window.location.href = `tollCharges.html`;
            })
            .catch(error => {
                console.error("Error:", error);
            });
});


document.getElementById('toll').addEventListener('click', function(e) {
    console.log("Form submitted")
    const tollId = document.getElementById('tollId').value;
    const data = {
        tollID: tollId
    };

    console.log(data);
    const jsonData = JSON.stringify(data);

    console.log(jsonData);

    // Define the URL for the API
    const apiUrl = "http://localhost:8080/NHAI/tollLogin";

    // Define the headers for the request, including the JWT token with Bearer prefix
    const headers = {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${jwtToken}`
    };

    fetch(apiUrl, {
        method: "POST",
        headers: headers,
        body: jsonData
    })
    .then(response => response.json())
    .then(responseData => {
        localStorage.setItem("tollId", tollId);
        localStorage.setItem("state", responseData.state);
        localStorage.setItem("tollPlaza", responseData['tollPlazaName']);
        window.location.href = `transactionNT.html`;
    })
    .catch(error => {
        console.error("Error:", error);
    });
});

const vehicleForm = document.getElementById('vehicleForm');
const vehicle = document.getElementById('vehicle');


vehicleForm.addEventListener('submit', function(e) {
    e.preventDefault();

    const vehicleNumber = document.getElementById('vehicleNumber').value;
    const data = {
        "vehicleNumber": vehicleNumber
    };

    console.log(data);

    // Convert the data to JSON format
    const jsonData = JSON.stringify(data);
    console.log(jsonData);

    // Define the URL for the API
    const apiUrl = "http://localhost:8080/NHAI/vehicleLogin";

    // Define the headers for the request, including the JWT token with Bearer prefix
    const headers = {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${jwtToken}`
    };

    // Make the API POST request
    fetch(apiUrl, {
        method: "POST",
        headers: headers,
        body: jsonData
    })
    .then(response => response.json())
    .then(responseData => {
        localStorage.setItem("vehicleNumber", responseData['vehicleNumber']);
        localStorage.setItem("vehicleType", responseData['vehicleType']);
        localStorage.setItem("accountBalance", responseData['accountBalance']);
        localStorage.setItem("emailID", responseData['emailId']);
        window.location.href = `transactionNV.html`;
    })
    .catch(error => {
        // Handle any errors
        console.error("Error:", error);
    });
});

const addToll = document.getElementById('addToll');

addToll.addEventListener('click', function() {
    window.location.href = 'addToll.html';
});

const viewToll = document.getElementById('viewToll');

viewToll.addEventListener('click', function() {
    window.location.href = 'tollsList.html';
});




document.addEventListener("DOMContentLoaded", function() {

    const url2 = "http://localhost:8000/byVehicle";
    
    fetch(url2)
    .then(response => response.text())
    .then(html => {
        document.getElementById('chart1-container').innerHTML = html;
    })
    .catch(error => console.error(error));

    const url3 = "http://localhost:8000/byState";
    
    fetch(url3)
    .then(response => response.text())
    .then(html => {
        document.getElementById('chart2-container').innerHTML = html;
    })
    .catch(error => console.error(error));


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