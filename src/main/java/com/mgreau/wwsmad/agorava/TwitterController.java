package com.mgreau.wwsmad.agorava;
import org.agorava.twitter.Twitter;
import org.agorava.twitter.TwitterTimelineService;
import org.agorava.twitter.model.Tweet;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/twitter")
@Produces("application/json")
public class TwitterController {

    @Inject
    @Twitter
    TwitterTimelineService tls;

    @GET
    @Path("/timeline")
    public List<Tweet> getHomeTimeline() {
        return tls.getHomeTimeline();
    }
}