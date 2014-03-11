angular.module('laf').
  factory('MessagesService', function ($resource) {
    return {
      crud: $resource('api/messages/:itemId/:nonOwners', {itemId: '@itemId', nonOwners: '@nonOwners'},
        {
          create: {method: 'PUT', params: {itemId: ''}},
          getByItemId: {method: 'GET', params: {itemId: '@itemId'}, isArray: true},
          getNonOwners: {method: 'GET', params: {itemId: '@itemId', nonOwners: '@nonOwners'}, isArray: true}
        })
    };
  });