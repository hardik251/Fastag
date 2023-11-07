document.addEventListener("DOMContentLoaded", function() {

    const jwtToken = localStorage.getItem("jwtToken");

    
    // Define the API URL
    const apiUrl = 'http://localhost:8080/NHAI/tollCharges';
    
    
    // Function to fetch and display transaction data
    function fetchTollChargesData() {

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
                const tbody = document.getElementById('chargesData');
                
                // Loop through the received data and create table rows
                data.forEach(charge => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${charge.category}</td>
                        <td>${charge.single}</td>
                        <td>${charge.round}</td>
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
    fetchTollChargesData();
    
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
        console.error('Error:', error); // Handle any errors
        const errorMessage = encodeURIComponent(error);
        window.location.href = `loginN.html`;
    });
    
    });
    
    
    
    // function displayPDF() {
    //     const pdfUrl = 'http://localhost:8080/NHAI/createPdfCharges';
        
    //     window.location.href = pdfUrl;
    // }
    
    // const downloadPdf = document.getElementById('view');
    // downloadPdf.addEventListener('click', displayPDF);
    
    
    // This will directly download the PDF file
    // download.addEventListener('click', function() {
    //     fetch('http://localhost:8080/NHAI/createPdfCharges', {
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
    //         a.download = 'charges.pdf'; // Specify the desired file name
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
    fetch('http://localhost:8080/NHAI/createPdfCharges', {
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
        a.download = 'Charges.pdf'; // Specify the desired file name
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url); // Clean up the URL object
    })
    .catch(error => {
        console.error('An error occurred:', error);
    });
});


    function sendPDF() {
        const apiUrl = 'http://localhost:8080/NHAI/sendPdfCharges';
    
        // Function to fetch and display PDF data
        function fetchPdfData() {
            // Define the headers for the request, including the JWT token with Bearer prefix
            const headers = {
                'Authorization': `Bearer ${jwtToken}`
            };
    
            // Make a GET request to the API with the specified headers
            fetch(apiUrl, {
                method: 'GET',
                headers: headers
            })
                .then(response => {
                    // Check if the response status is OK (200)
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    // Parse the response as text
                    return response.text();
                })
                .then(data => {
                    // Print the response in the console
                    console.log(data);
                })
                .catch(error => {
                    // Handle any errors that occurred during the fetch
                    console.error('Fetch error:', error);
                });
        }
    
        // Call the function to fetch and display PDF data
        fetchPdfData();
    }
    

    const sendPdfButton = document.getElementById('send');
    sendPdfButton.addEventListener('click', sendPDF);
    
    
    });
    
    