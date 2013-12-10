package com.mgreau.wwsmad.agorava;
import org.agorava.api.oauth.OAuthSession;

import java.io.Serializable;
import java.util.Map;

public class OAuthSessionJson {

        private boolean hasOAuth;

    OAuthSession delegate;
        

        public OAuthSessionJson(OAuthSession session) {
                delegate=session;
    }

        public boolean isHasOAuth() {
                return delegate != OAuthSession.NULL;
        }

    public String getId() {
        return delegate.getId();
    }


    public Map<String, Serializable> getExtraData() {
        return delegate.getExtraData();
    }

    public boolean isConnected() {
        return delegate.isConnected();
    }

    public String getName() {
        return delegate.getName();
    }
}