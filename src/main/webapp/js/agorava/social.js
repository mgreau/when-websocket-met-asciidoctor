app.factory('Social', function ($http, Config) {
  return {
    all: function () { return $http.get(Config.apiPath + '/providers'); },
    currentUser: function () { return $http.get(Config.apiPath + '/users/current'); },
    startDance: function (serviceName) { return $http.get(Config.apiPath + '/providers/' + serviceName + '/startDance'); },
    services: function () { return $http.get(Config.apiPath + '/services'); },
    session: function (session) { return $http.get(Config.apiPath + '/session/' + session.id); },
    serviceSessions: function (service) { return $http.get(Config.apiPath + '/services/' + service.name + "/sessions"); }
  };
});

