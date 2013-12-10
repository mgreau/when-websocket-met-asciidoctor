package com.mgreau.wwsmad.agorava;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.agorava.api.atinject.Current;
import org.agorava.api.oauth.OAuthSession;
import org.agorava.api.storage.UserSessionRepository;
import org.agorava.cdi.InApplicationProducer;
import org.agorava.cdi.deltaspike.DifferentOrNull;
import org.agorava.spi.SessionResolver;
import org.apache.deltaspike.core.api.exclude.Exclude;

/**
 * @author Antoine Sabot-Durand
 */
@Exclude(onExpression = InApplicationProducer.RESOLVER + ",rest", interpretedBy = DifferentOrNull.class)
@RequestScoped
public class RestSessionProducer implements SessionResolver {

    @Override
    @Produces
    public OAuthSession getCurrentSession(@Current UserSessionRepository repository) {
        return repository.getCurrent();
    }

    @Inject
    @Current
    private UserSessionRepository repo;


    public OAuthSession resolveSession(String id) {
        repo.setCurrent(repo.get(id));
        return repo.getCurrent();
    }

    public Iterator<OAuthSession> iteratorOnSessions(String service) {
        final List<OAuthSession> sessionForServices = new ArrayList<OAuthSession>();
        for (OAuthSession session : repo) {
            if (session.getName().equals(service))
                sessionForServices.add(session);
        }


        return new Iterator<OAuthSession>() {

            Iterator<OAuthSession> iter = sessionForServices.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public OAuthSession next() {
                OAuthSession res = iter.next();
                repo.setCurrent(res);
                return res;
            }


            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


}