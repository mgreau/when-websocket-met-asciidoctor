package com.mgreau.wwsmad.websocket.messages;

/**
 * 
 * All messages type available that can be exchange between peers, so that the JSON message
 * can be like that :
 * <pre>
 * {
    "type": "output", 
    "data": [                   
        {
            "format": "html5",
            "content": "<DOCTYPE...",
            "nbWriters": [],
            "auctionId": "first" 
        }
      ],        
    "adocId": "12"       
}
 * </pre>
 * 
 * @author mgreau
 * 
 */
public enum TypeMessage {

	notification, snapshot, diff, patch, output;
	
}
