document.addEventListener("DOMContentLoaded", function() {

    const jwtToken = localStorage.getItem("jwtToken");

    // Define the API URL
    const apiUrl = 'http://localhost:8080/NHAI/tollList';
    
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
                const tbody = document.getElementById('tollData');
                
                // Loop through the received data and create table rows
                data.forEach(transaction => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${transaction.id}</td>
                        <td>${transaction.state}</td>
                        <td>${transaction.tollName}</td>
                        <td>${transaction.mailId}</td>
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
        // Redirect to index.html when the button is clicked
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
        window.location.href = `loginN.html`;
    })
    .catch(error => {
        console.error('Error:', error); // Handle any errors
        const errorMessage = encodeURIComponent(error);
        window.location.href = `loginN.html`;
    });
    
    });
    
    
    
    // function displayPDF() {
    //     const pdfUrl = 'http://localhost:8080/NHAI/tollListPdf';
        
    //     window.location.href = pdfUrl;
    // }
    
    // // Attach the displayPDF function to a button click event
    // const downloadPdf = document.getElementById('view');
    // downloadPdf.addEventListener('click', displayPDF);
    
    
    // This will directly download the PDF file
    // download.addEventListener('click', function() {
    //     fetch('http://localhost:8080/NHAI/tollListPdf', {
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
    //         a.download = 'Tolls.pdf'; // Specify the desired file name
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
    fetch('http://localhost:8080/NHAI/tollListPdf', {
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
        a.download = 'TollList.pdf'; // Specify the desired file name
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url); // Clean up the URL object
    })
    .catch(error => {
        console.error('An error occurred:', error);
    });
});
    
    
    // function downloadCsvFile() {
    //     const csvUrl = 'http://localhost:8080/NHAI/tollListCsv';
    //     window.location.href = csvUrl;
    // }
    
    // const downloadCsv = document.getElementById('downloadCsv');
    // downloadCsv.addEventListener('click', downloadCsvFile);

    const downloadCsv = () => {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", "http://localhost:8080/NHAI/tollListCsv", true);
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
                a.download = "TollList.csv";
    
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
    //     const ExcelUrl = 'http://localhost:8080/NHAI/tollListExcel';
    //     window.location.href = ExcelUrl;
    // }
    
    // const downloadExcel = document.getElementById('downloadExcel');
    // downloadExcel.addEventListener('click', downloadExcelFile);


        // Function to send a GET request to the server with the JWT token
        const downloadExcel = () => {
            const xhr = new XMLHttpRequest();
            xhr.open("GET", "http://localhost:8080/NHAI/tollListExcel", true);
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
                    a.download = "TollList.xlsx";
    
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
// -------------------------------------

    // function downloadChargesCsvFile() {
    //     const csvUrl = 'http://localhost:8080/NHAI/chargesListCsv';
    //     window.location.href = csvUrl;
    // }
    
    // const chargesCsv = document.getElementById('chargesCSV');
    // chargesCsv.addEventListener('click', downloadChargesCsvFile);

    const chargesCsv = () => {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", "http://localhost:8080/NHAI/chargesListCsv", true);
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
                a.download = "AllTollCharges.csv";
    
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
    const chargesCsvButton = document.getElementById("chargesCSV");
    chargesCsvButton.addEventListener("click", chargesCsv);
    





    
    // function downloadChargesExcelFile() {
    //     const ExcelUrl = 'http://localhost:8080/NHAI/chargesListExcel';
    //     window.location.href = ExcelUrl;
    // }
    
    // const chargesExcel = document.getElementById('chargesExcel');
    // chargesExcel.addEventListener('click', downloadChargesExcelFile);

    // });

            // Function to send a GET request to the server with the JWT token
            const chargesExcel = () => {
                const xhr = new XMLHttpRequest();
                xhr.open("GET", "http://localhost:8080/NHAI/chargesListExcel", true);
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
                        a.download = "AllTollCharges.xlsx";
        
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
            const downloadChargesExcelButton = document.getElementById("chargesEXCEL");
            downloadChargesExcelButton.addEventListener("click", chargesExcel);

    
        });