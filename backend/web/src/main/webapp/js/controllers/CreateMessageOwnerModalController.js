function CreateMessageOwnerModalController($scope, $modalInstance, $timeout, AuthService, UtilsService, MessagesService, item) {

  $scope.authService = AuthService;
  $scope.utilsService = UtilsService;
  $scope.item = item;
  $scope.messages = [];
  $scope.message = {};
  $scope.users = [];
  $scope.user = {};

  // get users
  MessagesService.crud.getNonOwners({itemId: item.id, nonOwners: 'nonOwners'}, function (users) {
    $scope.users = users;
    if (UtilsService.isNotEmptyArray($scope.users)) {
      $scope.selectUser($scope.users[0]);
    }
  });

  $scope.selectUser = function (user) {
    $scope.user = user;
    // get messages for the user
    MessagesService.crud.getByItemId({itemId: $scope.item.id, nonOwner: $scope.user.id}, function (messages) {
      $scope.messages = messages;
    });
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

  $scope.isItemOwner = function () {
    return $scope.authService.currentUserHolder != null
      && $scope.authService.currentUserHolder.id == item.author;
  };

  $scope.isMessageOwner = function (message) {
    return $scope.authService.currentUserHolder != null
      && $scope.authService.currentUserHolder.id == message.sender;
  };

  $scope.saveMessage = function () {
    if (UtilsService.isNotBlank($scope.message.text)) {
      $scope.message.itemId = $scope.item.id;
      $scope.message.nonOwner = $scope.user.id;
      MessagesService.crud.create($scope.message, function (message) {
        $scope.messages.unshift(message);
        $scope.message = {};
      });
    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  $timeout(function () {
    $('.messages-panel').nanoScroller();
//    $(".pane").css("display", "block");
//    $(".slider").css("display", "block");
  }, 2000);

}