document.addEventListener("DOMContentLoaded", function() {
    const responseContainer = document.getElementById('responseContainer');

 
    // const urlParams = new URLSearchParams(window.location.search);
    // const state = urlParams.get('state');
    // const tollPlaza = urlParams.get('tollPlaza');
    const state = localStorage.getItem('state');
    const tollPlaza = localStorage.getItem('tollPlaza');


    responseContainer.innerHTML = `
        <p>State: ${state}</p>
        <p>Toll Plaza: ${tollPlaza}</p>
    `;

    let single = [];
    let round = [];
    

    const apiUrl = 'http://localhost:8080/fastag/tollCharges';
    
    
    const jwtToken = localStorage.getItem('jwtToken');
 
    function fetchTollChargesData() {

        fetch(apiUrl, {
            method: "GET",
            headers: {
                Authorization: `Bearer ${jwtToken}`,
            },
        })
            .then(response => {
                // Check if the response status is OK (200)
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                // Parse the response as JSON
                return response.json();
            })
            .then(data => {
                // Get a reference to the table body
                const tbody = document.getElementById('chargesData');
                
                // Loop through the received data and create table rows
                data.forEach(charge => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${charge.category}</td>
                        <td>${charge.single}</td>
                        <td>${charge.round}</td>
                    `;
                    single.push(charge.single);
                    round.push(charge.round);
                    tbody.appendChild(row);
                });
            })
            .catch(error => {
                // Handle any errors that occurred during the fetch
                console.error('Fetch error:', error);
            });
    }
    
    // Call the function to fetch and display transaction data
    fetchTollChargesData();

    // Get references to the buttons
    const transactionHistoryButton = document.getElementById('transactionHistoryButton');
    const startTollButton = document.getElementById('startTollButton');
    const logout = document.getElementById('logout');

        // Add a click event listener to the button
        startTollButton.addEventListener('click', function() {
            // Redirect to vehicle.html when the button is clicked
            window.location.href = 'vehicle.html';
        });
    // });
    

        transactionHistoryButton.addEventListener('click', function() {
            // Redirect to transaction.html when the button is clicked
            window.location.href = 'transaction.html';
        });

const download = document.getElementById('download');
// This will directly download the PDF file
download.addEventListener('click', function() {
    fetch('http://localhost:8080/fastag/chargesPdf', {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${jwtToken}`,
        },
        responseType: 'blob', // Specify that the response should be treated as binary data
    })
    .then(response => {
        if (response.status === 200) {
            return response.blob(); // Convert response to a Blob
        } else {
            console.error('GET request failed');
        }
    })
    .then(blob => {
        // Create a download link for the Blob
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'charges.pdf'; // Specify the desired file name
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url); // Clean up the URL object
    })
    .catch(error => {
        console.error('An error occurred:', error);
    });
});



logout.addEventListener('click', function() {


    localStorage.removeItem("state");
    localStorage.removeItem("tollPlaza");

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
    };

    fetch('http://localhost:8080/fastag/logout', {
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
    localStorage.removeItem("jwtToken");
    window.location.href = `login.html`;
})
.catch(error => {
    console.error('Error:', error); 
    const errorMessage = encodeURIComponent(error);
    localStorage.removeItem("jwtToken");
    window.location.href = `login.html`;
});

});


});