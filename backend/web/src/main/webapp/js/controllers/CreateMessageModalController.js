function CreateMessageModalController($scope, $modalInstance, $timeout, AuthService, UtilsService, MessagesService, item) {

  $scope.authService = AuthService;
  $scope.item = item;
  $scope.message = {
  };

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
  };

  $scope.saveMessage = function () {
    if (UtilsService.isNotBlank($scope.message.text)) {
      $scope.message.itemId = $scope.selectedItem.id;
      MessagesService.crud.create($scope.message, function () {
        $modalInstance.dismiss('cancel');
      });
    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

}