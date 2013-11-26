app.controller("LiveWritingCtrl", function($scope, DocRESTService, WriterService, WebSocketService) {
	
	//Real-time Collaborative Writing Docs
	$scope.lwDocs = new Object();
	$scope.isEditorActivate = false;
	$scope.isEvtOnChangeActivate = false;
	$scope.isDiffOnEditor = false;

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
			    	if ($scope.isDiffOnEditor === true){
						  $scope.lwDocs["1234"].state = "Diff are loaded, apply it or unload it by click on Compute diff.";
					 }
					 else {
				    	if ($scope.isEvtOnChangeActivate === false){
					    	$scope.lwDocs["1234"].adocSrc = editor.getValue();
							$scope.sendAdoc("1234");
				    	}
					 }
			    },
			    readOnly: false // false if this command should not apply in readOnly mode
			});
	  };

	  //Evt when editor value change
	  $scope.aceChanged = function(e) {
		  if ($scope.isDiffOnEditor === true){
			  $scope.lwDocs["1234"].state = "Diff are loaded, apply it or unload it by click on Compute diff.";
		  }
		  else {
			  if ($scope.isEvtOnChangeActivate === true){
				  $scope.lwDocs["1234"].adocSrc = $scope.editor.getValue();
				  $scope.sendAdoc("1234");
			  }
		  }
		  //TODO handle "is writing" event
	  };
	  
	$scope.modeAdocOnChange = function(value) {
		$scope.isEvtOnChangeActivate = value;
		
	};

	//Messages sent by peer server are handled here
	WebSocketService.subscribe(function(idAdoc, message) {
		try {
			var obj = JSON.parse(message);

			//Asciidoc message from server (last version from other writer or patch)
			if (angular.equals(obj.type, "snapshot")){
				$scope.lwDocs[idAdoc].adoc = obj.data;
				$scope.editor.setValue(obj.data.source);
				$scope.isDiffOnEditor = false;
				$scope.lwDocs[idAdoc].state = "Just Get last Asciidoc version";
				$scope.lwDocs[idAdoc].key = idAdoc;
			} 
			else if (angular.equals(obj.type, "patch")){
				$scope.lwDocs[idAdoc].adoc = obj.data;
				$scope.editor.setValue(obj.data.sourceToMerge);
				$scope.isDiffOnEditor = false;
				$scope.lwDocs[idAdoc].state = "Patch Apply!";
				$scope.lwDocs[idAdoc].key = idAdoc;
			} 
			else if (angular.equals(obj.type, "diff")){
				$scope.lwDocs[idAdoc].adoc = obj.data;
				//receive diff
				$scope.isDiffOnEditor = true;
				$scope.editor.setValue(obj.data.sourceToMerge);
				$scope.lwDocs[idAdoc].state = "Diff";
				$scope.lwDocs[idAdoc].key = idAdoc;
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
			$scope.lwDocs[idAdoc].state = "You work on OFFLINE MODE !!. You need to CONNECT to do this action.";
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
	
	$scope.applyPatch = function(idAdoc) {
		if (angular.equals(WebSocketService.status(idAdoc), WebSocket.OPEN)){
			if(angular.isUndefined($scope.lwDocs[idAdoc].author) || angular.equals($scope.lwDocs[idAdoc].author,"")){
				$scope.lwDocs[idAdoc].state = "You need to add an author name.";
				return
			}
			if ($scope.isDiffOnEditor === false){
				  $scope.lwDocs["1234"].state = "No Patch to apply.";
				  return;
			}
			$scope.isDiffOnEditor = false;
			$scope.lwDocs[idAdoc].state = "Patch Apply !";
			WebSocketService.sendAdocSourceToApplyPatch(idAdoc, $scope.lwDocs[idAdoc].adocSrc, 
					$scope.lwDocs[idAdoc].author, $scope.lwDocs[idAdoc].adoc.sourceToMerge);
		}
		else {
			$scope.lwDocs[idAdoc].state = "You work on OFFLINE MODE !!. You need to CONNECT to do this action.";
		}
	};
	
	//Show diff between the asciidoc source and asciidoc sent by another writer
	$scope.computeDiff = function(idAdoc) {
		if ($scope.isDiffOnEditor === true){
			$scope.isDiffOnEditor = false;
			$scope.editor.setValue($scope.lwDocs[idAdoc].adocSrc);
			return;
		}
		if (angular.equals(WebSocketService.status(idAdoc), WebSocket.OPEN)){
			if(angular.isUndefined($scope.lwDocs[idAdoc].author) || angular.equals($scope.lwDocs[idAdoc].author,"")){
				$scope.lwDocs[idAdoc].state = "You need to add an author name.";
				return;
			} else if (angular.isUndefined($scope.lwDocs[idAdoc].html5) 
					|| angular.isUndefined($scope.lwDocs[idAdoc].html5.source) || angular.equals($scope.lwDocs[idAdoc].html5.source,"")){
				$scope.lwDocs[idAdoc].state = "No source to compare with.";
				return;
			} else if (angular.equals($scope.lwDocs[idAdoc].html5.currentWriter,$scope.lwDocs[idAdoc].author)){
				$scope.lwDocs[idAdoc].state = "You are the last writer, no need to compute diff.";
				return;
			}
			$scope.lwDocs[idAdoc].adocSrc = $scope.editor.getValue();
			WebSocketService.sendAdocSourceForDiff(idAdoc, $scope.lwDocs[idAdoc].adocSrc, $scope.lwDocs[idAdoc].author, $scope.lwDocs[idAdoc].html5.source);
		}
		else {
			$scope.lwDocs[idAdoc].state = "You work on OFFLINE MODE !!. You need to CONNECT to do this action.";
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