function CreateMessageModalController($scope, $modalInstance, $timeout, $q, AuthService, UtilsService, MessagesService, ItemsService, item) {

  $scope.authService = AuthService;
  $scope.utilsService = UtilsService;
  $scope.item = item;
  $scope.messages = [];
  $scope.message = {};

  $scope.loadChats = function () {
    var deferred = $q.defer();
    if ($scope.item != null) {
      MessagesService.crud.getChatsByItemId({itemId: $scope.item.id}, function (chatsPage) {
        $scope.chats = chatsPage.content;
        if (UtilsService.isEmptyArray($scope.chats)) {
          $scope.chats = [
            {
              id: null,
              item: $scope.item
            }
          ]
        }
        if ($scope.chats.length == 1) {
          $scope.selectChat($scope.chats[0]);
        }
        $timeout(function () {
          var listHeight = angular.element('.wrapper-for-height').outerHeight(true);
          if (listHeight > 350) {
            angular.element('.chats-panel').css('min-height', 350);
          } else {
            angular.element('.chats-panel').css('min-height', listHeight);
          }
          angular.element('.chats-panel').nanoScroller();
        });
        $timeout(function () {
          angular.element('.chats-panel').nanoScroller();
        }, 2000);
        deferred.resolve();
      });
    } else {
      MessagesService.crud.getChats({}, function (chatsPage) {
        $scope.chats = chatsPage.content;
        if (UtilsService.isEmptyArray($scope.chats)) {
          $scope.chats = [
            {
              id: null
            }
          ]
        }
        if ($scope.chats.length == 1) {
          $scope.selectChat($scope.chats[0]);
        }
        $timeout(function () {
          var listHeight = angular.element('.wrapper-for-height').outerHeight(true);
          if (listHeight > 350) {
            angular.element('.chats-panel').css('min-height', 350);
          } else {
            angular.element('.chats-panel').css('min-height', listHeight);
          }
        });
        $timeout(function () {
          angular.element('.chats-panel').nanoScroller();
        }, 2000);
        deferred.resolve();
      });
    }
    return deferred.promise;
  };

  $scope.selectChat = function (chat) {
    $scope.selectedChat = chat;
    if ($scope.selectedChat.id != null) {
      $scope.loadMessagesByChat();
    } else {
      $scope.messages = [];
    }
  };

  $scope.loadMessagesByChat = function () {
    MessagesService.crud.getMessagesByChat({chatId: $scope.selectedChat.id}, function (messages) {
      $scope.messages = messages;
      $scope.authService.refresh();
      $timeout(function () {
        $('.messages-panel').nanoScroller();
      }, 2000);
    });
  };

  $scope.deSelectChat = function () {
    if ($scope.chats && $scope.chats.length > 1) {
      $scope.selectedChat = null;
      $scope.messages = null;
      $scope.loadChats();
    }
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
    return $scope.authService.currentUserHolder != null
      && $scope.authService.currentUserHolder.id == message.sender;
  };

  $scope.saveMessage = function () {
    if (UtilsService.isNotBlank($scope.message.text)) {
      $scope.message.itemId = $scope.selectedChat.item.id;
      $scope.message.chatId = $scope.selectedChat.id;
      MessagesService.crud.create($scope.message, function (message) {
        $scope.message = {};
        $scope.loadChats().then(function () {
          for (var i = 0; i < $scope.chats.length; i++) {
            var chat = $scope.chats[i];
            if (chat.id == message.chatId) {
              $scope.selectChat(chat);
              break;
            }
          }
        });
      });
    }
  };

  $scope.uploadPhoto = function () {
    var photoInput = angular.element('input[name="photos"]');
    angular.element('#photo-preview').attr('src', 'img/li_mini.gif');

    var formData = new FormData();
    formData.append('fileData', photoInput[0].files[0]);

    ItemsService.upload(formData, function (data, status) {
      angular.element('#photo-preview').attr('src', 'api/items/photo/' + data + '?w=100&h=100');
      $scope.message.photoId = data;
    }, function (data, status) {
    });
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.loadChats();
}