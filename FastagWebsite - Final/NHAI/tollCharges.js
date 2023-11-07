document.addEventListener("DOMContentLoaded", function() {
    const responseContainer = document.getElementById('responseContainer');
    
    // const urlParams = new URLSearchParams(window.location.search);
    // const state = urlParams.get('state');
    // const tollPlaza = urlParams.get('tollPlaza');
    // const tollId = urlParams.get('tollID');
    const tollId = localStorage.getItem("tollId");
    const state = localStorage.getItem("state");
    const tollPlaza = localStorage.getItem("tollPlaza");

    const jwtToken = localStorage.getItem("jwtToken");
    
    // Display the response data
    responseContainer.innerHTML = `
        <p>Toll ID: ${tollId}</p>
        <p>State: ${state}</p>
        <p>Toll Plaza: ${tollPlaza}</p>
    `;

    let single = [];
    let round = [];
    
    // Define the API URL
    const apiUrl = 'http://localhost:8080/NHAI/tollCharges';

    // Function to fetch and display transaction data
    function fetchTollChargesData() {
        // Define the headers for the request, including the JWT token with Bearer prefix
        const headers = {
            "Authorization": `Bearer ${jwtToken}`
        };
    
        // Make a GET request to the API with the specified headers
        fetch(apiUrl, {
            method: "GET",
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
    

    function getOTP() {

        const headers = {
            "Authorization": `Bearer ${jwtToken}`
        };


        fetch(`http://localhost:8080/NHAI/deleteTollOTP`, {
            method: "GET",
            headers: headers
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to send OTP');
                }
                return response.text(); // Assuming the response is a string
            })
            .then(data => {
                console.log('Response:', data); // Print the response to the console
                if (data.includes('OTP sent successfully!')) {
                    // Prompt the user to enter OTP
                    const otpEntered = prompt('Enter the OTP sent to your email:');
                    if (otpEntered !== null) {
                        // User entered an OTP
                        verifyOTP(otpEntered);
                    } else {
                        alert('OTP entry canceled.');
                    }
                } else {
                    alert('Error in sending OTP');
                }
            })
            .catch(error => {
                console.error('Error:', error); // Handle any errors
                alert(error);
            });
    }

    function verifyOTP(otpEntered) {
        // Define the URL for the API
        const apiUrl = 'http://localhost:8080/NHAI/deleteTollOTPVerification';
    
        // Define the headers for the request, including the JWT token with Bearer prefix
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        };
    
        // Create the request body
        const requestBody = JSON.stringify({ intValue: otpEntered });
    
        // Make a POST request to the API with the specified headers and body
        fetch(apiUrl, {
            method: 'POST',
            headers: headers,
            body: requestBody
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('OTP verification failed');
                }
                return response.text(); // Assuming the response is a string
            })
            .then(data => {
                console.log('Response:', data); // Print the response to the console
                if (data.includes('OTP verified successfully!')) {
                    // Proceed to delete operation
                    deleteToll();
                } else {
                    alert('Invalid OTP');
                }
            })
            .catch(error => {
                console.error('Error:', error); // Handle any errors
                alert(error);
            });
    }
    

    function deleteToll() {
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

        // Define the URL for the API
        const apiUrl = 'http://localhost:8080/NHAI/deleteToll';
    
        // Define the headers for the request, including the JWT token with Bearer prefix
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        };
    
        // Make a DELETE request to the API with the specified headers
        fetch(apiUrl, {
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
                alert(data);
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error);
            });
    }
    




const deleteTheToll = document.getElementById('deleteToll');

deleteTheToll.addEventListener('click', function () {
    const confirmed = confirm(`Are you sure you want to delete Toll ID ${tollId}? This action is irreversible.`);
    if (confirmed) 
    {
        const tollIdToDelete = prompt('Confirm the Toll ID:');
        if (tollIdToDelete === tollId) 
        {
            alert(" Sending OTP to your email. Please enter the OTP to confirm deletion.")
                getOTP();
        }
        else
        {
            alert('Invalid Toll ID. Toll Deletion Cancelled');
        }
    }
    else
    {
        alert('Toll Deletion Cancelled');
    }

});


    // ---------------
    
    const logout = document.getElementById('logout');
    
    logout.addEventListener('click', function() {

        localStorage.removeItem("jwtToken");
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
        // Define the URL for the API
        const apiUrl = 'http://localhost:8080/NHAI/logout';
    
        // Define the headers for the request, including the JWT token with Bearer prefix
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        };
    
        // Make a DELETE request to the API with the specified headers
        fetch(apiUrl, {
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
    

    function updateCharges() {
        localStorage.setItem("updatetollId", tollId);
        localStorage.setItem("updatestate", state);
        localStorage.setItem("updatetollPlaza", tollPlaza);
        localStorage.setItem("single1", single[0]);
        localStorage.setItem("round1", round[0]);   
        localStorage.setItem("single2", single[1]);
        localStorage.setItem("round2", round[1]);
        localStorage.setItem("single3", single[2]);
        localStorage.setItem("round3", round[2]);
        localStorage.setItem("single4", single[3]);
        localStorage.setItem("round4", round[3]);
        localStorage.setItem("single5", single[4]);
        localStorage.setItem("round5", round[4]);
        localStorage.setItem("single6", single[5]);
        localStorage.setItem("round6", round[5]);
        localStorage.setItem("single7", single[6]);
        localStorage.setItem("round7", round[6]);

        // const queryString = `?single1=${single[0]}&round1=${round[0]}&single2=${single[1]}&round2=${round[1]}&single3=${single[2]}&round3=${round[2]}&single4=${single[3]}&round4=${round[3]}&single5=${single[4]}&round5=${round[4]}&single6=${single[5]}&round6=${round[5]}&single7=${single[6]}&round7=${round[6]}`;
        window.location.href = `updateCharges.html`;
    }

    const update = document.getElementById('update');
    update.addEventListener('click', updateCharges);



    
    
    
    // function displayPDF() {
    //     const pdfUrl = 'http://localhost:8080/NHAI/createPdfCharges';
        
    //     window.location.href = pdfUrl;
    // }
    
    // // Attach the displayPDF function to a button click event
    // const viewPdf = document.getElementById('view');
    // viewPdf.addEventListener('click', displayPDF);
    
    
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
    
    
    });
    
    