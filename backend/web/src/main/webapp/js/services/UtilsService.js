angular.module('laf').
  factory('UtilsService', function () {
    return {
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
