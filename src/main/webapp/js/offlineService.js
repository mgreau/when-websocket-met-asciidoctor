
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
    	    console.log("element:", element);
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
            console.log('update, apply', data);
            listOThings = data;
            if (!listOThings || listOThings.length <= 0) {
                listOThings = [];
                IDB.batchInsert(LIST_O_STUFF, myDefaultList);
            }
        });
    };

    service.dbupdate = function (event, args) {
        console.log('args', args);
        var dbname = args[0],
            storeName = args[1],
            data = args[2];
        console.log('update', dbname, storeName, data);
        if (dbname === dbParams.name && LIST_O_STUFF === storeName)
            service.update(data);
    };

    service.getAllThings = function (transaction) {
        console.log('getAllThings', transaction);
        if (transaction instanceof IDBTransaction)
            IDB.getInit(transaction, LIST_O_STUFF);
        else
            IDB.getAll(LIST_O_STUFF);
    };

    service.getAll = function (event, data) {
        console.log("things DBGETALL");
        var dbname = data[0],
            storeName = data[1],
            transaction = data[2];
        console.log('getAll', dbname, storeName, transaction);
        if (dbname === dbParams.name && LIST_O_STUFF === storeName)
            service.getAllThings(transaction);
    };

    // This callback is for after the database is initialized the first time
    service.postInitDb = function (event, data) {
        var dbname = data[0],
            transaction = data[1];
        console.log('postInit', dbname, transaction);
        if (dbname !== dbParams.name)
            return;
        service.getAllThings(transaction);
    };
    
    
    service.getOfflineHTML5 = function (adocSrc){
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
    	strVar += adocSrc;
    	strVar += "    <\/div>";
    	strVar += "    <script src=\"offline\/opal.js\"><\/script>";
    	strVar += "    <script src=\"offline\/asciidoctor.js\"><\/script>";
    	strVar += "    <script>";
    	strVar += "      var adoc = document.getElementById('content');";
    	strVar += "      Opal.hash2(['attributes'], {'attributes': ['notitle!']}); ";
    	strVar += "      document.getElementById('content').innerHTML = Opal.Asciidoctor.$render(adoc.innerHTML);";
    	strVar += "    <\/script>";
    	strVar += "  <\/body>";
    	strVar += "<\/html>";
    	strVar += "";
    	return strVar;

    };

    return service;
});
