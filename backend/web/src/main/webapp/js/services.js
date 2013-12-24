angular.module('servicesModule', []).
    factory('extractCheckboxes', function () {
        return function (obj) {
            var newObj = [];
            for (var i = 0; i < obj.length; i++) {
                if (obj[i].selected) {
                    delete newObj[newObj.push(angular.copy(obj[i])) - 1].checked;
                }
            }
            return newObj;
        };
    });