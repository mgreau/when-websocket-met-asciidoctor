package com.mgreau.wildfly.asciidoctor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

@ApplicationScoped
public class AsciidoctorProcessor {
	private Asciidoctor delegate;
	
    // tag::render[]
	public String renderAsDocument(String source, String baseDir) {
		return delegate.render(source, OptionsBuilder.options()
				.safe(SafeMode.SAFE).backend("html5").headerFooter(true).eruby("erubis")
				//.option("base_dir", baseDir)
				.attributes(AttributesBuilder.attributes()
						.attribute("icons!", "")
						.attribute("copycss!", "").asMap()).asMap());
	}
    // end::render[]
	
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
