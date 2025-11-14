

async function validateUser() {
    try {

        // Get variables
        let username = document.getElementById("username").value;
        let password = document.getElementById("password").value;

        // Send post request
        const response = await fetch("/api/user/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            credentials: "include",
            body: JSON.stringify({ username, password })
        });

        // Inform the user
        if (response.ok) {
            document.getElementById("result").innerText = "Logged in.";
        } else {
            document.getElementById("result").innerText = "Login failed! Please, try again.";
        }

        console.log(response);

    }

    catch (error) {
        document.getElementById("result").textContent = "Login failed! Please, try again.";
        console.error(error);
    }
}
