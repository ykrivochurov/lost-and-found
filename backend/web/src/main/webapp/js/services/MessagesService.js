angular.module('laf').
  factory('MessagesService', function ($resource) {
    return {
      crud: $resource('api/messages/:itemId', {itemId: '@itemId'},
        {
          create: {method: 'PUT', params: {itemId: ''}},
          getByItemId: {method: 'GET', params: {itemId: '@itemId'}, isArray: true}
        })
    };
  });