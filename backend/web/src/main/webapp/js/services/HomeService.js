angular.module('board').
    factory('HomeService', function ($resource) {
        return {
            crud: $resource('api/home',
                {
                    get: {method: 'GET'}
                })
        };
    });
