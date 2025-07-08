package code.meme_comparators;

import java.util.Comparator;

import code.Meme;



public class MemeIdComparator implements Comparator<Meme> {

    @Override
    public int compare(Meme memeID, Meme otherMemeID) {

        if (memeID != null && otherMemeID != null) {
            return memeID.compareTo(otherMemeID);
        }

        return 0;
    }
    
}