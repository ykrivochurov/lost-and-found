angular.module('laf').
  factory('UsersService', function ($resource) {
    return {
      crud: $resource('api/users', {},
        {
          update: {method: 'POST'}
        })
    };
  });