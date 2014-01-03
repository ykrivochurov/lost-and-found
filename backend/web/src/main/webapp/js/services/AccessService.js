angular.module('laf').
    factory('AccessService', function ($resource) {
        return {
            logout: $resource('api/logout', {},
                {
                    do: {method: 'POST'}
                })
        };
    });