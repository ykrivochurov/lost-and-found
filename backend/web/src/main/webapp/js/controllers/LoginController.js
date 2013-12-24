function LoginController($scope, $http, $location, UserService) {
    $scope.user = {};
    $scope.isSessionExpired = $location.search()['unauthorised'] !== undefined;
    $scope.login = function (user) {
        $http({
            method: 'POST',
            url: "api/login?username=" + user.username + "&password=" + user.password
        }).success(function (data) {
                $scope.loggedIn(); // @see RootController.js
                $location.path("/home").search({});
            }).error(function (data, status) {
                $("#status401").show();
            });
    };
    angular.element('#userName').focus()
}