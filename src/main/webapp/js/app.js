//wWSmAD : when WebSocket met Asciidoctor
var app = angular.module('wWSmADapp', ['ui.ace']).constant('Config', {
    apiPath: '/wWSmAD/rest',
    templatesPath: 'resources/templates'
  })
  .run(function ($rootScope, Session) {
    console.log(Session);
    $rootScope.session = Session;
    
    $rootScope.$on('sessionRefreshUser', function () {
      Session.refreshUser();
    });

    $rootScope.$on('sessionRefreshServices', function () {
      Session.refreshServices();
    });

    $rootScope.$on('sessionRefreshService', function (e, service) {
      Session.refreshService(service);
    });
    
    $rootScope.$on('sessionRefreshOAuth', function (e, session) {
      Session.refreshOAuth(session);
    });
    
    $rootScope.$on('sessionRefreshAll', function () {
      Session.refreshAll();
    });
    
    $rootScope.$emit('sessionRefreshAll');
  })
;
