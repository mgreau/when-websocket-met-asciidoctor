app.controller("LiveWritingCtrl", function($scope, DocRESTService, WriterService, WebSocketService) {
	
	//Live Writing Docs
	$scope.lwDocs = new Object();

	DocRESTService.async().then(function(datas) {
	    		$scope.lwDocs["1234"] = new Object();
	    		$scope.lwDocs["1234"].key = "1234";
	    		$scope.lwDocs["1234"].status = 'DISCONNECTED';
	    		$scope.lwDocs["1234"].adocSrc = datas;
	    		$scope.lwDocs["1234"].state = "Init Asciidoc source.";
	    		$scope.lwDocs["1234"].auhtor = "";
	});

	//Messages sent by peer server are handled here
	WebSocketService.subscribe(function(idAdoc, message) {
		try {
			var obj = JSON.parse(message);

			//Asciidoc message from server (get last snapshot)
			if (angular.equals(obj.type, "snapshot")){
				$scope.lwDocs[idAdoc].adoc = obj.data;
				$scope.lwDocs[idAdoc].adocSrc = obj.data.source;
				$scope.lwDocs[idAdoc].state = "Get last Asciidoc version";
				$scope.lwDocs[idAdoc].key = idAdoc;
				$scope.lwDocs[idAdoc].auhtor = obj.data.currentWriter;
			} 
			// output Message from server
			else if (angular.equals(obj.type, "output")){
				$scope.lwDocs[idAdoc].html5 = obj.data;
				$scope.lwDocs[idAdoc].state = "New HTML5 output version";
				$scope.lwDocs[idAdoc].key = idAdoc;
			}
			else if (angular.equals(obj.type, "notification")){
				$scope.lwDocs[idAdoc].notification = obj.data;
				$scope.lwDocs[idAdoc].state = "Notification";
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
		if (angular.equals(WebSocketService.status(idAdoc), WebSocket.OPEN)){
			WebSocketService.sendAdocSource(idAdoc, $scope.lwDocs[idAdoc].adocSrc, $scope.lwDocs[idAdoc].author);
		}
		else {
			console.log("CONNECTION CLOSED, Don't send message");
		}
	};

	$scope.connect = function(idAdoc) {
		WebSocketService.connect(idAdoc);
	};

	$scope.disconnect = function(idAdoc) {
		WebSocketService.disconnect(idAdoc);
	};

});