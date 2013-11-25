package com.mgreau.wwsmad.websocket.decoders;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wwsmad.websocket.messages.AsciidocMessage;

public class MessageDecoder implements Decoder.Text<AsciidocMessage> {
	
	@Inject
	private Logger logger;
	
    /** Stores the name-value pairs from a JSON message as a Map */
    private Map<String,String> messageMap;

    @Override
    public void init(EndpointConfig ec) { }
    
    @Override
    public void destroy() { }
    
    /* Create a new Message object if the message can be decoded */
    @Override
    public AsciidocMessage decode(String string) throws DecodeException {
        AsciidocMessage msg = null;
        if (willDecode(string)) {
            switch (messageMap.get("type")) {
                case "adoc":
                    msg = new AsciidocMessage(messageMap.get("writer"), messageMap.get("source"));
                break;
                case "adoc-for-diff":
                    msg = new AsciidocMessage(messageMap.get("writer"), messageMap.get("source"));
                    msg.setAdocSourceToMerge(messageMap.get("sourceToMerge"));
                    msg.setAction("diff");
                break;
                case "adoc-for-patch":
                    msg = new AsciidocMessage(messageMap.get("writer"), messageMap.get("source"));
                    msg.setPatchToApply(messageMap.get("patch"));
                    msg.setAction("patch");
                break;
            }
        } else {
        	logger.severe(string);
            throw new DecodeException(string, "[Message] Can't decode.");
        }
        return msg;
    }
    
    /* Decode a JSON message into a Map and check if it contains
     * all the required fields according to its type. */
    @Override
    public boolean willDecode(String string) {
        boolean decodes = false;
        /* Convert the message into a map */
        messageMap = new HashMap<>();
        JsonParser parser = Json.createParser(new StringReader(string));
        while (parser.hasNext()) {
            if (parser.next() == JsonParser.Event.KEY_NAME) {
                String key = parser.getString();
                parser.next();
                String value = parser.getString();
                messageMap.put(key, value);
            }
        }
        /* Check the kind of message and if all fields are included */
        Set keys = messageMap.keySet();
        if (keys.contains("type")) {
            switch (messageMap.get("type")) {
                case "adoc":
                    if (keys.contains("source"))
                        decodes = true;
                break;
                case "adoc-for-diff":
                    if (keys.contains("source") && keys.contains("sourceToMerge") )
                        decodes = true;
                break;
                case "adoc-for-patch":
                    if (keys.contains("source") && keys.contains("patch") )
                        decodes = true;
                break;
            }
        }
        return decodes;
    }
}
