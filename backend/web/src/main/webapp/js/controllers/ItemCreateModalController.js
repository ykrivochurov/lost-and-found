function ItemCreateModalController($scope, $modalInstance, $timeout, UtilsService, AuthService, ItemsService, UsersService, MapService, itemType) {

  $scope.authService = AuthService;
  console.log($scope.currentUser);
  $scope.itemType = itemType;
  if (UtilsService.isNotEmptyArray($scope.laf.tags)) {
    $scope.laf.what = WHAT_PREFFIX[$scope.itemType] + WHAT_PREDEF[$scope.laf.tags[0]];
  }
  $scope.laf.itemType = itemType;
  MapService.getLocationObject();

  $scope.setCurrentUser = function (user) {
    $scope.currentUser = user;
    if (!$scope.$$phase) {
      $scope.$apply();
    }
  };

  $scope.saveItem = function () {
    $scope.laf.cityId = $scope.currentCity.id;
    $scope.laf.location = MapService.getLocationObject();
    ItemsService.crud.create($scope.laf, function (item) {
      console.log('Item created: ' + JSON.stringify(item));
      $scope.itemAdded(item);
      $scope.renewLaf();
      UsersService.crud.update($scope.currentUser);
      $modalInstance.dismiss('cancel');
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
    $scope.renewLaf();
    $modalInstance.dismiss('cancel');
  };

  $scope.uploadPhoto = function () {
    var photoInput = angular.element('input[name="photos"]');
    var spinnerTarget = angular.element('.ci-popup .picture');
    var spinner = new Spinner(SPINER_OPTS).spin(spinnerTarget[0]);

    var formData = new FormData();
    formData.append('fileData', photoInput[0].files[0]);

    ItemsService.upload(formData, function (data, status) {
      angular.element('#photo-preview').attr('src', 'api/items/photo/' + data + '?w=100&h=100');
      $scope.laf.photoId = data;
      spinner.stop();
    }, function (data, status) {
      spinner.stop();
    });
  };

  $timeout(function () {
    $(".phone-mask").mask("+7 (999) 999-9999");
  });
}