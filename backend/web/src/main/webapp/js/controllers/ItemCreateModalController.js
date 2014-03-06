function ItemCreateModalController($scope, $modalInstance, $timeout, UtilsService, AuthService, ItemsService, UsersService, mapServiceInstance, itemType) {

  $scope.authService = AuthService;
  $scope.itemType = itemType;
  if (UtilsService.isNotEmptyArray($scope.laf.tags)) {
    $scope.laf.what = WHAT_PREFFIX[$scope.itemType] + WHAT_PREDEF[$scope.laf.tags[0]];
  }
  $scope.laf.itemType = itemType;
  $scope.mapServiceInstance = mapServiceInstance;
  $scope.mapServiceInstance.getLocationObject();
  $scope.mapServiceInstance.deactivateMiddleStatePanel();

  $scope.showMap = function () {
    $scope.mapServiceInstance.activateMiddleStatePanel($scope.laf, true);
    $modalInstance.dismiss('cancel');
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

  $scope.saveItem = function () {
    $scope.laf.cityId = $scope.currentCity.id;
    $scope.laf.location = $scope.mapServiceInstance.getLocationObject();
    UsersService.crud.update($scope.authService.currentUserHolder, function (user) {
      $scope.authService.refresh(user);
      ItemsService.crud.create($scope.laf, function (item) {
        console.log('Item created: ' + JSON.stringify(item));
        $scope.itemAdded(item);
        $scope.renewLaf();
        $modalInstance.dismiss('cancel');
      });
    });
  };

  $scope.addTag = function (tag) {
    if ($scope.laf.tags.indexOf(tag) == -1 && UtilsService.isNotBlank(tag)) {
      $scope.laf.tags.push(tag);
    }
  };

  $scope.removeTag = function (tag) {
    var index = $scope.laf.tags.indexOf(tag);
    if (index > -1) {
      $scope.laf.tags.splice(index, 1);
    }
  };

  $scope.ok = function () {
    $modalInstance.close(itemType);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.uploadPhoto = function () {
    var photoInput = angular.element('input[name="photos"]');
    angular.element('#photo-preview').attr('src', 'img/li_mini.gif');

    var formData = new FormData();
    formData.append('fileData', photoInput[0].files[0]);

    ItemsService.upload(formData, function (data, status) {
      angular.element('#photo-preview').attr('src', 'api/items/photo/' + data + '?w=100&h=100');
      $scope.laf.photoId = data;
    }, function (data, status) {
    });
  };

  $timeout(function () {
    $(".phone-mask").mask("+7 (999) 999-9999");
  });
}