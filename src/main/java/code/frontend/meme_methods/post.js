
async function addMeme() {
    try {
        document.getElementById("buttonText").textContent = "   Adding..."

        // Get the variables
        let title = document.getElementById("title").value;
        let tags = document.getElementById("tags").value.split(" ");
        let image = document.getElementById("memeFile").files[0];

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
        const response = await fetch("/api/memes", content);

        /// Get results
        const result = await response.json();
        document.getElementById("buttonText").textContent = "   Meme added succesfully.";
        alert(result.message);
    } catch (error) {
        document.getElementById("buttonText").textContent = "  Unable to add the meme. Please try again.";
        console.error(error);
    }
}



function getTagsArray(tags) {
    let index = 1;
    tagsArray = [];

    for (let tag of tags) {
        tagsArray.push({
            title: tag, 
            count: "0"
        });
    }

    return tagsArray;
}
