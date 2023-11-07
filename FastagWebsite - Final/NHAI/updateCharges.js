document.addEventListener("DOMContentLoaded", function() {
    const stateTollContainer = document.getElementById('stateTollContainer');
    
    // Extract the response data from the query parameters (assuming it's passed in the URL)
    const urlParams = new URLSearchParams(window.location.search);
    // const tollId = urlParams.get('tollId');
    // const state = urlParams.get('state');
    // const tollPlaza = urlParams.get('tollPlaza');
    const tollId = localStorage.getItem("updatetollId");
    const state = localStorage.getItem("updatestate");
    const tollPlaza = localStorage.getItem("updatetollPlaza");

    const jwtToken = localStorage.getItem("jwtToken");
    
    // // Display the response data
    // stateTollContainer.innerHTML = `
    //     <p>State: ${state}</p>
    //     <p>Toll Plaza: ${tollPlaza}</p>
    // `;

    const tollIdInput = document.querySelector(`input[name="tollId"]`);
    const stateInput = document.querySelector(`input[name="state"]`);
    const tollPlazaInput = document.querySelector(`input[name="tollPlazaName"]`);

    tollIdInput.value = tollId;
    stateInput.value = state;
    tollPlazaInput.value = tollPlaza;

const tollForm = document.getElementById('tollForm');


  // Extract the default single and round charges from query parameters
  const defaultSingleValues = [];
  const defaultRoundValues = [];

  for (let i = 1; i <= 7; i++) {
      const defaultSingle = localStorage.getItem(`single${i}`);
      const defaultRound = localStorage.getItem(`round${i}`);

      defaultSingleValues.push(defaultSingle);
      defaultRoundValues.push(defaultRound);
  }

  // Set the default values in the form inputs
  for (let i = 1; i <= 7; i++) {
      const singleInput = document.querySelector(`input[name="single${i}"]`);
      const roundInput = document.querySelector(`input[name="round${i}"]`);

      singleInput.value = defaultSingleValues[i - 1];
      roundInput.value = defaultRoundValues[i - 1];
  }


tollForm.addEventListener('submit', function(e) {
    e.preventDefault();


    // Get the values from the form inputs
    const state = document.getElementById('state').value;
    const tollPlaza = document.getElementById('tollPlazaName').value;
    const singleValues = [];
    const roundValues = [];

    // Loop through the category and charges inputs and collect the values
    for (let i = 1; i <= 7; i++) {
        // const category = document.querySelector(`input[name="category${i}"]`).value;
        const single = document.querySelector(`input[name="single${i}"]`).value;
        const round = document.querySelector(`input[name="round${i}"]`).value;

        singleValues.push(Number(single));
        roundValues.push(Number(round));
    }

    // Create the data object to send in the POST request
    const data = {
        state: state,
        tollPlaza: tollPlaza,
        // password: password,
        single: singleValues,
        round: roundValues
    };

    // Convert the data to JSON format
    const jsonData = JSON.stringify(data);

    // Define the URL for the API
    const apiUrl = "http://localhost:8080/NHAI/updateToll";

    // Define the headers for the request
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
    };

    // Make a POST request
    fetch(apiUrl, {
        method: "PUT",
        headers: headers,
        body: jsonData
    })
        .then(response => response.json())
        .then(responseData => {
            // Handle the response if needed
            console.log(responseData);
            alert(responseData);
            // window.location.href = `addSuccess.html?id=${responseData.id}&message=${responseData['message']}`;
        })
        .catch(error => {
            console.error("Error:", error);
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