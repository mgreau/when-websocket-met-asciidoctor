package com.mgreau.wwsmad.asciidoctor;

import java.util.Map;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * DZSlides headers for one slide.
 * 
 * <head> 
  <meta charset="UTF-8"> 
  <meta name="generator" content="Asciidoctor 0.1.4, dzslides backend"> 
  <title>Real-time collaborative editor for AsciiDoc</title> 
  <meta name="author" content="@mgreau"> 
  <meta name="copyright" content="CC BY-SA 2.0"> 
  <meta name="presdate" content="Apr 16, 2014"> 
  <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:400,700,200,300"> 
  <link rel="stylesheet" href="./dzslides/themes/highlight/asciidoctor.css"> 
  <link rel="stylesheet" href="./dzslides/themes/style/devnation.css"> 
  <style>section:not(.topic) > h2 { display: none;}</style> 
  <link rel="stylesheet" href="./dzslides/core/dzslides.css"> 
  <link rel="stylesheet" href="./dzslides/themes/transition/fade.css"> 
  <script type="text/javascript" src="http://code.jquery.com/jquery-1.7.2.min.js"></script> 
  <script type="text/javascript" src="js/iframe/iframe_anchors.js"></script> 
 </head>
 
 * @author greau.maxime@gmail.com
 *
 */
public class DZSlidesPostProcessor extends Postprocessor {



    public DZSlidesPostProcessor(Map<String, Object> config) {
        super(config);
    }

    @Override
    public String process(Document document, String output) {
    
        final org.jsoup.nodes.Document doc = Jsoup.parse(output);
        
    	 Element head = doc.getElementsByTag("head").first();

    	 head.appendElement("link").attr("rel", "stylesheet").attr("href", "./dzslides/themes/highlight/asciidoctor.css");
    	 head.appendElement("link").attr("rel", "stylesheet").attr("href", "./dzslides/core/dzslides.css");
    	 head.appendElement("link").attr("rel", "stylesheet").attr("href", "./dzslides/themes/style/devnation.css");
         
        return doc.html();
    }

	//@Override
	public String process(String output) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
