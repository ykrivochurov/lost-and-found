angular.module('laf').
  factory('MessagesService', function ($resource) {
    return {
      crud: $resource('api/messages/:chatId', {chatId: '@chatId'},
        {
          create: {method: 'PUT', params: {chatId: ''}},
          getChats: {method: 'GET', params: {chatId: ''}},
          getChatsByItemId: {method: 'GET', params: {chatId: '', itemId: '@itemId'}},
          getMessagesByChat: {method: 'GET', params: {chatId: '@chatId'}, isArray: true}
        })
    };
  });