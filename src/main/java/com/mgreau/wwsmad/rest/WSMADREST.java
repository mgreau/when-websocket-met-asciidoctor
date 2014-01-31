package com.mgreau.wwsmad.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mgreau.wwsmad.asciidoctor.AsciidoctorProcessor;

@Path("documents")
public class WSMADREST {

	@Inject
	AsciidoctorProcessor processor;

	private String sample = "sample.ad";

	@GET
	@Path("sample-adoc")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAdocFile() {
		return getSampleDoc();
	}

	private String getSampleDoc(){
		return processor.readFromStream(Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(sample));
	}

}