
async function registerUser() {
    try {

        console.log("start")

        // Get variables
        let username = document.getElementById("username").value;
        let password = document.getElementById("password").value;

        console.log("variables")

        // Check vairables validity
        checkValidity("username", username, /^[\w-]+$/, 3);
        checkValidity("password", password, /^.+$/, 6);

        console.log("valid")

        // Send post request
        const response = await fetch("/api/user/registration", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({ 
                name: username, 
                password
            })
        });

        console.log("send")

        // Inform the user
        if (response.ok) {
            document.getElementById("result").innerText = "Registration succeeded. You can now login.";
        } else {
            document.getElementById("result").innerText = "Registration failed! Please, try again.";
        }

        console.log(response);

    }

    catch (error) {

        // Invalid username
        if (error.includes("Invalid username")) {
            document.getElementById("usernameException").textContent = error;
        }

        // Invalid password
        else if (error.includes("Invalid password")) {
            document.getElementById("passwordException").textContent = error;
        }

        // Other errors
        else {
            document.getElementById("result").textContent = "Registration failed! Please, try again.";
        }

        console.error(error);
    }
}


function checkValidity(variable_name, str, regex, min_length) {

    // Check lenght
    if (str.length < min_length || str.length > 20) {
        throw "Invalid " + variable_name + ": length must be " + min_length + "-20 characters.";
    }

    // Check content
    if (!str.match(regex)) {
        throw "Invalid " + variable_name + ": can only contain alphabets, numbers, '_' and '-'.";
    }
}

