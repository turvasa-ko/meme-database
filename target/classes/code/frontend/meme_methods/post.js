
async function addMeme() {
    try {
        document.getElementById("buttonText").textContent = "   Adding..."

        let title = document.getElementById("title").value;
        let tags = document.getElementById("tags").value;
        let image = document.getElementById("memeFile").files[0];

        let memeJson = JSON.stringify({
            title: title,
            tags: getTagsJsonArray(tags.split(" "))
        })

        let data = new FormData();
        data.append("meme", memeJson)
        data.append("image", image);

        const response = await fetch("/api/memes", {
            method: "POST",
            credentials: "include",
            body: data
        }) 

        const result = await response.json();
        document.getElementById("buttonText").textContent = "   Meme added succesfully.";
        alert(result.message);
    } catch (error) {
        document.getElementById("buttonText").textContent = "  Unable to add the meme. Please try again.";
        console.error(error);
    }
}



function getTagsJsonArray(tags) {

    let tagsJson = "[";
    let index = 1;

    for (let tag of tags) {
        tagsJson += "{title: " + tag + ", count: 0}";

        if (index++ != tags.lenght) {
            tagsJson += ", \n";
        }
    }

    return tagsJson + "]";
}
