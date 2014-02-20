package lol;

import java.util.ArrayList;

/**
 * Created by thb on 12.02.14.
 */
public class GeneralSaying {
    private final long id;
    private final String content;

    public GeneralSaying(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}