// Function to submit the vehicle number
document.getElementById('vehicleEntryForm').addEventListener('submit', function (e) {
  e.preventDefault(); // Prevent form submission for demonstration purposes
  const vehicleNumber = document.getElementById('vehicleNumber').value;

  // Create the JSON payload
  const payload = {
    stringValue: vehicleNumber
  };

  // Define the URL
  const url = 'http://localhost:8080/fastag/vehicleEntry';

  // Get the JWT token from localStorage
  const jwtToken = localStorage.getItem("jwtToken");

  // Define the request options
  const requestOptions = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${jwtToken}` // Add the Authorization header with the JWT token
    },
    body: JSON.stringify(payload),
  };

  // Make the POST request
  fetch(url, requestOptions)
    .then(response => response.json())
    .then(data => {
      if (data.message === "Invalid Vehicle Number") {
        console.log(data);
        alert("Invalid Vehicle Number");
      } else if (data.message === "Server error") {
        console.log(data);
        alert("Server error");
      } else {
        console.log(data);
        // Display the received JSON response in the responseContainer div

        // Update the light status circle color based on the response
        const lightStatus = document.getElementById('lightStatus');
        var audio = new Audio('ting2.mp3');

        if (data.light === 'Green') {
          lightStatus.style.backgroundColor = 'green';
          audio.play();
        } else {
          lightStatus.style.backgroundColor = 'red';
        }

        const responseContainer = document.getElementById('responseContainer');
        if (data.light === 'Green') {

          if(data.alert === null) {
        responseContainer.innerHTML = `

          <p>Thank You!</p>
          <p>Happy Journey!</p>
          <p>Vehicle Type: ${data['vehicleType']}</p>
          <p>Journey Type: ${data['journeyType']}</p>
          <p>Charges: ${data.charges}</p>
          <p>Account Balance: ${data['accountBalance']}</p>
        `;
          }
          else {
            responseContainer.innerHTML = `
            <p>Thank You!</p>
            <p>Happy Journey!</p>
            <p>Vehicle Type: ${data['vehicleType']}</p>
            <p>Journey Type: ${data['journeyType']}</p>
            <p>Charges: ${data.charges}</p>
            <p>Account Balance: ${data['accountBalance']}</p>
            <p class="alert-orange">Alert: ${data['alert']}</p>
          `;
            }

      }
      else {
        responseContainer.innerHTML = `
          <p class="alert-red">${data['alert']}</p>
        `;
      }




        // After 10 seconds, change the light color back to red
        setTimeout(function () {
          lightStatus.style.backgroundColor = 'red';
        }, 10000); // 10000 milliseconds = 10 seconds
      }
    })
    .catch(error => {
      // Handle any errors that occurred during the request
      console.error('Error:', error);
    });


  
  const logout = document.getElementById('logout');

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