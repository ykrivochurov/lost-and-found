function LoginModalController($scope, $modalInstance, AuthService, UtilsService) {

  $scope.authService = AuthService;

  $scope.startSpin = function () {
    $scope.spinner = new Spinner(SPINER_OPTS).spin(angular.element('.login-buttons')[0]);
  };

  $scope.setCurrentUser = function (user) {
    if (UtilsService.isNotEmpty($scope.spinner)) {
      $scope.spinner.stop();
    }
    if (!$scope.$$phase) {
      $scope.$apply();
    }
    $modalInstance.close();
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
}