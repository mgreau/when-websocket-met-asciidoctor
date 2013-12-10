package com.mgreau.wwsmad.agorava;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agorava.AgoravaConstants;
import org.agorava.api.atinject.Current;
import org.agorava.api.storage.GlobalRepository;
import org.agorava.api.storage.UserSessionRepository;
import org.agorava.cdi.InApplicationProducer;
import org.agorava.cdi.deltaspike.DifferentOrNull;
import org.agorava.spi.UserSessionRepositoryResolver;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.exclude.Exclude;
import org.apache.deltaspike.servlet.api.Web;

/**
 * @author Antoine Sabot-Durand
 */
@RequestScoped
@Exclude(onExpression = InApplicationProducer.RESOLVER + ",rest", interpretedBy = DifferentOrNull.class)
public class RestInCookieRepoProducer implements UserSessionRepositoryResolver {

    @Inject
    @ConfigProperty(name = AgoravaConstants.RESOLVER_COOKIE_LIFE_PARAM, defaultValue = "-1")
    Integer cookielife;


    @Inject
    @Web
    private HttpServletResponse response;

    @Inject
    @Web
    private HttpServletRequest request;

    @Inject
    private GlobalRepository globalRepository;


    protected String getRepoId() {
        String id;
        if (request.getCookies() != null)
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(AgoravaConstants.RESOLVER_REPO_COOKIE_NAME))
                    return cookie.getValue();
            }
        return null;
    }

    private void setCookie(String id) {
        Cookie cookie = new Cookie(AgoravaConstants.RESOLVER_REPO_COOKIE_NAME, id);
        cookie.setMaxAge(cookielife);
        String path = request.getContextPath().isEmpty() ? "/" : request.getContextPath();
        cookie.setPath(request.getContextPath());
        response.addCookie(cookie);
    }

    @Produces
    @Current
    @Named("currentRepo")
    @RequestScoped
    public UserSessionRepository getCurrentRepository() {
        String id = getRepoId();
        if (id == null || globalRepository.get(id) == null) {
            UserSessionRepository repo = globalRepository.createNew();
            setCookie(repo.getId());
            return globalRepository.createNew();
        } else
            return globalRepository.get(id);
    }



}
