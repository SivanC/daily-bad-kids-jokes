package com.badkidsjokes.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.tumblr.jumblr.*;
import com.tumblr.jumblr.types.*;

/**
 * Gets jokes from the badkidsjokes tumblr blog. All credit for the jokes
 * goes to badkidsjokes.tumblr.com.
 *
 * @version 0.1.0
 * @author Sivan Cooperman
 */
public class JokeGetter {
    private Blog blog;
    private JumblrClient client;

    public JokeGetter() {
        try {
            File secrets = new File("src/main/resources/tumblr-auth.txt");
            Scanner scn = new Scanner(secrets);
            String client_key = scn.nextLine().split(":")[1];
            String client_secret = scn.nextLine().split(":")[1];
            String oauth_token = scn.nextLine().split(":")[1];
            String oauth_secret = scn.nextLine().split(":")[1];
            scn.close();

            this.client = new JumblrClient(client_key, client_secret);
            this.client.setToken(oauth_token, oauth_secret);
            this.blog = client.blogInfo("badkidsjokes.tumblr.com");
        } catch(FileNotFoundException e) {
            System.out.println("Secrets file not found!");
        }
    }

    public String getJoke() {
        // Generate random number between 0 and num posts
        Random rng = new Random();
        int numPosts = this.blog.getPostCount();
        int postIndex = rng.nextInt(numPosts);

        // Jumblr only gets 20 posts at a time, so use pagination to offset
        // to the correct page.
        Map<String, Integer> options = new HashMap<String, Integer>();
        options.put("offset", postIndex / 20);
        int postPageIndex = postIndex % 20;

        System.out.println("Post Index: " + Integer.toString(postIndex) +
                "\nOffset: " + Integer.toString(postIndex / 20) +
                "\nIndex on Page: " + Integer.toString(postPageIndex));

        List<Post> posts = this.blog.posts(options);
        TextPost post = (TextPost) posts.get(postPageIndex);
        System.out.println(post.getBody());
        return post.getBody();
    }
}
