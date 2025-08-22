
let sortingType = "id";

const sortLabels = {
    id: "Oldest",
    reverse_id: "Newest",
    title: "Alphabetical",
    reverse_title: "Alphabetical (inverse)",
    likes: "Most Liked",
    reverse_likes: "Least Liked"
}


async function searchMeme() {
    try {
        let id = document.getElementById("id");
        let title = document.getElementById("title");
        let tags = document.getElementById("tags");

        let response = await fetch("/api/search?id=" + id + "&title=" + title + "&sortingType=" + sortingType, {
            method: "GET",
            headers: {"Content-Type": "application/json"},
            body: tagsJson(tags.split(" "))
        });
        let memes = JSON.parse(response);

        for (let meme of memes) {
            display_meme(meme);
        }

    } catch (error) {
        document.getElementById("searchResults").textContent = "Something went wrong";
        console.error(error);
    }
}


function tagsJson(tags) {
    let tagsJson = "[";
    let index = 1;
    
    for (let tag of tags) {
        tagsJson += "{title: " + tag + ", count = 0}"

        if (index++ != tags.length) {
            tagsJson += ", "
        }
    }

    return JSON.parse(tagsJson + "]");
}


function display_meme(memeJson) {
    let meme = document.createElement("img");

    meme.src = memeJson.path;
    meme.width = memeJson.width;
    meme.height = memeJson.height;
    meme.alt = memeJson.title;

    document.body.appendChild(meme);
}


function setSort(type) {
    sortingType = type;

    const label = sortLabels[type] || "Newest";
    document.getElementById("sortingType").textContent = label;
}