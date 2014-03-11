function CreateMessageModalController($scope, $modalInstance, $timeout, AuthService, UtilsService, MessagesService, item) {

  $scope.authService = AuthService;
  $scope.utilsService = UtilsService;
  $scope.item = item;
  $scope.messages = [];
  $scope.message = {};

  MessagesService.crud.getByItemId({itemId: item.id}, function (messages) {
    $scope.messages = messages;
    $timeout(function () {
      $('.messages-panel').nanoScroller();
    }, 2000);
  });

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

  $scope.isMessageOwner = function (message) {
    return $scope.authService.currentUserHolder != null
      && $scope.authService.currentUserHolder.id == message.sender;
  };

  $scope.saveMessage = function () {
    if (UtilsService.isNotBlank($scope.message.text)) {
      $scope.message.itemId = $scope.selectedItem.id;
      $scope.message.receiver = $scope.item.author;
      MessagesService.crud.create($scope.message, function (message) {
        $scope.messages.unshift(message);
        $scope.message = {};
        $timeout(function () {
          $('.messages-panel').nanoScroller();
        }, 2000);
      });
    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

}