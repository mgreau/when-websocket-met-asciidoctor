package com.mgreau.wwsmad.cdi;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.asciidoctor.DocumentHeader;

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
		
		OutputMessage html = new OutputMessage(TypeFormat.html5);
		html.setAdocId(event.id);
		html.setType(TypeMessage.output);
		html.setCurrentWriter(event.msg.getCurrentWriter());
		
		//Check if document header is present
		DocumentHeader docHeader = null;
		try {
			logger.info("DocHeader add custom header");

			docHeader = processor.renderDocumentHeader(event.msg
					.getAdocSource());
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
			headers.put("revdate", "2014-02-26");
			headers.put("revnumber", "1234");
			docHeader = DocumentHeader.createDocumentHeader("Doc title", "page title", headers);
			
		} catch (RuntimeException rEx) {
			logger.severe("DocHeader processing error, add custom header" + rEx.getCause().toString());
		}
		
		long start = System.currentTimeMillis();
		try {
			html.setContent(processor.renderAsDocument(event.msg.getAdocSource(),
					""));
			html.setDocHeader(docHeader);
			html.setTimeToRender(System.currentTimeMillis() - start);
		} catch (RuntimeException rEx) {
			html.setTimeToRender(-1);
			logger.severe("processing error." + rEx.getCause().toString());
		}
		html.setAdocSource(event.msg.getAdocSource());

		// send the new HTML version to all connected peers
		WWSMADEndpoint.sendMessage(html, event.id);
	}
	
	public void dzslidesRenderedEvent(@Observes @Backend("dzslides") AsciidocMessageEvent event){
		logger.info("DZSlides rendered start");
		
		 
	}
	
	public void pdfRenderedEvent(@Observes @Backend("pdf") AsciidocMessageEvent event){
		//NOT YET IMPLEMENTED
	}


}
