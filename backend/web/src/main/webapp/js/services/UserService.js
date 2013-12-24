angular.module('board').
    factory('UserService', function ($resource) {
        return {
            crud: $resource('api/users/:userId:compOrComm/:compOrCommId',
                {userId: '@userId', compOrCommId: '@compOrCommId'},
                {
                    byCompany:   {method: 'GET', params: {compOrComm: 'company'}, isArray: true},
                    byCommittee: {method: 'GET', params: {compOrComm: 'committee'}, isArray: true},
                    current: {method: 'GET', params: {compOrComm: 'current'}}
                })
        };
    });