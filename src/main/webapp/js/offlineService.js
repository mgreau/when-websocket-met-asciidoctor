
//Service to handle Offline mode
// it uses IndexedDB to store files locally
app.factory('OfflineService', function($rootScope, $window, WebSocketService, IDB) {
	
	var service = {};
	var LIST_O_STUFF = "adocStore";
	
    service.addItem = function(item){
        IDB.put(LIST_O_STUFF, item);
    };
    
    service.getItem = function(adSpaceID){
    	var res ="";

    	listOThings.every(function(element, index, array) {
    	    if (element.id == adSpaceID) {
    	    	res = element;
    	        return false;
    	    }
    	    return true;

    	});
        return res;
    };

    service.removeAll = function(){
        IDB.removeAll(LIST_O_STUFF);
    };

    service.removeItem = function(id) {
        IDB.remove(LIST_O_STUFF, id);
    };
    
    var myDefaultList = [
        
    ];

    var listOThings = [];
	
	service.update = function (data) {
        $rootScope.$apply(function () {
            listOThings = data;
            if (!listOThings || listOThings.length <= 0) {
                listOThings = [];
                IDB.batchInsert(LIST_O_STUFF, myDefaultList);
            }
        });
    };

    service.dbupdate = function (event, args) {
        var dbname = args[0],
            storeName = args[1],
            data = args[2];
        if (dbname === dbParams.name && LIST_O_STUFF === storeName)
            service.update(data);
    };

    service.getAllThings = function (transaction) {
        if (transaction instanceof IDBTransaction)
            IDB.getInit(transaction, LIST_O_STUFF);
        else
            IDB.getAll(LIST_O_STUFF);
    };

    service.getAll = function (event, data) {
        var dbname = data[0],
            storeName = data[1],
            transaction = data[2];
        if (dbname === dbParams.name && LIST_O_STUFF === storeName)
            service.getAllThings(transaction);
    };

    // This callback is for after the database is initialized the first time
    service.postInitDb = function (event, data) {
        var dbname = data[0],
            transaction = data[1];
        if (dbname !== dbParams.name)
            return;
        service.getAllThings(transaction);
    };
    
    
    service.getOfflineHTML5 = function (adocSrc){
    	var asciidoctorOptions = "Opal.hash2(['attributes'], {'attributes': '" + service.buildAsciidoctorOptions() + "' }) ";
    	var strVar="";
    	strVar += "<!DOCTYPE html>";
    	strVar += "  <html>";
    	strVar += "  <head>";
    	strVar += "    <meta http-equiv=\"Content-Type\" content=\"text\/html; charset=UTF-8\">";
    	strVar += "    <title>Asciidoctor in JavaScript powered by Opal<\/title>";
    	strVar += "    <link rel=\"stylesheet\" href=\"offline\/asciidoctor.css\">";
    	strVar += "  <\/head>";
    	strVar += "  <body>";
    	strVar += "    <div id=\"content\">";
    	strVar += "    <\/div>";
    	strVar += "    <script src=\"offline\/opal.js\"><\/script>";
    	strVar += "    <script src=\"offline\/asciidoctor.js\"><\/script>";
    	strVar += "    <script>";
    	strVar += "      var generatedHtml = undefined;";
		strVar += "      try{ ";
		strVar += "      		asciidoctorDocument = Opal.Asciidoctor.$load(\"";
	    strVar += service.stringEncode(adocSrc) ;
	    strVar += "\", ";
	    strVar += asciidoctorOptions;
	    strVar += ");";
	    strVar += "			   generatedHtml = asciidoctorDocument.$render();";
	    strVar += "		 }catch (e) {generatedHtml='Rendering error : ' + e.name + ':' + e.message};";
    	strVar += "      document.getElementById('content').innerHTML = generatedHtml;";
    	strVar += "    <\/script>";
    	strVar += "  <\/body>";
    	strVar += "<\/html>";
    	strVar += "";
    	return strVar;

    };
    
    /**
     * Build Asciidoctor options
     */
    service.buildAsciidoctorOptions = function (items) {
        var customAttributes = '';
        if (items){
        	customAttributes = items['CUSTOM_ATTRIBUTES_KEY'];
        }
        var defaultAttributes = 'showtitle toc2 showauthor icons=font@';
        if (customAttributes) {
            attributes = defaultAttributes.concat(' ').concat(customAttributes);
        } else {
            attributes = defaultAttributes;
        }
        return attributes;
    };
    
	service.stringEncode = function (preescape) {
		var escaped="";
		var i=0;
		for(i=0;i<preescape.length;i++)
		{
			escaped=escaped+service.encodeCharx(preescape.charAt(i));
		}
		return escaped;
	};
	
	
	service.encodeCharx = function(original) {
		var hex=new Array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f');
	   	var found=true;
	   	var thecharchar=original.charAt(0);
	   	var thechar=original.charCodeAt(0);
		switch(thecharchar) {
				case '\n': return "\\n"; break; //newline
				case '\r': return "\\r"; break; //Carriage return
				case '\'': return "\\'"; break;
				case '"': return "\\\""; break;
				case '\&': return "\\&"; break;
				case '\\': return "\\\\"; break;
				case '\t': return "\\t"; break;
				case '\b': return "\\b"; break;
				case '\f': return "\\f"; break;
				case '/': return "\\x2F"; break;
				case '<': return "\\x3C"; break;
				case '>': return "\\x3E"; break;
				default:
					found=false;
					break;
			}
			if(!found)
			{
				if(thechar>127) {
					var c=thechar;
					var a4=c%16;
					c=Math.floor(c/16); 
					var a3=c%16;
					c=Math.floor(c/16);
					var a2=c%16;
					c=Math.floor(c/16);
					var a1=c%16;
				//	alert(a1);
					return "\\u"+hex[a1]+hex[a2]+hex[a3]+hex[a4]+"";		
				}
				else
				{
					return original;
				}
			}
		};
			

    return service;
});
