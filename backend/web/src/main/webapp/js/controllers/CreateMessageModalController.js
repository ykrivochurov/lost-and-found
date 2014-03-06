function CreateMessageModalController($scope, $modalInstance, $timeout, AuthService, UtilsService, MessagesService, item) {

  $scope.authService = AuthService;
  $scope.utilsService = UtilsService;
  $scope.item = item;
  $scope.messages = [
    {
      sender: '53086da6da062612e3705c75',
      receiver: 'test',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed convallis blandit augue quis elementum. Maecenas a lobortis orci. In iaculis ante eu libero rhoncus vulputate. Donec non leo leo. Vivamus sem turpis, molestie at magna quis, iaculis pulvinar felis. Aliquam iaculis tempor lacus, quis tincidunt tortor blandit eget. Curabitur iaculis consequat nisl, quis eleifend metus lacinia id. Nunc in neque a nunc fermentum aliquet. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec sed arcu in nunc commodo dignissim.'
    },
    {
      sender: '53086da7da062612e3705c76',
      receiver: 'test',
      text: 'test text 3'
    },
    {
      sender: '53086da6da062612e3705c75',
      receiver: 'test',
      text: 'test text 2'
    },
    {
      sender: '53086da7da062612e3705c76',
      receiver: 'test',
      text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed convallis blandit augue quis elementum. Maecenas a lobortis orci. In iaculis ante eu libero rhoncus vulputate. Donec non leo leo. Vivamus sem turpis, molestie at magna quis, iaculis pulvinar felis. Aliquam iaculis tempor lacus, quis tincidunt tortor blandit eget. Curabitur iaculis consequat nisl, quis eleifend metus lacinia id. Nunc in neque a nunc fermentum aliquet. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec sed arcu in nunc commodo dignissim.'
    },
  ]
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

  $scope.isMessageOwner = function (message) {
    return $scope.authService.currentUserHolder.id == message.sender;
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

  $timeout(function () {
    $('.messages-panel').nanoScroller({ flash: true });
    $(".pane").css("display", "block");
    $(".slider").css("display", "block");
  });

}