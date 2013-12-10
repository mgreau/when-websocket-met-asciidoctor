package com.mgreau.wwsmad.agorava;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.agorava.AgoravaContext;
import org.agorava.api.atinject.Current;
import org.agorava.api.oauth.OAuthSession;
import org.agorava.api.service.OAuthLifeCycleService;
import org.agorava.api.storage.UserSessionRepository;
import org.agorava.spi.UserProfile;

/**
 * @author Antoine Sabot-Durand
 */
@Path("/")
@Produces("application/json")
public class SocialService {

    @Inject
    OAuthLifeCycleService lifeCycleService;

    @Inject
    RestSessionProducer resolver;

    @Inject
    @Current
    UserSessionRepository repo;

    @GET
    @Path("/users/current")
    public UserProfile getCurrentProfile() {
        System.out.println(repo.getCurrent());
        return repo.getCurrent().getUserProfile();
    }

    @GET
    @Path("/services")
    public Map<String, ServiceStructure> getServices() {
        Map<String, ServiceStructure> result = new HashMap<String, ServiceStructure>();

        for (String serviceName : AgoravaContext.getListOfServices()) {
            Map<String, OAuthSessionJson> n2s = new HashMap<String, OAuthSessionJson>();
            for (OAuthSession session : repo.getAll()) {
                if (session.getServiceName().equals(serviceName)) {
                    n2s.put(session.getId(), new OAuthSessionJson(session));
                }
            }
            result.put(serviceName, new ServiceStructure(serviceName, n2s));
        }
        return result;
    }

    @GET
    @Path("/services/current")
    public OAuthSessionJson getCurrentService() {
        return new OAuthSessionJson(lifeCycleService.getCurrentSession());
    }

    @GET
    @Path("/sessions")
    public List<OAuthSessionJson> getSessions() {
        List<OAuthSessionJson> result = new ArrayList<OAuthSessionJson>();
        for (OAuthSession session : lifeCycleService.getAllActiveSessions()) {
            result.add(new OAuthSessionJson(session));
        }
        return result;
    }

    @GET
    @Path("/sessions/current")
    public OAuthSessionJson getCurrentSession() {
        return new OAuthSessionJson(lifeCycleService.getCurrentSession());
    }

    @GET
    @Path("/providers")
    public List<String> getListOfServices() {
        return AgoravaContext.getListOfServices();
    }

    @GET
    @Path("/providers/{service}/startDance")
    public String startDanceFor(@PathParam("service") String service) {
        return lifeCycleService.startDanceFor(service);
    }


}