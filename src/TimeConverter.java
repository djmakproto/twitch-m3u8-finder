import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeConverter {
    public static Timestamp convertTime(String startTime) throws Exception {
        startTime = startTime.trim().toLowerCase();
        Pattern twitchTracker = Pattern.compile("[a-z]*, [a-z]* [1-3]?[0-9], [0-2]?[0-9]:[0-5][0-9]"); //day, month, date, 24-hour:minute
        Pattern twitchTrackerSlash = Pattern.compile("[0-3]?[0-9]/[a-z]*/[0-9]* [0-2]?[0-9]:[0-5][0-9]"); //date/month/year 24-hour:minute
        Pattern sullygnome = Pattern.compile("[a-z]* [0-3]?[0-9][snrt][tdh] [a-z]* [0-1]?[0-9]:[0-5][0-9][ap]m"); //day date month 12-hour:minuteMeridian
        Pattern streamscharts = Pattern.compile("[0-3][0-9] [a-z]* [0-9]*, [0-2]?[0-9]:[0-5][0-9]"); //date month year, 24-hour:minute
        Pattern extension = Pattern.compile("[a-z]*, [0-3]?[0-9] [a-z]* [0-9]* [0-2]?[0-9]:[0-5][0-9] [ap]m"); //day, date month year 24-hour:minute meridian
        Pattern twentyFourTime = Pattern.compile("[0-2]?[0-9]:[0-5][0-9]"); //24-hour:minute
        Pattern twelveTime = Pattern.compile("[0-1]?[0-9]:[0-5][0-9] ?[ap]m"); //12-hour:minute meridian(space optional)
        Matcher ttMatcher = twitchTracker.matcher(startTime);
        Matcher ttSlashMatcher = twitchTrackerSlash.matcher(startTime);
        Matcher sullygnomeMatcher = sullygnome.matcher(startTime);
        Matcher streamschartsMatcher = streamscharts.matcher(startTime);
        Matcher extensionMatcher = extension.matcher(startTime);
        Matcher twentyFourMatcher = twentyFourTime.matcher(startTime);
        Matcher twelveMatcher = twelveTime.matcher(startTime);
        String[] parts = startTime.split(" ");
        if(ttMatcher.matches()){
            return twitchTrackerTimestamp(parts);
        }
        if(ttSlashMatcher.matches()){
            return twitchTrackerSlashTimestamp(startTime);
        }
        if(sullygnomeMatcher.matches()){
            return sullygnomeTimestamp(parts);
        }
        if(streamschartsMatcher.matches()){
            return streamschartsTimestamp(parts);
        }
        if(extensionMatcher.matches()){
            return extensionTimestamp(parts);
        }
        if(twentyFourMatcher.matches()){
            return twentyFourTimestamp(startTime);
        }
        if(twelveMatcher.matches()){
            return twelveTimestamp(startTime);
        }
        throw new Exception("invalid timestamp format");
    }

    public static Timestamp twitchTrackerTimestamp(String[] parts) throws Exception {
        String month = parts[1];
        String day = parts[2].substring(0, parts[2].indexOf(','));
        String hour = parts[3].substring(0, parts[3].indexOf(":"));
        String min = parts[3].substring(parts[3].indexOf(":")+1);
        return constructTimestamp(month, day, hour, min);
    }

    public static Timestamp twitchTrackerSlashTimestamp(String startTime) throws Exception {
        String[] parts = startTime.split(" ");
        String[] dmy = parts[0].split("/");
        String[] time = parts[1].split(":");
        return constructTimestamp(dmy[1], dmy[0], time[0], time[1]);
    }

    public static Timestamp sullygnomeTimestamp(String[] parts) throws Exception {
        String month = parts[2];
        String day = parts[1].substring(0,parts[1].length()-2);
        String hour = parts[3].substring(0,parts[3].indexOf(':'));
        String min = parts[3].substring(parts[3].indexOf(':')+1,parts[3].length()-2);
        String meridian = parts[3].substring(parts[3].length()-2);
        String hr = twelveToTwentyFour(Integer.parseInt(hour), meridian);
        return constructTimestamp(month, day, hr, min);
    }

    public static Timestamp streamschartsTimestamp(String[] parts) throws Exception {
        String month = parts[1];
        String day = parts[0];
        String hour = parts[3].substring(0, parts[3].indexOf(":"));
        String min = parts[3].substring(parts[3].indexOf(":")+1);

        return constructTimestamp(month, day, hour, min);
    }

    public static Timestamp extensionTimestamp(String[] parts) throws Exception {
        String month = parts[2];
        String day = parts[1];
        String meridian = parts[5];
        String hour = parts[4].substring(0,parts[4].indexOf(":"));
        String min = parts[4].substring(parts[4].indexOf(":")+1);
        hour = twelveToTwentyFour(Integer.parseInt(hour),meridian);
        return constructTimestamp(month,day,hour,min);
    }

    public static Timestamp twentyFourTimestamp(String startTime){
        String hour = startTime.substring(0,startTime.indexOf(":"));
        String minutes = startTime.substring(startTime.indexOf(":")+1);
        Date now = Date.from(Instant.now());
        return new Timestamp(now.getYear(), now.getMonth(), now.getDate(), Integer.parseInt(hour), Integer.parseInt(minutes), 0, 0);
    }

    public static Timestamp twelveTimestamp(String startTime){
        String hour = startTime.substring(0,startTime.indexOf(":"));
        String min = startTime.substring(startTime.indexOf(":")+1, startTime.indexOf(":")+3);
        String meridian = startTime.substring(startTime.length()-2);
        String twofourhour = twelveToTwentyFour(Integer.parseInt(hour), meridian);
        return twentyFourTimestamp(twofourhour + ":" + min);
    }

    public static String twelveToTwentyFour(int hour, String meridian){
        int hr = hour;
        if(meridian.equals("am") && hour == 12){
            hr = 0;
        }
        if(meridian.equals("pm") && hour < 12){
            hr = hour+12;
        }
        return Integer.toString(hr);
    }

    public static int monthToInt(String m){
        String[] monthShort = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] monthLong = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        for(int i = 0; i < monthLong.length; i++){
            if(m.equalsIgnoreCase(monthLong[i]) || m.equalsIgnoreCase(monthShort[i])){
                return i;
            }
        }
        return -1;
    }

    public static Timestamp constructTimestamp(String month, String day, String hour, String min) throws Exception {
        int monthInt = monthToInt(month.trim());
        if(monthInt == -1){
            throw new Exception("invalid month");
        }
        int year = Date.from(Instant.now()).getYear();
        Timestamp ans = new Timestamp(year, monthInt, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min), 0, 0);
        if(ans.after(new Timestamp(Instant.now().toEpochMilli()))){
            ans.setYear(year-1);
        }
        return ans;
    }
}
