angular.module('laf').
    factory('CompanyService', function ($resource) {
        return {
            crud: $resource('api/companies/:companyId/:statusUserCtrl/:newStatus',
                {companyId: '@id', statusUserCtrl: '@statusUserCtrl', newStatus: '@newStatus'},
                {
                    create: {method: 'PUT'},
                    update: {method: 'POST'},
                    updateStatus: {method: 'POST', params: {statusUserCtrl: 'status'}},
                    getOne: {method: 'GET'},
                    getAll: {method: 'GET', isArray: true},
                    delete: {method: 'DELETE'}
                })
        };
    });
