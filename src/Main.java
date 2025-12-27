import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args){
        String[] domains = {"https://ds0h3roq6wcgc.cloudfront.net/", "https://d2nvs31859zcd8.cloudfront.net/" , "https://d2aba1wr3818hz.cloudfront.net/" ,
                "https://d3c27h4odz752x.cloudfront.net/" , "https://dgeft87wbj63p.cloudfront.net/" , "https://d1m7jfoe9zdc1j.cloudfront.net/" ,
                "https://d3vd9lfkzbru3h.cloudfront.net/" , "https://ddacn6pr5v0tl.cloudfront.net/" , "https://d3aqoihi2n8ty8.cloudfront.net/" ,
                "https://d3fi1amfgojobc.cloudfront.net/", "https://d1g1f25tn8m2e6.cloudfront.net/", "https://d1oca24q5dwo6d.cloudfront.net/",
                "https://d1w2poirtb3as9.cloudfront.net/", "https://d2dylwb3shzel1.cloudfront.net/",
                "https://d2um2qdswy1tb0.cloudfront.net/", "https://d2xmjdvx03ij56.cloudfront.net/", "https://d36nr0u3xmc4mm.cloudfront.net/",
                "https://d6d4ismr40iw.cloudfront.net/", "https://d6tizftlrpuof.cloudfront.net/", "https://dykkng5hnh52u.cloudfront.net/",
                "https://d2vi6trrdongqn.cloudfront.net/", "https://d3stzm2eumvgb4.cloudfront.net/"};
        UIClass ui = new UIClass(domains);
        //guess();
    }

}


