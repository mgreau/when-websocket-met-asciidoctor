package com.mgreau.wwsmad.asciidoctor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.DocumentHeader;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.extension.ExtensionRegistry;

@ApplicationScoped
public class AsciidoctorProcessor {
	
	private static final Logger logger = Logger.getLogger("AsciidoctorProcessor");
	private Asciidoctor delegate;
	
    // tag::render[]
	public String renderAsDocument(String source, String baseDir) {
		logger.info("[RENDER]::START rendering adoc");
		
		ExtensionRegistry extensionRegistry = this.delegate.extensionRegistry(); 
		extensionRegistry.postprocessor(IFrameAnchorPostProcessor.class);
		
		String output = "There is a problem";
		
		try{
			output = delegate.render(
					source,
					OptionsBuilder
							.options()
							.safe(SafeMode.UNSAFE).headerFooter(true)
							.eruby("erubis")
							.attributes(
									AttributesBuilder.attributes()
											.attribute("icons!", "")
											.attribute("allow-uri-read")
											.attribute("copycss!", "").asMap())
							.asMap());
			logger.info("[RENDER]::END rendering adoc");

		} catch(RuntimeException rex){
			logger.severe("[RENDER]::ERROR rendering adoc");
		}
		
		return output;
	}
    // end::render[]
	
	public DocumentHeader renderDocumentHeader(String source) {
		logger.info("Start rendering adoc");
		return delegate.readDocumentHeader(source);
	}
	
	public String renderAsDocument(InputStream source, String baseDir) {
		return renderAsDocument(readFromStream(source), baseDir);
	}
	
	public Asciidoctor getDelegate() {
		return delegate;
	}
	
	@PostConstruct
	public void init() {
		delegate = Asciidoctor.Factory.create();
	}
	
	public String readFromStream(final InputStream is) {
		if (is == null) {
			return "";
		}
		final char[] buffer = new char[1024];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException ex) {
			/* ... */
		} catch (IOException ex) {
			/* ... */
		}
		return out.toString();
	}
}
