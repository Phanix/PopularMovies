package hantaro.com.popularmovies;

import java.io.Serializable;

/**
 * This class represent each review in the movie
 * contains a review and the reviewer name
 */
public class Reviewer implements Serializable {

    private String mName;
    private String mContent;

    public String getName() {
        return mName;
    }

    public String getContent() {
        return mContent;
    }

    public Reviewer(String name, String content) {
        mName = name;
        mContent = content;
    }
}
