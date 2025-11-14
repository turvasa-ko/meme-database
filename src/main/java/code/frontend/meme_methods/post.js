
async function addMeme() {
    try {

        // Get the variables
        let title = document.getElementById("title").value.trim();
        let tags = document.getElementById("tags").value.split(" ");
        let image = document.getElementById("memeFile").files[0];

        // Check variables validity
        checkTitleValidity(title);
        checkTagsValidity(tags);
        checkFileValidity(file);

        // Convert variables to JSON
        let memeJson = JSON.stringify({
            title: title,
            tags: getTagsArray(tags)
        })

        // Convert meme file to FormData
        let data = new FormData();
        data.append("meme", memeJson)
        data.append("image", image);

        // Create the content header
        const content = {
            method: "POST",
            credentials: "include",
            body: data
        };

        // Send the POST request
        document.getElementById("result").textContent = "   Adding..."
        const response = await fetch("/api/memes", content);
        infromUser(response);

        // Console output
        console.log(response);
    } 
    
    catch (error) {

        // Invalid meme title
        if (error.includes("Invalid title")) {
            document.getElementById("titleException").textContent = error;
        }

        // Invalid tag title
        else if (error.includes("Invalid tag")) {
            document.getElementById("tagsException").textContent = error;
        }

        // invalid file format
        else if (error.includes("Invalid file")) {
            document.getElementById("fileException").textContent = error;
        }

        // Other errors
        else document.getElementById("result").textContent = "Unable to add the meme. Please try again.";

        console.error(error);
    }
}



function getTagsArray(tags) {
    let tagsArray = [];

    for (let tag of tags) {
        tagsArray.push({
            title: tag, 
            count: "0"
        });
    }

    return tagsArray;
}



function checkTitleValidity(title) {
    let regex = "/^[\w-]{3-30}$/";

    // Check length
    if (title.length > 30 || title.length < 3) {
        throw "Invalid title: length must be 3-30 chars.";
    }

    // Match for regex
    if (!title.match(regex)) {
        throw "Invalid title: can only contain alphabets, numbers, '_' and '-'."
    }

    // Check and relpace white space
    if (title.includes(" ")) {
        document.getElementById("titleException").textContent = "All white space in the title are replaced by '-' automaticly"
        title = title.replace(" ", "_")
    }

    // Reset exception message
    document.getElementById("titleException").textContent = "";
}


function checkTagsValidity(tags) {
    let regex = "/^[/w-]{1,20}$/"

    if (tags.length < 1) {
        throw "Invalid tag: at least 1 take must be given."
    }

    // Iterate all tags
    for (let tag of tags) {

        // Check length
        if (tag.length > 20) {
            throw "Invalid tag: length can't exceed 20 chars.";
        }

        // Match for regex
        if (!tag.match(regex)) {
            throw "Invalid tag: can only contain alphabets, numbers, '_' and '-'."
        }
    }

    // Reset exception message
    document.getElementById("tagException").textContent = "";
}



function checkFileValidity(file) {
    let allowedExtensions = "(\.jpg|\.jpeg|\.png|\.gif)$/i";

    // Check file validity
    if (!allowedExtensions.exec(file.value)) {
        throw "Invalid file format: must be .jpg, .jpeg, .png or .gif"
    }
}


function infromUser(response) {

    // Success
    if (response.ok) {
        document.getElementById("result").textContent = "Meme added succesfully.";
    }

    // Unauthorized user
    else if (response.status = 401) {
        document.getElementById("result").textContent = "You have to login before adding memes."
    }

    // Other exceptions
    else {
        document.getElementById("result").textContent = "Unable to add the meme. Please try again.";
    }
}
