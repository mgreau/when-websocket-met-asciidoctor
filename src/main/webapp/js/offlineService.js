
//Service to handle Offline mode
// it uses IndexedDB to store files locally
app.factory('OfflineService', function($window, WebSocketService, IDB) {
	
	var service = {};
	var LIST_O_STUFF = "adocStore";

    var myDefaultList = [
        {
            id: 1,
            name: "adoc1",
            param: "a non-key, non-index parameter"
        }
    ];

    service.listOThings = [];

    service.addItem = function(item){
        IDB.put(LIST_O_STUFF, item);
    };
    
    service.getItem = function(key){
        IDB.getItem(LIST_O_STUFF, key);
    };

    service.removeAll = function(){
        IDB.removeAll(LIST_O_STUFF);
    };

    service.removeItem = function(id) {
        IDB.remove(LIST_O_STUFF, id);
    };

    service.update = function (data) {
        $scope.$apply(function () {
            console.log('update, apply', data);
            $scope.listOThings = data;
            if (!$scope.listOThings || $scope.listOThings.length <= 0) {
                $scope.listOThings = [];
                IDB.batchInsert(LIST_O_STUFF, myDefaultList);
            }
        });
    };

    service.dbupdate = function (event, args) {
        console.log("list-o-things DBUPDATE");
        console.log('args', args);
        var dbname = args[0],
            storeName = args[1],
            data = args[2];
        console.log('update', dbname, storeName, data);
        if (dbname === dbParams.name && LIST_O_STUFF === storeName)
            this.update(data);
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
            this.getAllThings(transaction);
    };

    // This callback is for after the database is initialized the first time
    service.postInitDb = function (event, data) {
        var dbname = data[0],
            transaction = data[1];
        console.log('postInit', dbname, transaction);
        if (dbname !== dbParams.name)
            return;

        this.getAllThings(transaction);
    };


    (function () {
        // if the db has not been initialized, then the listeners should work
        if (!IDB.db)
            return;
        // if the db has been initialized, then the listeners won't get the events,
        // and we need to just do a request immediately
        service.getAllThings();
    })();
    
    return service;
});
