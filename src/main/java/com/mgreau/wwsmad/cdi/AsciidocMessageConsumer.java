package com.mgreau.wwsmad.cdi;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.asciidoctor.ast.DocumentHeader;

import com.mgreau.wwsmad.asciidoctor.AsciidoctorProcessor;
import com.mgreau.wwsmad.cdi.qualifier.Backend;
import com.mgreau.wwsmad.cdi.qualifier.ComputeDiff;
import com.mgreau.wwsmad.cdi.qualifier.Patch;
import com.mgreau.wwsmad.diff.DiffAdoc;
import com.mgreau.wwsmad.diff.DiffProvider;
import com.mgreau.wwsmad.websocket.WWSMADEndpoint;
import com.mgreau.wwsmad.websocket.messages.OutputMessage;
import com.mgreau.wwsmad.websocket.messages.TypeFormat;
import com.mgreau.wwsmad.websocket.messages.TypeMessage;

/**
 * Consumer for all asciidoc messages.
 * 
 * @author mgreau
 *
 */
@ManagedBean
public class AsciidocMessageConsumer {
	
	@Inject
	private Logger logger;
	
	@Inject
	AsciidoctorProcessor processor; 
	
	@Inject @DiffProvider("Google")
	DiffAdoc diffGoogle;

	public void diffEvent(@Observes @ComputeDiff AsciidocMessageEvent event) {
		logger.info("::event:: received computeDiff event message");
		
		event.msg.setAdocSourceToMerge(diffGoogle.rawDiff(event.msg.getAdocSource(), event.msg.getAdocSourceToMerge()));
		event.msg.setType(TypeMessage.diff);
		event.msg.setAdocId(event.id);
		event.msg.setFormat(TypeFormat.asciidoc);
		
		WWSMADEndpoint.sendMessage(event.session, event.msg, event.id);
	}
	
	public void patchEvent(@Observes @Patch AsciidocMessageEvent event) {
		logger.info("::event:: received patch event message");
		
		event.msg.setAdocSourceToMerge(diffGoogle.applyPatch(event.msg.getAdocSource(), event.msg.getPatchToApply()));
		event.msg.setType(TypeMessage.patch);
		event.msg.setAdocId(event.id);
		event.msg.setFormat(TypeFormat.asciidoc);
		
		WWSMADEndpoint.sendMessage(event.session, event.msg, event.id);

	}
	
	public void html5RenderedEvent(@Observes @Backend("html5") AsciidocMessageEvent event){
		logger.info("::event:: received html5 event message");
		OutputMessage html = buildOutputMessage(TypeFormat.html5, event);
		
		long start = System.currentTimeMillis();
		try {
			html.setContent(processor.renderAsDocument(event.msg.getAdocSource()));
			html.setTimeToRender(System.currentTimeMillis() - start);
		} catch (RuntimeException rEx) {
			logger.severe("processing error." + rEx.getCause().toString());
		}

		// send the new HTML version to all connected peers
		WWSMADEndpoint.sendMessage(html, event.id);
	}
	
	/**
	 * 
	 * @param event
	 */
	public void dzslidesRenderedEvent(@Observes @Backend("dzslides") AsciidocMessageEvent event){
		logger.info("::event:: received dzslides event message");
		OutputMessage html = buildOutputMessage(TypeFormat.html5, event);
		
		final String templateDir = System.getProperty("jboss.server.data.dir")+"/asciidoctor-backends/slim/dzslides";
		
		long start = System.currentTimeMillis();
		try {
			html.setContent(processor.renderAsDocument(event.msg.getAdocSource(), "dzslides", 
					new java.io.File(templateDir), event.msg.getPart()));
			html.setTimeToRender(System.currentTimeMillis() - start);
		} catch (RuntimeException rEx) {
			logger.severe("processing error." + rEx.toString());
		}
		
		// send the new HTML version to all connected peers
		WWSMADEndpoint.sendMessage(html, event.id);
	}
	
	public void pdfRenderedEvent(@Observes @Backend("pdf") AsciidocMessageEvent event){
		//NOT YET IMPLEMENTED
	}
	
	private OutputMessage buildOutputMessage(TypeFormat type, AsciidocMessageEvent event){
		final OutputMessage html = new OutputMessage(type);
		html.setAdocId(event.id);
		html.setType(TypeMessage.output);
		html.setCurrentWriter(event.msg.getCurrentWriter());
		html.setAdocSource(event.msg.getAdocSource());
		html.setTimeToRender(-1);
		html.setDocHeader(checkHeader(event.msg
				.getAdocSource()));
		
		return html;
	}
	
	/**
	 * Check if all required headers are present.
	 * 
	 * @param source the AsciiDoc source sent by the client
	 * @return the DocumentHeader 
	 */
	private DocumentHeader checkHeader (String source){
		//Check if document header is present
		DocumentHeader docHeader = null;
		try {
			logger.info("[RENDER] processing DocumentHeader");

			docHeader = processor.renderDocumentHeader(source);
			for (Map.Entry<String, Object> h : docHeader.getAttributes().entrySet()){
				logger.log(Level.FINER, h.getKey() + " : " + h.getValue());
			}
			//FIXME : check which headers are mandatory
			Map<String, Object> headers = docHeader.getAttributes();
			if (docHeader.getAuthors().size() == 0) {
				logger.info("DocHeader add author");
				headers.put("author", "the Author");
				headers.put("email", "test@test.fr");
			}
			docHeader = DocumentHeader.createDocumentHeader("Doc title", "page title", headers);
			
		} catch (RuntimeException rEx) {
			logger.severe("DocHeader processing error, add custom header" + rEx.getCause().toString());
		}
		
		return docHeader;
				
	}


}
