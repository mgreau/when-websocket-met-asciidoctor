package com.mgreau.wwsmad.asciidoctor;

import org.asciidoctor.extension.Postprocessor;
import org.asciidoctor.internal.DocumentRuby;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * #12 : iframe : making linking between anchors work.
 * 
 * @author greau.maxime@gmail.com
 *
 */
public class IFrameAnchorPostProcessor extends Postprocessor {

    public IFrameAnchorPostProcessor(DocumentRuby documentRuby) {
        super(documentRuby);
    }

    @Override
    public String process(String output) {
    
        final Document document = Jsoup.parse(output);
        final String js = "js/iframe/iframe_anchors.js";
        
        if (document.getElementsByAttributeValue("src", js).size() ==  0){
        	 Element head = document.getElementsByTag("head").first();

        	 head.appendElement("script").attr("type", "text/javascript").attr("src", "http://code.jquery.com/jquery-1.7.2.min.js");
             head.appendElement("script").attr("type", "text/javascript").attr("src", js);
        }
        
        return document.html();
    }
    
}
