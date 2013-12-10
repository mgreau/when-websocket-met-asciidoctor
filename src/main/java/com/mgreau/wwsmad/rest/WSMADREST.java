package com.mgreau.wwsmad.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.agorava.api.atinject.Current;
import org.agorava.api.oauth.OAuthSession;
import org.agorava.api.service.OAuthLifeCycleService;
import org.agorava.spi.UserProfile;
import org.agorava.twitter.Twitter;
import org.agorava.twitter.TwitterTimelineService;
import org.agorava.twitter.TwitterUserService;

import com.mgreau.wwsmad.asciidoctor.AsciidoctorProcessor;

@Path("documents")
public class WSMADREST {

	@Inject
	AsciidoctorProcessor processor;

	private String sample = "sample.ad";

	@GET
	@Path("sample-adoc")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAdocFile() {
		return getSampleDoc();
	}

	private String getSampleDoc() {
		return processor.readFromStream(Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(sample));
	}
	
}