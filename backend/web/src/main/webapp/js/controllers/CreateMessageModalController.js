function CreateMessageModalController($scope, $modalInstance, $timeout, AuthService, UtilsService, MessagesService) {

  $scope.authService = AuthService;
  $scope.message = {
  };

  $scope.setCurrentUser = function (user) {
    $scope.currentUser = user;
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