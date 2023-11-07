// Define the API URL
const apiUrl = 'http://localhost:8080/vehicleOwner/transaction';

const jwtToken = localStorage.getItem("jwtToken");


// Function to fetch and display transaction data
function fetchTransactionData() {
    // Make a GET request to the API
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


// function displayPDF() {

//     const pdfUrl = 'http://localhost:8080/vehicleOwner/createPdf';
    
//     window.location.href = pdfUrl;
// }

// // Attach the displayPDF function to a button click event
// const displayButton = document.getElementById('view');
// displayButton.addEventListener('click', displayPDF);




const download = document.getElementById('download');
// This will directly download the PDF file
download.addEventListener('click', function() {
    fetch('http://localhost:8080/vehicleOwner/createPdf', {
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
        a.download = 'transactions.pdf'; // Specify the desired file name
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url); // Clean up the URL object
    })
    .catch(error => {
        console.error('An error occurred:', error);
    });
});

// function downloadCSV() {
//     // const jwtToken = jwtToken; // Replace with your JWT token
//     const csvUrl = 'http://localhost:8080/vehicleOwner/createCsv';
  
//     window.location.href = csvUrl;
//   }
  
//   // Attach the downloadCsv function to the button click event
//   const downloadButton = document.getElementById('downloadCsv');
//   downloadButton.addEventListener('click', downloadCSV);

    
// Function to send a GET request to the server with the JWT token
const downloadCsv = () => {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:8080/vehicleOwner/createCsv", true);
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
            a.download = "transactions.csv";

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



//   function downloadEXCEL() {
//     // const jwtToken = jwtToken; // Replace with your JWT token
//     const csvUrl = 'http://localhost:8080/vehicleOwner/createExcel';
  
//     window.location.href = csvUrl;
//   }
  
//   // Attach the downloadCsv function to the button click event
//   const downloadExcel = document.getElementById('downloadExcel');
//   downloadExcel.addEventListener('click', downloadEXCEL);

    // Function to send a GET request to the server with the JWT token
    const downloadExcel = () => {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", "http://localhost:8080/vehicleOwner/createExcel", true);
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
                a.download = "transactions.xlsx";

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
