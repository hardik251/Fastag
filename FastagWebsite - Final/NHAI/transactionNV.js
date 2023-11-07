document.addEventListener("DOMContentLoaded", function() {
    // Get a reference to the response container
    const responseContainer = document.getElementById('responseContainer');

    // Extract the response data from the query parameters (assuming it's passed in the URL)
    // const urlParams = new URLSearchParams(window.location.search);
    // const vehicleNumber = urlParams.get('vehicleNumber');
    // const emailID = urlParams.get('emailID');
    // const vehicleType = urlParams.get('vehicleType');
    // const accountBalance = urlParams.get('accountBalance');
    const vehicleNumber = localStorage.getItem("vehicleNumber");
    const emailID = localStorage.getItem("emailID");
    const vehicleType = localStorage.getItem("vehicleType");
    const accountBalance = localStorage.getItem("accountBalance");

    const jwtToken = localStorage.getItem("jwtToken");

    // Display the response data
    responseContainer.innerHTML = `
        <p>Vehicle Number: ${vehicleNumber}</p>
        <p>Email ID: ${emailID}</p>
        <p>Vehicle Type: ${vehicleType}</p>
        <p>Account Balance: ${accountBalance}</p>

    `;

// Define the API URL
const apiUrl = 'http://localhost:8080/NHAI/transactionV';

// Function to fetch and display transaction data
function fetchTransactionData() {

    const headers = {
        'Authorization': `Bearer ${jwtToken}`
    };

    // Make a GET request to the API
        fetch(apiUrl, {
        method: 'GET',
        headers: headers
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
            const tbody = document.getElementById('transactionData');
            
            // Loop through the received data and create table rows
            data.forEach(transaction => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${transaction.transactionId}</td>
                    <td>${new Date(transaction.dateTime).toLocaleString()}</td>
                    <td>${transaction.tollPlaza}</td>
                    <td>${transaction.state}</td>
                    <td>${transaction.journeyType}</td>
                    <td>${transaction.charges}</td>
                    <td>${transaction.closingAccountBalance}</td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(error => {
            // Handle any errors that occurred during the fetch
            console.error('Fetch error:', error);
        });
}

// Call the function to fetch and display transaction data
fetchTransactionData();

const logout = document.getElementById('logout');

logout.addEventListener('click', function() {

    localStorage.removeItem("jwtToken");
    // remove vehicleNumber from localStorage
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
    return response.text(); // Assuming the response is a string
})
.then(data => {
    console.log('Response:', data); // Print the response to the console
    const dataMessage = encodeURIComponent(data);
    window.history.forward();
    window.location.href = `loginN.html`;
})
.catch(error => {
    console.error('Error:', error); // Handle any errors
    const errorMessage = encodeURIComponent(error);
    window.history.forward();
    window.location.href = `loginN.html`;
    
});

});


// const download = document.getElementById('download');

// function displayPDF() {
//     const pdfUrl = 'http://localhost:8080/NHAI/createPdfV';
    
//     window.location.href = pdfUrl;
// }


// const downloadButton = document.getElementById('view');
// downloadButton.addEventListener('click', displayPDF);


// This will directly download the PDF file
// download.addEventListener('click', function() {
//     fetch('http://localhost:8080/NHAI/createPdfV', {
//         method: 'GET',
//         responseType: 'blob', // Specify that the response should be treated as binary data
//     })
//     .then(response => {
//         if (response.status === 200) {
//             return response.blob(); // Convert response to a Blob
//         } else {
//             console.error('GET request failed');
//         }
//     })
//     .then(blob => {
//         // Create a download link for the Blob
//         const url = window.URL.createObjectURL(blob);
//         const a = document.createElement('a');
//         a.href = url;
//         a.download = 'transactions.pdf'; // Specify the desired file name
//         document.body.appendChild(a);
//         a.click();
//         window.URL.revokeObjectURL(url); // Clean up the URL object
//     })
//     .catch(error => {
//         console.error('An error occurred:', error);
//     });
// });

const download = document.getElementById('download');
// This will directly download the PDF file
download.addEventListener('click', function() {
    fetch('http://localhost:8080/NHAI/createPdfV', {
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
        a.download = 'VehicleTransactions.pdf'; // Specify the desired file name
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url); // Clean up the URL object
    })
    .catch(error => {
        console.error('An error occurred:', error);
    });
});

// function downloadCsvFile() {
//     const csvUrl = 'http://localhost:8080/NHAI/createCsvV';
//     window.location.href = csvUrl;
// }

// const downloadCsv = document.getElementById('downloadCsv');
// downloadCsv.addEventListener('click', downloadCsvFile);

const downloadCsv = () => {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:8080/NHAI/createCsvV", true);
    xhr.setRequestHeader("Authorization", `Bearer ${jwtToken}`);
    xhr.responseType = "blob"; // Expecting binary data (CSV file)
    
    xhr.onload = function () {
        if (xhr.status === 200) {
            // Create a blob URL for the CSV data
            const blob = new Blob([xhr.response], { type: "text/csv" });
            const url = window.URL.createObjectURL(blob);

            // Create a hidden <a> element to trigger the download
            const a = document.createElement("a");
            a.style.display = "none";
            a.href = url;
            a.download = "VehicleTransactions.csv";

            // Trigger the click event to start the download
            document.body.appendChild(a);
            a.click();

            // Cleanup
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } else {
            // Handle errors, e.g., display an error message
            console.error("Failed to download CSV:", xhr.status);
        }
    };

    xhr.send();
};

// Attach the downloadCsv function to the button click event
const downloadButton = document.getElementById("downloadCSV");
downloadButton.addEventListener("click", downloadCsv);


// function downloadExcelFile() {
//     const ExcelUrl = 'http://localhost:8080/NHAI/createExcelV';
//     window.location.href = ExcelUrl;
// }

// const downloadExcel = document.getElementById('downloadExcel');
// downloadExcel.addEventListener('click', downloadExcelFile);

const downloadExcel = () => {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:8080/NHAI/createExcelV", true);
    xhr.setRequestHeader("Authorization", `Bearer ${jwtToken}`);
    xhr.responseType = "blob"; // Expecting binary data (Excel file)

    xhr.onload = function () {
        if (xhr.status === 200) {
            // Create a blob URL for the Excel data
            const blob = new Blob([xhr.response], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
            const url = window.URL.createObjectURL(blob);

            // Create a hidden <a> element to trigger the download
            const a = document.createElement("a");
            a.style.display = "none";
            a.href = url;
            a.download = "VehicleTransactions.xlsx";

            // Trigger the click event to start the download
            document.body.appendChild(a);
            a.click();

            // Cleanup
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } else {
            // Handle errors, e.g., display an error message
            console.error("Failed to download Excel:", xhr.status);
        }
    };

    xhr.send();
};

// Attach the downloadExcel function to the button click event
const downloadExcelButton = document.getElementById("downloadEXCEL");
downloadExcelButton.addEventListener("click", downloadExcel);

});