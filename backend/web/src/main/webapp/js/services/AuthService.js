angular.module('laf').
  factory('AuthService', function ($resource, $timeout, UtilsService) {
    var authCallback = null;
    var applyCallback = null;
    var spinnerOnLine = false;
    var resultObject = {
      currentUserHolder: null
    };
    var vk = {
      data: {},
      appID: 4145243,
      appPermissions: 1027,

      init: function () {
        VK.init({apiId: vk.appID});

        $timeout(function () {
          VK.Auth.getLoginStatus(vk.auth, true);
        });
      },

      auth: function (response) {
        if (response.status === 'connected') {
          vk.data.user = response.session.user;
          var uid = response.session.mid;
          var sid = response.session.sid;
          var name = response.session.user.first_name + ' ' + response.session.user.last_name;
          $.get("/api/auth/vk?sid=" + sid + "&uid=" + uid + "&name=" + name, function (user, status) {
            resultObject.currentUserHolder = user;
            if (UtilsService.isNotEmpty(authCallback)) {
              authCallback(user);
            }

            if (UtilsService.isNotEmpty(applyCallback)) {
              applyCallback();
            }

            $timeout(function () {
              angular.element('.user-panel .back-block').remove();
            });
          });
        } else {
          fb.init();
        }
      },

      login: function (callback) {
        authCallback = callback;
        VK.Auth.login(vk.auth, 1027);
      },

      access: function (callback) {
        VK.Auth.getLoginStatus(function (response) {
          if (response.session) { // Пользователь авторизован
            callback(vk.data.user);
          } else { // Пользователь не авторизован
//            vk.login(callback); todo
          }
        });
      },

      logout: function () {
        VK.Auth.logout();
        this.data.user = {};
        alert('До свидания!');
      }
    };
    var fb = {
      data: {},
      appID: 491113621008870,

      init: function () {
        FB.init({appId: fb.appID, xfbml: true, cookie: true, oauth: true});

        FB.Event.subscribe('auth.statusChange', fb.auth);
        $timeout(function () {
          angular.element('.user-panel .back-block').remove();
        }, 2000);
      },

      auth: function (response) {
        if (response.authResponse) {
          var uid = response.authResponse.userID;
          var token = response.authResponse.accessToken;
          $.get("/api/auth/fb?token=" + token + "&uid=" + uid, function (user, status) {
            resultObject.currentUserHolder = user;

            if (UtilsService.isNotEmpty(authCallback)) {
              authCallback(user);
            }
            if (UtilsService.isNotEmpty(applyCallback)) {
              applyCallback();
            }

            $timeout(function () {
              angular.element('.user-panel .back-block').remove();
            });
          })
        }

        angular.element('.user-panel .back-block').remove();

        if (UtilsService.isNotEmpty(applyCallback)) {
          applyCallback();
        }
      },

      login: function (callback) {
        authCallback = callback;
        FB.login(function () {
        }, {scope: 'user_relationships,publish_stream,offline_access,email'});
      },

      access: function (callback) {
        FB.getLoginStatus(function (response) {
          if (response.status === 'connected') {
            fb.data.user = response.session.user;
            var uid = response.authResponse.userID;
            var accessToken = response.authResponse.accessToken;
            callback(fb.data.user);//todo
          } else if (response.status === 'not_authorized') {
            // the user is logged in to Facebook,
          } else {
            // the user isn't logged in to Facebook.
          }
        });
      },

      logout: function () {
        FB.logout(function (response) {
        });
        this.data.user = {};
        alert('До свидания!');
      }
    };
    vk.init();
//    fb.init();

    resultObject.user = $resource('api/auth/user', {}, {get: {method: 'GET'}});
    resultObject.refresh = function (existingUser) {
      if (UtilsService.isNotEmpty(existingUser)) {
        resultObject.currentUserHolder = existingUser;
      } else {
        resultObject.user.get(function (user) {
          resultObject.currentUserHolder = user;
        })
      }
    };
    resultObject.fb = fb;
    resultObject.vk = vk;
    return  resultObject;
  });