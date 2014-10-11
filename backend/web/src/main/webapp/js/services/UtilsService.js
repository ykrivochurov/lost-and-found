angular.module('laf').
  factory('UtilsService', function ($location, $route) {
    return {
      getCurrentPath: function () {
        if ($location.url().indexOf('yandex') > -1) {
          return '/#/yandex';
        } else {
          return '/#/';
        }
      },
      generateUrl: function (item) {
        if ($location.url().indexOf('yandex') > -1) {
          return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '') + '/#/yandex?number=' + item.number;
        }
        return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '') + '/#/?number=' + item.number;
      },
      thumbUrl: function (item) {
        return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '') + '/api/items/photo/' + item.thumbnailId;
      },
      isEmpty: function (variable) {
        return variable == null || typeof(variable) == 'undefined';
      },

      isNotEmpty: function (variable) {
        return !this.isEmpty(variable);
      },

      isNotBlank: function (variable) {
        return this.isNotEmpty(variable) && typeof (variable) == "string" && variable.match(/\S/);
      },

      isBlank: function (variable) {
        return  this.isEmpty(variable) || typeof (variable) == "string" && (variable == '' || !variable.match(/\S/));
      },

      isEmptyArray: function (array) {
        return this.isEmpty(array) || !array.length || array.length == 0;
      },

      isNotEmptyArray: function (array) {
        return this.isNotEmpty(array) && array.length && array.length > 0;
      },

      isFunction: function (variable) {
        return this.isNotEmpty(variable) && typeof(variable) == 'function';
      }
    };
  });
