app.controller("RCEAdocCtrl", function($scope, $rootScope, DocRESTService, OfflineService, WebSocketService, IDB) {
	
	//RCEAdoc : Realtime Collaborative Editor for Asciidoctor
	$scope.rceAdocs = new Object();
	$scope.isEditorActivate = false;
	$scope.isEvtOnChangeActivate = false;
	$scope.isDiffOnEditor = false;
	
	//Editor buttons
	$scope.radioModel = 'onCtrlS';
	
	//Progress Bar
	$scope.max = 100;
	$scope.dynamic = 100;
    $scope.type = 'success';
    
	//TODO : handle private space
	var spaceID = "1234";
	
	//First call fore each client 
	DocRESTService.async().then(function(datas) {
	    		$scope.rceAdocs[spaceID] = new Object();
	    		$scope.rceAdocs[spaceID].key = spaceID;
	    		$scope.rceAdocs[spaceID].status = 'DISCONNECTED';
	    		$scope.rceAdocs[spaceID].adocSrc = datas;
	    		$scope.rceAdocs[spaceID].state = "WELCOME ! You can create a new space OR join a team.";
	    		$scope.rceAdocs[spaceID].author = "";
	    		$scope.addAlert("info", $scope.rceAdocs[spaceID].state);
	});
	
	$scope.aceLoaded = function(_editor) {
		$scope.editor = _editor;
	    // Options
		$scope.editor.setReadOnly(true);
		$scope.editor.insert($scope.rceAdocs[spaceID].adocSrc);
		
		$scope.editor.commands.addCommand({
			    name: 'sendAsciidocToServer',
			    bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
			    exec: function(editor) {
			    	if ($scope.isDiffOnEditor === true){
						  $scope.rceAdocs[spaceID].state = "Diff are loaded, apply it or unload it by click on Compute diff.";
						  $scope.addAlert("info", $scope.rceAdocs[idAdoc].state);
					 }
					 else {
				    	if ($scope.isEvtOnChangeActivate === false){
					    	$scope.rceAdocs[spaceID].adocSrc = editor.getValue();
							$scope.sendAdoc(spaceID);
				    	}
					 }
			    },
			    readOnly: false // false if this command should not apply in readOnly mode
			});
	  };

	  //Evt when editor value change
	  $scope.aceChanged = function(e) {
		  if ($scope.isDiffOnEditor === true){
			  $scope.rceAdocs[spaceID].state = "Diff are loaded, apply it or unload it by clicking the 'Compute diff' button.";
			  $scope.addAlert("info", $scope.rceAdocs[idAdoc].state);
		  }
		  else {
			  if ($scope.isEvtOnChangeActivate === true){
				  $scope.rceAdocs[spaceID].adocSrc = $scope.editor.getValue();
				  $scope.sendAdoc(spaceID);
			  }
		  }
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
				$scope.rceAdocs[idAdoc].adoc = obj.data;
				$scope.editor.setValue(obj.data.source);
				$scope.isDiffOnEditor = false;
				$scope.rceAdocs[idAdoc].state = "Just Get last Asciidoc version";
				$scope.rceAdocs[idAdoc].key = idAdoc;
			} 
			else if (angular.equals(obj.type, "patch")){
				$scope.rceAdocs[idAdoc].adoc = obj.data;
				$scope.editor.setValue(obj.data.sourceToMerge);
				$scope.isDiffOnEditor = false;
				$scope.rceAdocs[idAdoc].state = "Patch Apply!";
				$scope.rceAdocs[idAdoc].key = idAdoc;
			} 
			else if (angular.equals(obj.type, "diff")){
				$scope.rceAdocs[idAdoc].adoc = obj.data;
				//receive diff
				$scope.isDiffOnEditor = true;
				$scope.editor.setValue(obj.data.sourceToMerge);
				$scope.rceAdocs[idAdoc].state = "Diff";
				$scope.rceAdocs[idAdoc].key = idAdoc;
			} 
			// output Message from server
			else if (angular.equals(obj.type, "output")){
				$scope.rceAdocs[idAdoc].html5 = obj.data;
				$scope.rceAdocs[idAdoc].state = "New HTML5 output version";
				$scope.rceAdocs[idAdoc].key = idAdoc;
				//progress bar to 100
				$scope.dynamic = 100;
			}
			else if (angular.equals(obj.type, "notification")){
				$scope.rceAdocs[idAdoc].notification = obj.data;
				$scope.rceAdocs[idAdoc].key = idAdoc;
			}
			
			$scope.addAlert("success", $scope.rceAdocs[idAdoc].state);

		} catch (exception) {
			//Message WebSocket lifecycle
			$scope.rceAdocs[idAdoc].status = message;
			console.log(message);
		}
		$scope.$apply();
	});
	
	//Send the asciidoc file to the server in order to see the ouput result
	$scope.sendAdoc = function(idAdoc) {
		if (angular.equals(WebSocketService.status(idAdoc), WebSocket.OPEN)){
			if(angular.isUndefined($scope.rceAdocs[idAdoc].author) || angular.equals($scope.rceAdocs[idAdoc].author,"")){
				$scope.rceAdocs[idAdoc].state = "You need to add an author name.";
				return
			}
			//progress bar to 0
			$scope.dynamic = 0;
			WebSocketService.sendAdocSource(idAdoc, $scope.rceAdocs[idAdoc].adocSrc, $scope.rceAdocs[idAdoc].author);
		}
		else {
			$scope.rceAdocs[idAdoc].state = "You work on OFFLINE MODE !!. You need to CONNECT to do this action.";
			$scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
		}
	};
	
	
	//Load the asciidoc source associated to the last output, to the source editor
	$scope.loadLastAdoc = function(idAdoc) {
		if (angular.isUndefined($scope.rceAdocs[idAdoc].html5.source)){
			console.log("No html5.source content");
			$scope.rceAdocs[idAdoc].state = "You already have the last version.";
			$scope.addAlert("info", $scope.rceAdocs[idAdoc].state);
		}
		else {
			$scope.rceAdocs[idAdoc].adocSrc = $scope.rceAdocs[idAdoc].html5.source;
			$scope.editor.setValue($scope.rceAdocs[idAdoc].adocSrc);
			$scope.rceAdocs[idAdoc].state = "Last asciidoc source loaded !!.";
			$scope.addAlert("success", $scope.rceAdocs[idAdoc].state);
		}
	};
	
	$scope.applyPatch = function(idAdoc) {
		if (angular.equals(WebSocketService.status(idAdoc), WebSocket.OPEN)){
			if(angular.isUndefined($scope.rceAdocs[idAdoc].author) || angular.equals($scope.rceAdocs[idAdoc].author,"")){
				$scope.rceAdocs[idAdoc].state = "You need to add an author name.";
				$scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
				return
			}
			if ($scope.isDiffOnEditor === false){
				  $scope.rceAdocs[spaceID].state = "No Patch to apply.";
				  $scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
				  return;
			}
			$scope.isDiffOnEditor = false;
			$scope.rceAdocs[idAdoc].state = "Patch Apply !";
			WebSocketService.sendAdocSourceToApplyPatch(idAdoc, $scope.rceAdocs[idAdoc].adocSrc, 
					$scope.rceAdocs[idAdoc].author, $scope.rceAdocs[idAdoc].adoc.sourceToMerge);
		}
		else {
			$scope.rceAdocs[idAdoc].state = "You work on OFFLINE MODE !!. You need to CONNECT to do this action.";
			$scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
		}
	};
	
	//Show diff between the asciidoc source and asciidoc sent by another writer
	$scope.computeDiff = function(idAdoc) {
		if ($scope.isDiffOnEditor === true){
			$scope.isDiffOnEditor = false;
			$scope.editor.setValue($scope.rceAdocs[idAdoc].adocSrc);
			return;
		}
		if (angular.equals(WebSocketService.status(idAdoc), WebSocket.OPEN)){
			if(angular.isUndefined($scope.rceAdocs[idAdoc].author) || angular.equals($scope.rceAdocs[idAdoc].author,"")){
				$scope.rceAdocs[idAdoc].state = "You need to add an author name.";
				$scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
				return;
			} else if (angular.isUndefined($scope.rceAdocs[idAdoc].html5) 
					|| angular.isUndefined($scope.rceAdocs[idAdoc].html5.source) || angular.equals($scope.rceAdocs[idAdoc].html5.source,"")){
				$scope.rceAdocs[idAdoc].state = "No source to compare with.";
				$scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
				return;
			} else if (angular.equals($scope.rceAdocs[idAdoc].html5.currentWriter,$scope.rceAdocs[idAdoc].author)){
				$scope.rceAdocs[idAdoc].state = "You are the last writer, no need to compute diff.";
				$scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
				return;
			}
			$scope.rceAdocs[idAdoc].adocSrc = $scope.editor.getValue();
			WebSocketService.sendAdocSourceForDiff(idAdoc, $scope.rceAdocs[idAdoc].adocSrc, $scope.rceAdocs[idAdoc].author, $scope.rceAdocs[idAdoc].html5.source);
		}
		else {
			$scope.rceAdocs[idAdoc].state = "You work on OFFLINE MODE !!. You need to be CONNECTED to do this action.";
			$scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
		}
	};
	
	//
	$scope.enableEditor = function(idAdoc) {
		if(angular.isUndefined($scope.rceAdocs[idAdoc].author) || angular.equals($scope.rceAdocs[idAdoc].author,"")){
			$scope.rceAdocs[idAdoc].state = "You need to add an author name!!";
			$scope.addAlert("danger", $scope.rceAdocs[idAdoc].state);
			return
		}
		$scope.editor.setReadOnly(false);
		$scope.isEditorActivate = true;
		$scope.rceAdocs[idAdoc].state = "You can write the doc and send your version on Ctrl+S command OR on each change.";
		$scope.addAlert("success", $scope.rceAdocs[idAdoc].state);
	};

	//WebSocket connection in order to send data to the server
	$scope.connect = function(idAdoc) {
		WebSocketService.connect(idAdoc);
	};
	
	//Disconnect from the server, work offline ?
	$scope.disconnect = function(idAdoc) {
		WebSocketService.disconnect(idAdoc);
		//Activate the offline mode with storage
		$scope.addAlert("warning", "You are working on Offline, don't forget to Save on disk!");
	};
	
	//Alert messages
	$scope.alerts = [];
    
    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
      };
      
	$scope.addAlert = function(typeAlert, message) {
		$scope.alerts = [];
		$scope.alerts.push({ type: typeAlert, msg: message});
	};
	
	//Save the editor content into IndexDB
	$scope.saveOnDisk = function(idAdoc) {
		var data =   {
	            id: idAdoc,
	            name: "adocSource",
	            param: $scope.editor.getValue()
	        };
	  OfflineService.addItem(data);
	};
	
	//Load the latest backup from IndexDB into editor
	$scope.loadFromDisk = function(idAdoc) {
        var data = OfflineService.getItem(1);
        $scope.editor.setValue(data.param);
        $scope.addAlert("success", "Last backup asciidoc source loaded !");
	};
	
	$rootScope.$on('failure', function () {
        console.log('failed to open db');
    });
    $rootScope.$on('dbopenupgrade', OfflineService.postInitDb);
    $rootScope.$on('dbopen', OfflineService.postInitDb);

    $rootScope.$on('getinit', OfflineService.dbupdate);
    $rootScope.$on('getall', OfflineService.dbupdate);
    $rootScope.$on('remove', OfflineService.getAll);
    $rootScope.$on('put', OfflineService.getAll);
    $rootScope.$on('clear', OfflineService.getAll);
    $rootScope.$on('batchinsert', OfflineService.getAll);
    
    (function () {
        // if the db has not been initialized, then the listeners should work
        if (!IDB.db)
            return;
        // if the db has been initialized, then the listeners won't get the events,
        // and we need to just do a request immediately
        OfflineService.getAllThings();
    })();

});