import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Utility {


    public static Set<String> useThreadedGuesser(String[] domains, String username, String startTimeStr, String vodID) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(17);
        List<Guesser> guessList = new ArrayList<>();
        Set<String> resSet = new HashSet<>();
        Timestamp startTime = TimeConverter.convertTime(startTimeStr.trim());
        System.out.printf("checking %s at %s (+/- 1 min) vod id %s%n", username, startTime, vodID);
        boolean dbg = true;
        for (String domain : domains) {
            for(int i = 0; i < 180; i++) {
                Timestamp st = (Timestamp) startTime.clone();
                st.setMinutes(startTime.getMinutes()-1);
                st.setSeconds(i);
                Guesser thread = new Guesser(domain, username, st, vodID, dbg);
                guessList.add(thread);
            }
        }

        List<Future<String>> results = executorService.invokeAll(guessList);
        for(Future<String> res: results){
            String r = res.get();
            if(null != r){
                resSet.add(r);
            }
        }
        executorService.shutdown();
        if(resSet.isEmpty()){
            System.out.println("VOD does not exist");
            resSet.add("VOD does not exist");
        } else {
            for(String res:resSet){
                System.out.println(res);
            }
        }
        System.out.println("done guessing");
        return resSet;
    }
}
