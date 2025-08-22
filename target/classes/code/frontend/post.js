
async function addMeme() {
    try {
        document.getElementById("buttonText").textContent = "   Adding..."

        let title = document.getElementById("title").value;
        let tags = document.getElementById("tags").value;
        let image = document.getElementById("memeFile").files[0];

        if (title == null || tags == null || image == null) {
            document.getElementById("buttonText").textContent = "   All above fields must have content in them.";
        }

        let memeJson = JSON.stringify({
            title: title,
            tags: tagsJson(tags.split(" "))
        })

        let data = new FormData()
            .append("meme", memeJson)
            .append("image", image);

        const response = await fetch("/api/memes", {
            method: "POST",
            body: data
        }) 

        const result = await response.json();
        document.getElementById("buttonText").textContent = "   Meme added succesfully.";
        alert(result.message);
    } catch (error) {
        document.getElementById("results").textContent = "  Unable to add the meme, please try again.";
        console.error(error);
    }
}



async function getTagsJsonArray(tags) {

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