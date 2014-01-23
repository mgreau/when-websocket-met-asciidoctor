//wWSmAD : when WebSocket met Asciidoctor
var app = angular.module('wWSmADapp', ['ui.ace', 'ui.bootstrap']).config(function($sceProvider) {
	  // Completely disable SCE.  For demonstration purposes only!
	  // Do not use in new projects.
	  $sceProvider.enabled(false);
	});
