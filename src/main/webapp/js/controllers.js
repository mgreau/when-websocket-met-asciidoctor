app.controller("LiveWritingCtrl", function($scope, DocRESTService, WriterService, WebSocketService) {
	
	//Live Writing Docs
	$scope.lwDocs = new Object();

	DocRESTService.async().then(function(datas) {
	    		$scope.lwDocs["1234"] = new Object();
	    		$scope.lwDocs["1234"].key = "1234";
	    		$scope.lwDocs["1234"].status = 'DISCONNECTED';
	    		$scope.lwDocs["1234"].adocSrc = datas;
	    		$scope.lwDocs["1234"].state = "Init Asciidoc source.";
	});

	//Messages sent by peer server are handled here
	WebSocketService.subscribe(function(idAdoc, message) {
		try {
			var obj = JSON.parse(message);

			//Asciidoc message from server
			if (obj.hasOwnProperty("adoc")){
				$scope.lwDocs[idAdoc].adoc = obj.adoc;
				$scope.lwDocs[idAdoc].adocSrc = obj.adoc.source;
				$scope.lwDocs[idAdoc].state = "Get last Asciidoc version";
				$scope.lwDocs[idAdoc].key = idAdoc;
			} 
			//Html5 output Message from server
			else if (obj.hasOwnProperty("html5Backend")){
				$scope.lwDocs[idAdoc].html5 = obj.html5Backend;
				$scope.lwDocs[idAdoc].state = "New HTML5 output version";
				$scope.lwDocs[idAdoc].key = idAdoc;
			}

		} catch (exception) {
			//Message WebSocket lifcycle
			$scope.lwDocs[idAdoc].status = message;
			console.log(message);
		}
		$scope.$apply();
	});
	
	$scope.sendAdoc = function(idAdoc) {
		WebSocketService.sendAdocSource(idAdoc, $scope.lwDocs[idAdoc].adocSrc, "max");
	};

	$scope.connect = function(idAdoc) {
		WebSocketService.connect(idAdoc);
	};

	$scope.disconnect = function(idAdoc) {
		WebSocketService.disconnect(idAdoc);
	};

});