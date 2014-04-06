package com.mgreau.wwsmad.asciidoctor;

import java.util.Map;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * #12 : iframe : making linking between anchors work.
 * 
 * @author greau.maxime@gmail.com
 *
 */
public class IFrameAnchorPostProcessor extends Postprocessor {



    public IFrameAnchorPostProcessor(Map<String, Object> config) {
        super(config);
    }

    @Override
    public String process(Document document, String output) {
    	
    	if ("html5".equals(document.getAttributes().get("backend"))){
    		
	        final org.jsoup.nodes.Document doc = Jsoup.parse(output);
	        final String js = "js/iframe/iframe_anchors.js";
	        
	        if (doc.getElementsByAttributeValue("src", js).size() ==  0){
	        	 Element head = doc.getElementsByTag("head").first();
	
	        	 head.appendElement("script").attr("type", "text/javascript").attr("src", "http://code.jquery.com/jquery-1.7.2.min.js");
	             head.appendElement("script").attr("type", "text/javascript").attr("src", js);
	        }
	        return doc.html();
    	}
    	return output;
    }

	//@Override
	public String process(String output) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
