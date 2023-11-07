
function generateCaptchaText() {
    const characters = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789';
    let captcha = '';
    for (let i = 0; i < 6; i++) {
        captcha += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return captcha;
}


// Function to generate a CAPTCHA image with obfuscated text
function generateCaptchaImage(text) {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const width = 150;
    const height = 50;

    // Set canvas dimensions
    canvas.width = width;
    canvas.height = height;

    // Customize CAPTCHA styling
    ctx.fillStyle = 'white';
    ctx.fillRect(0, 0, width, height);

    // Apply noise to the background
    for (let i = 0; i < 150; i++) {
        ctx.fillStyle = `rgba(0, 0, 0, ${Math.random() * 0.5})`;
        ctx.fillRect(Math.random() * width, Math.random() * height, 1, 1);
    }

    // Customize text styling
    ctx.font = 'bold 30px Arial';
    ctx.strokeStyle = 'rgba(0, 0, 0, 0.8)';
    ctx.lineWidth = 2;
    ctx.lineJoin = 'round';

    // Obfuscate the text by applying random transformations
    for (let i = 0; i < text.length; i++) {
        const x = 20 + i * 20 + Math.random() * 10 - 5;
        const y = 35 + Math.random() * 10 - 5;
        const rotation = Math.random() * 0.4 - 0.2; // Random rotation
        ctx.setTransform(1, rotation, -rotation, 1, x, y);
        ctx.strokeText(text.charAt(i), 0, 0);
    }

    return canvas.toDataURL(); // Convert canvas to data URL
}


// Function to update the CAPTCHA image and value
function updateCaptchaImage() {
    const captchaText = generateCaptchaText();
    const captchaImage = document.getElementById('captchaImage');
    const captchaValueInput = document.getElementById('captchaValue');
    
    captchaValueInput.value = captchaText; // Store the CAPTCHA value
    captchaImage.src = generateCaptchaImage(captchaText);
    document.getElementById('captchaInput').value = ''; // Clear the input field
}


// Initial CAPTCHA image loading
updateCaptchaImage();

// Refresh CAPTCHA button click event handler
document.getElementById('refresh').addEventListener('click', function () {
    updateCaptchaImage();
});


document.getElementById('loginForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const tollId = document.getElementById('tollId').value;
    const password = document.getElementById('password').value;
    const enteredCaptcha = document.getElementById('captchaInput').value;
    const generatedCaptcha = document.getElementById('captchaValue').value;
    const login = document.getElementById('login');

    if (enteredCaptcha === generatedCaptcha) {

        // Create the data object
        const data = {
            intValue: tollId,
            stringValue: password
        };

        // Convert the data to JSON format
        const jsonData = JSON.stringify(data);

        // Define the URL for the API
        const apiUrl = "http://localhost:8080/fastag/login";

        // Define the headers for the request
        const headers = {
            "Content-Type": "application/json"
        };

        // // Make the API POST request
        fetch(apiUrl, {
            method: "POST",
            headers: headers,
            body: jsonData
        })
            .then(response => response.json())
            .then(responseData => {

                if(responseData.message === "Invalid credentials")
                {
                    console.log(responseData);
                    alert("Invalid credentials");
                }
                else if(responseData.message === "Server error")
                {
                    console.log(responseData);
                    alert("Server Error");
                }
                else{

                    localStorage.setItem("jwtToken", responseData['jwtToken']);
                    localStorage.setItem("tollPlaza", responseData['tollPlaza']);
                    localStorage.setItem("state", responseData['state']);
                    console.log(responseData);
                    window.location.href = `logged.html`;
                }

            })
            .catch(error => {
                console.error("Error:", error);
            });

    } else {
        alert('CAPTCHA is incorrect. Please try again.');
    }
});

