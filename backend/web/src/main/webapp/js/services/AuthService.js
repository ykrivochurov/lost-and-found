angular.module('laf').
  factory('AuthService', function ($resource) {
    var vk = {
      data: {},
      appID: 4145243,
      appPermissions: 1027,

      init: function () {
        VK.init({apiId: vk.appID});
        VK.Auth.getLoginStatus(vk.auth, true);
      },

      auth: function (response) {
        if (response.status === 'connected') {
          vk.data.user = response.session.user;
          var uid = response.session.mid;
          var sid = response.session.sid;
          var name = response.session.user.first_name + ' ' + response.session.user.last_name;
          $.get("/auth/vk?sid=" + sid + "&uid=" + uid + "&name=" + name, function (data, status) {
            $('.loggingin').removeClass('loggingvk');
            if (status == 'success') {
              $('#logins .vk').append($('<span>' + data + '</span>'));
            }
          })
        } else {
          $('.loggingin').removeClass('loggingvk');
        }
      },

      login: function (callback) {
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
      },

      auth: function (response) {
        if (response.authResponse) {
          var uid = response.authResponse.userID;
          var token = response.authResponse.accessToken;
          $.get("/auth/fb?token=" + token + "&uid=" + uid, function (data, status) {
            $('.loggingin').removeClass('loggingfb')
            if (status == 'success')
              $('#logins .fb').append($('<span>' + data + '</span>'))
          })
        } else {
          $('.loggingin').removeClass('loggingfb')
        }
      },

      login: function () {
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
    fb.init();
    return {
      user: $resource('auth/user'),
      fb: fb,
      vk: vk
    };
  });