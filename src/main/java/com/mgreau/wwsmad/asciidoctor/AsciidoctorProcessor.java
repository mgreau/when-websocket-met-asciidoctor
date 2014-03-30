package com.mgreau.wwsmad.asciidoctor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.ContentPart;
import org.asciidoctor.ast.DocumentHeader;
import org.asciidoctor.ast.StructuredDocument;
import org.asciidoctor.extension.JavaExtensionRegistry;

@ApplicationScoped
public class AsciidoctorProcessor {
	
	private static final Logger logger = Logger.getLogger("AsciidoctorProcessor");
	
	private Asciidoctor asciidoctor ;//=  Asciidoctor.Factory.create();
	
	public String renderAsDocument(final String source){
		return renderAsDocument(source, "html5", null, null);
	}
	
    // tag::render[]
	public String renderAsDocument(final String source,String backend, final File templateDir, String part) {
		String output = null;
		
		if (backend == null || "".equals(backend))
			backend = "html5";
		
		if (backend.equals("html5")){
			JavaExtensionRegistry javaExtensionRegistry = this.asciidoctor
					.javaExtensionRegistry();
			javaExtensionRegistry
					.postprocessor("com.mgreau.wwsmad.asciidoctor.IFrameAnchorPostProcessor");
		} else if (backend.equals("dzslides")){
			JavaExtensionRegistry javaExtensionRegistry = this.asciidoctor
					.javaExtensionRegistry();
			javaExtensionRegistry
					.postprocessor(DZSlidesPostProcessor.class);
		}
		
		OptionsBuilder optsBuilder = OptionsBuilder
				.options();
		if (templateDir != null && templateDir.exists()){
			optsBuilder = optsBuilder.templateDir(templateDir);
		}
		
		try {
			logger.info("[RENDER]::START rendering adoc");
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters = optsBuilder
					.backend(backend)
					.safe(SafeMode.UNSAFE).headerFooter(true)
					.eruby("erubis")
					.attributes(
							AttributesBuilder.attributes()
									.attribute("icons!", "")
									.attribute("allow-uri-read")
									.attribute("copycss!", "").asMap())
					.asMap();
			
			StructuredDocument document = asciidoctor.readDocumentStructure(
					source, parameters);
			if (backend.equals("dzslides") && document != null && part != null  && document.getPartById(part) != null) {
				parameters.put(Asciidoctor.STRUCTURE_MAX_LEVEL, 2);
				ContentPart p = document.getPartById(part);
				output = getHeaderForSlides() + "<body><"+ p.getContext() +" class=\""+ p.getRole() + "\">"+ "<h2>" + p.getTitle() + "</h2>" + p.getContent() + "</"+ p.getContext() +">" + getBodyFooterForSlides() +"</body>";
			}
			
			if (output == null)
				output = asciidoctor.render(source, parameters);
			logger.info("[RENDER]::END rendering adoc");

		} catch(RuntimeException rex){
			logger.severe("[RENDER]::ERROR rendering adoc" + rex.toString());
			output = "Error during render process";
		}
		return output;
	}
    // end::render[]
	
	public DocumentHeader renderDocumentHeader(String source) {
		logger.info("Start rendering adoc");
		return asciidoctor.readDocumentHeader(source);
	}
	
	public String renderAsDocument(InputStream source) {
		return renderAsDocument(readFromStream(source));
	}
	
	public Asciidoctor getDelegate() {
		return asciidoctor;
	}
	
	@PostConstruct
	public void init() {
		asciidoctor = Asciidoctor.Factory.create();
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
	
	private String getHeaderForSlides(){
      return "<head> " +
  "<meta charset=\"UTF-8\"> "+
  "<title>Real-time collaborative editor for AsciiDoc</title> " +
  "<link rel=\"stylesheet\" href=\"http://fonts.googleapis.com/css?family=Open+Sans:400,700,200,300\"> " +
  "<link rel=\"stylesheet\" href=\"./dzslides/themes/highlight/asciidoctor.css\"> " +
  "<link rel=\"stylesheet\" href=\"./dzslides/themes/style/devnation.css\"> " +
  "<link rel=\"stylesheet\" href=\"./dzslides/core/dzslides.css\"> " +
  "<link rel=\"stylesheet\" href=\"./dzslides/themes/transition/fade.css\"> " +
 "</head>";
	}
	
	private String getBodyFooterForSlides(){
		return "<script src=\"./dzslides/core/dzslides.js\"></script> " +
				"<script src=\"./dzslides/highlight/highlight.pack.js\"></script>"
				+ "<script>hljs.initHighlightingOnLoad()</script>";
	}
}
