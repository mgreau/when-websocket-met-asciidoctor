app.directive("html5render", function(){
	return{
		restrict: 'AE',
		templateUrl: "templates/html5-render.html",
		replace: true,
		scope: false,
		link: function (scope, element, attrs, ctrl) {
			scope.theHtml5 = [];
			scope.theAdocId = "";
    		scope.$watch(attrs.html5, function(newVal, oldVal) {
        		scope.theHtml5 = newVal;
        		//#
        		//var range = angular.element(document.querySelector('#html5-rendered'))[0].contentWindow.getSelection().getRangeAt(0);
    		});

    		scope.$watch(attrs.key, function(newVal, oldVal) {
        		scope.theAdocId = newVal;
    		});

		}
	};
});

app.directive("asciidoceditor", function(){
	return{
		restrict: 'AE',
		templateUrl: "templates/asciidoc-editor.html",
		replace: true,
		link: function (scope, element, attrs, ctrl) {
			scope.theAdocId = "";

			scope.$watch(attrs.key, function(newVal, oldVal) {
        		scope.theAdocId = newVal;
    		});

		}
	};
});

app.directive("collaborativemessage", function(){
	return{
		restrict: 'AE',
		templateUrl: "templates/collaborative-msg.html",
		replace: true,
		link: function (scope, element, attrs, ctrl) {
			scope.theDoc = "";

			scope.$watch(attrs.doc, function(newVal, oldVal) {
        		scope.theDoc = newVal;
    		});

		}
	};
});

app.directive("auth", function(){
	return{
		restrict: 'AE',
		templateUrl: "templates/auth.html",
		replace: true,
		link: function (scope, element, attrs, ctrl) {
			
			scope.theNewDoc = "";

			scope.$watch(attrs.doc, function(newVal, oldVal) {
        		scope.theNewDoc = newVal;
    		});
			
		}
	};
});



