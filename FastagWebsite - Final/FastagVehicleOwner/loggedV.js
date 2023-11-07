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

    // Get references to the buttons
    const redirectToTransactionButton = document.getElementById('redirectToTransactionButton');
    const redirectToUpdateMail = document.getElementById('redirectToUpdateMail');

    
        // Add a click event listener to the button
        redirectToTransactionButton.addEventListener('click', function() {
            // Redirect to transactionV.html when the button is clicked
            window.location.href = 'transactionV.html';
        });
    // });

        // Add a click event listener to the button
        redirectToUpdateMail.addEventListener('click', function() {
            // Redirect to transaction.html when the button is clicked
            window.location.href = 'updateEmailV.html';
        });
    // });
    

// logout.addEventListener('click', function() {
//     // Redirect to index.html when the button is clicked

//     localStorage.removeItem("jwtToken");
//     localStorage.removeItem("vehicleNumber");
//     localStorage.removeItem("emailID");
//     localStorage.removeItem("vehicleType");
//     localStorage.removeItem("accountBalance");

//     fetch('http://localhost:8080/vehicleOwner/logout', {
//     method: 'DELETE',
//     headers: {
//         'Content-Type': 'application/json', // Set the appropriate content type if needed
//     },
// })
// .then(response => {
//     if (!response.ok) {
//         throw new Error('Network response was not ok');
//     }
//     return response.text(); // Assuming the response is a string
// })
// .then(data => {
//     console.log('Response:', data); // Print the response to the console
//     const dataMessage = encodeURIComponent(data);
//     window.location.href = `loginV.html`;
// })
// .catch(error => {
//     console.error('Error:', error); // Handle any errors
//     const errorMessage = encodeURIComponent(error);
//     window.location.href = `loginV.html`;
// });

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
