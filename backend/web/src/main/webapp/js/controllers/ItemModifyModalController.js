function ItemModifyModalController($scope, $modalInstance, $timeout, UtilsService, AuthService, ItemsService, laf, item, categories, mapServiceInstance) {

  $scope.authService = AuthService;
  $scope.utilsService = UtilsService;
  $scope.categories = categories;
  $scope.itemType = item.itemType;
  if (UtilsService.isNotEmpty(laf)) {
    $scope.laf = laf;
  } else {
    $scope.laf = {
      id: item.id,
//      what: item.what,
      where: item.where,
      when: item.when,
      creationDate: item.creationDate,
      tags: item.tags.slice(0),
      author: item.author,
      photoId: item.photoId,
      thumbnailId: item.thumbnailId,
      location: item.location,
      showPrivateInfo: item.showPrivateInfo,
      money: item.money,
      mainCategory: item.mainCategory,
      itemType: item.itemType,
      cityId: item.cityId,
      number: item.number,
      closed: item.closed,
      messages: item.messages,
      user: item.user,
      item: item
    };
  }

  $scope.mapServiceInstance = mapServiceInstance;
  $scope.mapServiceInstance.getLocationObject();
  $scope.mapServiceInstance.deactivateMiddleStatePanel();

  $scope.showMap = function () {
    $scope.mapServiceInstance.activateMiddleStatePanel($scope.laf, false);
    $modalInstance.dismiss('cancel');
  };

  ItemsService.crud.getByNumber({numberOrId: item.number}, function (currentItem) {
    $scope.laf.user = currentItem.user;
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

  $scope.saveItem = function (isValid) {
    if (!isValid || UtilsService.isEmptyArray($scope.laf.tags)) {
      alert('Не все поля в форме заполнены ' + isValid);
    }
    delete $scope.laf.item;
    ItemsService.crud.update($scope.laf, function (updatedItem) {
      console.log('Item updated: ' + JSON.stringify(updatedItem));

      item.what = updatedItem.what;
      item.where = updatedItem.where;
      item.when = updatedItem.when;
      item.tags = updatedItem.tags.slice(0);
      item.photoId = updatedItem.photoId;
      item.thumbnailId = updatedItem.thumbnailId;
      item.location = updatedItem.location;
      item.showPrivateInfo = updatedItem.showPrivateInfo;
      item.money = updatedItem.money;
      item.mainCategory = updatedItem.mainCategory;
      item.closed = updatedItem.closed;
      item.user = updatedItem.user;

      $scope.mapServiceInstance.updateMarkerBalloon(item);

      $modalInstance.close(item);
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
    $modalInstance.close();
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.uploadPhoto = function () {
    var photoInput = angular.element('input[name="photos"]');
    var spinnerTarget = angular.element('.ci-popup .picture');
    var spinner = new Spinner(SPINER_OPTS).spin(spinnerTarget[0]);

    var formData = new FormData();
    formData.append('fileData', photoInput[0].files[0]);

    ItemsService.upload(formData, function (data, status) {
      angular.element('#photo-preview-m').attr('src', 'api/items/photo/' + data + '?w=100&h=100');
      $scope.laf.photoId = data;
      spinner.stop();
    }, function (data, status) {
      spinner.stop();
    });
  };

  $timeout(function () {
    if (UtilsService.isNotEmpty($scope.laf.photoId)) {
      angular.element('#photo-preview-m').attr('src', 'api/items/photo/' + $scope.laf.photoId + '?w=100&h=100');
    }

    $(".phone-mask").mask("+7 (999) 999-9999");
  });
}