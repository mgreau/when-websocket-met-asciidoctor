//wWSmAD : when WebSocket met Asciidoctor
'use strict';

var dbParams = {
    name: 'adocDB',
    version: 1,
    options: [
        {
            storeName: 'adocStore',
            keyPath: 'id',
            indexes: [
                { name: 'name', unique: false }
            ]
        }
    ]
};

var app = angular.module('wWSmADapp', 
		[	
		 	'ngResource',
		 	'ui.ace', 
		 	'ui.bootstrap',
		 	'angular-indexeddb'
	 	]
);

app.config(function($sceProvider) {
	  // Completely disable SCE.  For demonstration purposes only!
	  // Do not use in new projects.
	  $sceProvider.enabled(false);
});

app.run(['IDB', function (IDB) {
    IDB.openDB(dbParams.name, dbParams.version, dbParams.options);
}]);
