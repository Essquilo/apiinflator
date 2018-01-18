package de.ovgu.softwareproductlines;

import org.junit.Test;

public class APITest {

    @Test
    public void testGithubApi() throws Exception {
        System.out.println("Testing aspects...");
        TestAPI api = new TestAPI();
        api.rawRepos("me");
    }

    public static void main(String[] args) {
        try {
            new APITest().testGithubApi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
