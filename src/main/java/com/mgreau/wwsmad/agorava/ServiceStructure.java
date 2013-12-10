package com.mgreau.wwsmad.agorava;
import java.util.Map;

/**
 * @author Antoine Sabot-Durand
 */
public class ServiceStructure {

    private String name;

    private Map<String,OAuthSessionJson> sessions;

    public ServiceStructure(String name, Map<String, OAuthSessionJson> sessions) {
        this.name = name;
        this.sessions = sessions;
    }

    public Map<String, OAuthSessionJson> getSessions() {
        return sessions;
    }

    public String getName() {
        return name;
    }
}