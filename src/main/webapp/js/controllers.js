app.controller("LiveWritingCtrl", function($scope, DocRESTService, WriterService, WebSocketService) {
	
	//Live Writing Docs
	$scope.lwDocs = new Object();
	$scope.isEditorActivate = false;
	$scope.isEvtOnChangeActivate = false;

	DocRESTService.async().then(function(datas) {
	    		$scope.lwDocs["1234"] = new Object();
	    		$scope.lwDocs["1234"].key = "1234";
	    		$scope.lwDocs["1234"].status = 'DISCONNECTED';
	    		$scope.lwDocs["1234"].adocSrc = datas;
	    		$scope.lwDocs["1234"].state = "Init Asciidoc source.";
	    		$scope.lwDocs["1234"].author = "";
	});
	
	$scope.aceLoaded = function(_editor) {
		$scope.editor = _editor;
	    // Options
		$scope.editor.setReadOnly(true);
		$scope.editor.insert($scope.lwDocs["1234"].adocSrc);
		
		$scope.editor.commands.addCommand({
			    name: 'sendAsciidocToServer',
			    bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
			    exec: function(editor) {
			    	if ($scope.isEvtOnChangeActivate === false){
				    	$scope.lwDocs["1234"].adocSrc = editor.getValue();
						$scope.sendAdoc("1234");
			    	}
			    },
			    readOnly: false // false if this command should not apply in readOnly mode
			});
	  };

	  //Evt when editor value change
	  $scope.aceChanged = function(e) {
		  if ($scope.isEvtOnChangeActivate === true){
			  $scope.lwDocs["1234"].adocSrc = $scope.editor.getValue();
			  $scope.sendAdoc("1234");
		  }
		  //TODO handle is writing event
	  };
	  
	$scope.modeAdocOnChange = function(value) {
		$scope.isEvtOnChangeActivate = value;
		
	};

	//Messages sent by peer server are handled here
	WebSocketService.subscribe(function(idAdoc, message) {
		try {
			var obj = JSON.parse(message);

			//Asciidoc message from server (get last snapshot)
			if (angular.equals(obj.type, "snapshot")){
				$scope.lwDocs[idAdoc].adoc = obj.data;
				$scope.lwDocs[idAdoc].adocSrc = obj.data.source;
				$scope.lwDocs[idAdoc].state = "Just Get last Asciidoc version";
				$scope.lwDocs[idAdoc].key = idAdoc;
				$scope.lwDocs[idAdoc].author = obj.data.currentWriter;
			} 
			// output Message from server
			else if (angular.equals(obj.type, "output")){
				$scope.lwDocs[idAdoc].html5 = obj.data;
				$scope.lwDocs[idAdoc].state = "New HTML5 output version";
				$scope.lwDocs[idAdoc].key = idAdoc;
			}
			else if (angular.equals(obj.type, "notification")){
				$scope.lwDocs[idAdoc].notification = obj.data;
				$scope.lwDocs[idAdoc].state = "Notification received.";
				$scope.lwDocs[idAdoc].key = idAdoc;
			}

		} catch (exception) {
			//Message WebSocket lifecycle
			$scope.lwDocs[idAdoc].status = message;
			console.log(message);
		}
		$scope.$apply();
	});
	
	//Send the asciidoc file to the server in order to see the ouput result
	$scope.sendAdoc = function(idAdoc) {
		if (angular.equals(WebSocketService.status(idAdoc), WebSocket.OPEN)){
			if(angular.isUndefined($scope.lwDocs[idAdoc].author) || angular.equals($scope.lwDocs[idAdoc].author,"")){
				$scope.lwDocs[idAdoc].state = "You need to add an author name.";
				return
			}
			WebSocketService.sendAdocSource(idAdoc, $scope.lwDocs[idAdoc].adocSrc, $scope.lwDocs[idAdoc].author);
		}
		else {
			$scope.lwDocs[idAdoc].state = "You work on OFFLINE MODE !!.";
		}
	};
	
	//Load the asciidoc source associated to the last output, to the source editor
	$scope.loadLastAdoc = function(idAdoc) {
		if (angular.isUndefined($scope.lwDocs[idAdoc].html5.source)){
			console.log("No html5.source content");
			$scope.lwDocs[idAdoc].state = "You already have the last version.";
		}
		else {
			$scope.lwDocs[idAdoc].adocSrc = $scope.lwDocs[idAdoc].html5.source;
			$scope.editor.setValue($scope.lwDocs[idAdoc].adocSrc);
			$scope.lwDocs[idAdoc].state = "Last asciidoc source loaded !!.";
		}
	};
	
	//
	$scope.enableEditor = function(idAdoc) {
		if(angular.isUndefined($scope.lwDocs[idAdoc].author) || angular.equals($scope.lwDocs[idAdoc].author,"")){
			$scope.lwDocs[idAdoc].state = "You need to add an author name!!";
			return
		}
		$scope.editor.setReadOnly(false);
		$scope.isEditorActivate = true;
		$scope.lwDocs[idAdoc].state = "You can write your doc !";
	};

	//WebSocket connection in order to send data to the server
	$scope.connect = function(idAdoc) {
		WebSocketService.connect(idAdoc);
	};

	//Disconnect from the server, work offline ?
	$scope.disconnect = function(idAdoc) {
		WebSocketService.disconnect(idAdoc);
	};

});