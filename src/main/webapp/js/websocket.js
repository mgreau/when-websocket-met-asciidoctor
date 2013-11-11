var wsUrl;
var idAdoc = '1234';

var appPath = window.location.pathname.split('/')[1];
var host = window.location.hostname;
var port = "8000";

if (host == 'localhost') {
	port = '8080';
}

if (window.location.protocol == 'https:') {
	port = '8443';
	wsUrl = 'wss://' + host + ':'+ port +'/' + appPath +'/adoc/'+idAdoc;
} else {
	wsUrl = 'ws://' + host + ':'+ port +'/' + appPath +'/adoc/'+idAdoc;
}
//wsUrl = 'ws://localhost:8080/usopen/matches/1234';

var socket; // websocket
//Adoc file
var adocSource; 
// HTML output 
var htmlSource, author, timeToRender;

function connect() {
	iniHtmlElements();
	createWebSocket(wsUrl);
}

function iniHtmlElements() {
	adocSource = document.getElementById("adocSource");
	htmlSource = document.getElementById("htmlSource");
	author = document.getElementById("author");
	timeToRender = document.getElementById("timeToRender");
}

function createWebSocket(host) {
	if (!window.WebSocket) {
		var spanError = document.createElement('span');
		spanError.setAttribute('class', 'alert alert-danger');
		spanError.innerHTML = "WebSocket is not supported by your browser!";
		document.body.appendChild(spanError);
		return false;
	} else {
		socket = new WebSocket(host);
		socket.onopen = function() {
			document.getElementById("status").innerHTML = 'CONNECTED';
		};
		socket.onclose = function() {
			document.getElementById("status").innerHTML = 'DISCONNECTED';
		};
		socket.onerror = function() {
			document.getElementById("status").innerHTML = 'ERROR - Please refresh this page';
		};
		socket.onmessage = function(msg) {
			try { 
				console.log(data);
				var obj = JSON.parse(msg.data);
				
				if (obj.hasOwnProperty("adoc")){
					adocSource.innerHTML = obj.adoc.source;
				}
				else if (obj.hasOwnProperty("html")){
					htmlSource.srcdoc = obj.html.source;
					author.innerHTML = obj.html.author;
					timeToRender.innerHTML = obj.html.timeToRender;
				}

			} catch (exception) {
				data = msg.data;
				console.log(data);
			}
		};
	}
}
function sendAdoc() {
	var source = document.getElementById("adocSource").value;
	var jsonObj = {"type" : "adoc", "source" : source, "author": "max"};
	var msg = JSON.stringify(jsonObj);
	console.log(msg);

	socket.send(msg);
}


window.addEventListener("load", connect, false);