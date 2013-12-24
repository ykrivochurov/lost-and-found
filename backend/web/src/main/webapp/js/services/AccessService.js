angular.module('board').
    factory('AccessService', function ($resource) {
        return {
            logout: $resource('api/logout', {},
                {
                    do: {method: 'POST'}
                })
        };
    });