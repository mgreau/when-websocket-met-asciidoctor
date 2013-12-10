app.factory('Session', function ($rootScope, Social) {
  var user = {},
      services = {};

  function refreshUser() {
    Social.currentUser().then(function (response) {
      console.log(response);
      $rootScope.session.user = response.data || {};
      console.log('new session',$rootScope.session);
    }, function (error) {
      console.log(error);
    });
  }
  
  function refreshServices() {
    Social.services().then(function (response) {
      console.log(response);
      $rootScope.session.services = response.data || {};
      console.log('new session',$rootScope.session);
    }, function (error) {
      console.log(error);
    });
  }

   function refreshService(service) {
    Social.service(service).then(function (response) {
      console.log(response);
      services[service.name] = response.data || {};
      console.log('new session',$rootScope.session);
    }, function (error) {
      console.log(error);
    });
  }

  function refreshOAuth(session) {
    Social.session(session).then(function (response) {
      console.log(response);
      services[session.service][session.id] = response.data || {};
      console.log('new session',$rootScope.session);
    }, function (error) {
      console.log(error);
    });
  }
  
  function refreshAll() {
    refreshUser();
    refreshServices();
  }
  
  return {
    user: user,
    services: services,
    refreshUser: refreshUser,
    refreshServices: refreshServices,
    refreshService: refreshService,
    refreshOAuth: refreshOAuth,
    refreshAll: refreshAll
  };
});