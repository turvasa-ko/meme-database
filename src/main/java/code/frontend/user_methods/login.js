

async function validateUser() {
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;

    const response = await fetch("/api/user/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        credentials: "include",
        body: JSON.stringify({ username, password })
    })

    if (response.ok) {
        document.getElementById("result").innerText = "Login succesfull"
    } else {
        document.getElementById("result").innerText = "Login failed! Please, try again."
    }
}
