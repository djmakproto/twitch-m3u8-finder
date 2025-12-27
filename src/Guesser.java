import jdk.nashorn.internal.codegen.CompilerConstants;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class Guesser implements Callable<String> {
    String domain, username, vodId, threadName;
    Timestamp startTime;
    boolean debugMode;
    public Guesser(String d, String un, Timestamp time, String id, boolean dbg){
        domain = d;
        threadName = domain.substring(domain.indexOf("/")+2, domain.indexOf("."));
        username = un;
        vodId = id;
        debugMode = dbg;
        startTime = (Timestamp) time.clone();
        log(String.format("%s: %s\t%s: %s", "domain", domain, "time", startTime));
    }

    public String call() throws Exception {
        String s = constructURL(domain, username, (startTime.getTime()/1000), vodId);
        return checkURL(s);
    }

    public String constructURL(String domain, String username, long timestamp, String vodID) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        String input = String.format("%s_%s_%s", username.trim(), vodID.trim(), timestamp);
        log("input: " + input);
        md.reset();
        md.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] hash = md.digest();
        log("hash array: " + Arrays.toString(hash));
        BigInteger hexConv = new BigInteger(1, hash);
        log("full hash: " + hexConv.toString(16));
        String prelimhash = hexConv.toString(16);
        while(prelimhash.length() < 40){
            prelimhash = "0"+prelimhash;
        }
        String hashOutput = prelimhash.substring(0, 20);
        return String.format("%s%s_%s/chunked/index-dvr.m3u8", domain, hashOutput, input);
    }

    public String checkURL(String url) throws URISyntaxException, IOException {
        HttpURLConnection m3u8File = (HttpURLConnection) new URI(url).toURL().openConnection();
        m3u8File.setRequestMethod("HEAD");
        m3u8File.setRequestProperty("content-type", "text");
        int responseCode = m3u8File.getResponseCode();
        log(responseCode + " " + url);
        if(responseCode == 200){
            return url;
        }
        return null;
    }

    public void log(String message){
        if(debugMode) {
            System.out.println(threadName + ": " + message);
        }
    }
}
