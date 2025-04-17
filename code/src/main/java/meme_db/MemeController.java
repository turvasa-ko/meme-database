package meme_db;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/memes")
public class MemeController {

    private final MemeRepository memeRepository;


    public MemeController(MemeRepository memeRepository) {
        this.memeRepository = memeRepository;
    }




    @PostMapping("/add")
    public String postMeme(
        @RequestParam String title,
        @RequestParam MultipartFile photo,
        @RequestParam Set<String> tags
    ) {

        try {
            Meme meme = new Meme(title, tags, photo.getBytes());
            memeRepository.save(meme);

            return meme.getTitle()+" was added succesfully";
        }

        catch (Exception e) {
            return "Error occured while adding meme: "+e;
        }
    }

    


    @GetMapping("/memes/title")
    public ResponseEntity<byte[]> getMemeByTitle(@RequestParam String title) {        
        Meme meme = memeRepository.findById(title).orElseThrow();

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(meme.getImage());
    }
    


    @GetMapping("/memes")
    public List<ResponseEntity<byte[]>> getMethodName() {
        List<Meme> memes = memeRepository.findAll();
        List<ResponseEntity<byte[]>> photos = new ArrayList<>();

        for (Meme meme: memes) {
            photos.add(ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(meme.getImage()));
        }

        return photos;
    }




    @GetMapping("/memes/tags")
    public List<ResponseEntity<byte[]>> getMethodName(@RequestParam String tags) {        
        List<Meme> memes = memeRepository.findAll();
        List<ResponseEntity<byte[]>> photos = new ArrayList<>();

        for (Meme meme: memes) {
            
            Set<String> tagSet = tagSet(tags);
            Set<String> memeTags = meme.getTags();

            if (memeTags.containsAll(tagSet)) {
                photos.add(ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(meme.getImage()));
            }
        }

        return photos;
    }


    private Set<String> tagSet(String tags) {
        String[] tagsArray = tags.split(",");
        Set<String> tagSet = new HashSet<>();

        for (String tag: tagsArray) {
            tagSet.add(tag);
        }

        return tagSet;
    }
    
    

}
