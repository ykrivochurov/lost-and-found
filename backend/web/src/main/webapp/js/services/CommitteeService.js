angular.module('laf').
    factory('CommitteeService', function ($resource) {
        return {
            crud: $resource('api/committees/:committeeId/:userCtrl:statusCtrl/:newStatus',
                {committeeId: '@committeeId', statusCtrl: '@statusUserCtrl', newStatus: '@newStatus'},
                {
                    byCompany: {method: 'GET', params: {companyId: '@companyId'}, isArray: true},
                    updateStatus: {method: 'POST', params: {statusCtrl: 'status'}},
                    create: {method: 'PUT'},
                    update: {method: 'POST'},
                    delete: {method: 'DELETE'},
                    getOne: {method: 'GET'},
                    addUser: {method: 'POST', params: {userCtrl: 'addUser', userId: '@userId'}},
                    deleteUser: {method: 'POST', params: {userCtrl: 'deleteUser', userId: '@userId'}}
                })
        };
    });