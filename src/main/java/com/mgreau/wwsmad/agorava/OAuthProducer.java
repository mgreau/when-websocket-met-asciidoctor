package com.mgreau.wwsmad.agorava;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.agorava.api.oauth.application.OAuthAppSettings;
import org.agorava.api.oauth.application.OAuthAppSettingsBuilder;
import org.agorava.api.oauth.application.OAuthApplication;
import org.agorava.api.oauth.application.Param;
import org.agorava.api.oauth.application.SimpleOAuthAppSettingsBuilder;
import org.agorava.twitter.Twitter;

public class OAuthProducer {
	
	@ApplicationScoped
	@Produces
	@Twitter
	@OAuthApplication(builder = SimpleOAuthAppSettingsBuilder.class,
	        params = {@Param(name = OAuthAppSettingsBuilder.API_KEY, value = "jkEB9qmxwr0zcTxZjHH7g"),
	                @Param(name = OAuthAppSettingsBuilder.API_SECRET, value = "OQTvDps8maYW0UuMhFDNqQSHnCoW1rNXLtghAlLJaY"),
	        })
	public OAuthAppSettings twitterSettings;
	

}
