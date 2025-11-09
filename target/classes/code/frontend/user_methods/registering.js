

function setUserCookie(username, password) {
    document.cookie = 
        "username=" + username + "; password=" + password + ";"+
        "path=/"
    ;
}


function getUserCookie() {
    let cookie = document.cookie.split(";");
    return cookie[0];
}
