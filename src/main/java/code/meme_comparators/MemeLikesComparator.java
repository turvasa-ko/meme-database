package code.meme_comparators;

import java.util.Comparator;

import code.Meme;



public class MemeLikesComparator implements Comparator<Meme> {
    
    @Override
    public int compare(Meme memeLikes, Meme otherMemeLikes) {

        if (memeLikes != null && otherMemeLikes != null) {
            return memeLikes.getLikes().compareTo(otherMemeLikes.getLikes());
        }

        return 0;
    }
}
