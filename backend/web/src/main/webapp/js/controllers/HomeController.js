function HomeController($q, $scope, $modal, $timeout, $animate, $sce, GeoLocationService, UtilsService, ItemsService, CategoriesService, MapService, AuthService, CityService, $location, ShareService, MessagesService) {
  $scope.shareService = ShareService;
  $scope.authService = AuthService;
  $scope.mapService = MapService;
  $scope.messagesService = MessagesService;
  $scope.lafBusy = false;
  $scope.cities = CityService.crud.get(function (cities) {
    $scope.currentCity = cities[0];
    $scope.mapService.init($scope);
    $scope.mapService.initMap('map-panel');
  });
  $scope.map = 'Новосибирск';
  $scope.currentCountry = 'Россия';
  $scope.currentCity = $scope.cities[0];
  $scope.dateFormat = 'dd/MM/yyyy';
  $scope.isCollapsed = false;
  $scope.categoriesListType = 'LOST';
  $scope.whatDict = ['ключи', 'телефон', 'кошелек', 'сумку', 'варежку'];
  $scope.tagsIcons = TAGS_ICONS;
  $scope.pinIcons = PIN_ICONS;
  $scope.searchQuery = null;
  $scope.selectedCategory = null;
  $scope.selectedTag = null;
  $scope.selectedItem = null;

  $scope.showSearchResults = true;

  $scope.showSelectedCategory = false;
  $scope.showCategoriesList = true;
  $scope.myItemsMode = false;

  $scope.itemsList = {
    page: null,
    lostAndFoundItems: []
  };

  $scope.getCategoryByName = function (name) {
    for (var i = 0; i < $scope.categories.length; i++) {
      var category = $scope.categories[i];
      if (category.name == name) {
        return category;
      }
    }
  };

  $scope.renewLaf = function () {
    $scope.laf = {when: '', where: '', what: '', creationDate: new Date().getTime(), tags: []};
  };

  $scope.renewLaf();

  $scope.refreshCategories = function () {
    var deferred = $q.defer();
    $scope.categoriesCounts = CategoriesService.crud.counts({itemType: $scope.categoriesListType}, function (counts) {
      if (UtilsService.isEmptyArray($scope.categories)) {
        $scope.categories = CategoriesService.crud.all();
      }
      deferred.resolve();
    });
    return deferred.promise;
  };

  $scope.refreshCategories();

  $scope.categoriesRedraw = function () {
    $scope.categories = $scope.categories;
  };

  $scope.loadItemByNumber = function (number) {
    $scope.showBusy('Загрузка объявления...');
    try {
      ItemsService.crud.getByNumber({number: number}, function (item) {
        if (UtilsService.isEmpty(item.id)) {
          //todo неудалось найти указанное объявление
          $scope.hideBusy();
          return;
        }
        var deferred = $q.defer();
        deferred.promise.then(function () {
          $scope.isCollapsed = true;
          $scope.selectCategoryAndTag($scope.getCategoryByName(item.mainCategory), item.tags[0], false).then(function () {
            // get item from db if exists
            for (var i = 0; i < $scope.itemsList.lostAndFoundItems.length; i++) {
              var dbItem = $scope.itemsList.lostAndFoundItems[i];
              if (dbItem.id == item.id) {
                item = dbItem;
                break;
              }
            }
            $scope.openItem(item);
            $scope.hideBusy();
          });
        });
        $scope.setCategoriesListType(item.itemType).then(function () {
          deferred.resolve();
        });
      });
    } catch (e) {
      $scope.hideBusy();
    }
  }

  $scope.itemAdded = function (item) {
    var deferred = $q.defer();
    $scope.authService.refresh();
    deferred.promise.then(function () {
      $scope.isCollapsed = true;
      $scope.selectCategoryAndTag($scope.getCategoryByName(item.mainCategory), item.tags[0], false).then(function () {
        // get item from db if exists
        for (var i = 0; i < $scope.itemsList.lostAndFoundItems.length; i++) {
          var dbItem = $scope.itemsList.lostAndFoundItems[i];
          if (dbItem.id == item.id) {
            item = dbItem;
            break;
          }
        }
        $scope.openItem(item);
      });
    });
    if ($scope.categoriesListType == item.itemType) {
      var firstTag = item.tags[0];
      var countForTag = $scope.categoriesCounts[firstTag];
      if (UtilsService.isNotEmpty(countForTag)) {
        countForTag++;
        $scope.categoriesCounts[firstTag] = countForTag;
        console.log('+1');
        $scope.categoriesRedraw();
      } else {
        $scope.categoriesCounts[firstTag] = 1;
        $scope.categoriesRedraw();
      }
      MapService.createMarker(item);
      deferred.resolve();
    } else {
      $scope.setCategoriesListType(item.itemType).then(function () {
        deferred.resolve();
      });
    }
  };

  $scope.getCountByTag = function (tag) {
    var count = $scope.categoriesCounts[tag];
    if (UtilsService.isEmpty(count)) {
      return 0;
    }
    return count;
  };

  $scope.getIconByTag = function (tagName) {
    return $scope.tagsIcons[tagName];
  };

  $scope.setCategoriesListType = function (value) {
    var deferred = $q.defer();
    $scope.myItemsMode = false;
    $scope.categoriesListType = value;
    angular.element('.lost-b').toggleClass('active', angular.equals(value, 'LOST'));
    angular.element('.found-b').toggleClass('active', angular.equals(value, 'FOUND'));
    $scope.refreshCategories().then(function () {
      MapService.hideBalloonForItem($scope.selectedItem);
      MapService.rebuildMarkers().then(function () {
        $scope.clearCategorySelection();
        MapService.hideAllBalloons();
        deferred.resolve();
      });
    });
    return deferred.promise;
  };

  $scope.selectCategoryAndTag = function (category, tag, my) {
    var deferred = $q.defer();
    $scope.itemsList = {
      page: null,
      lostAndFoundItems: []
    };

    var callback = function (items) {
      $scope.itemsList.page = items;
      $scope.itemsList.lostAndFoundItems = items.content; //todo need to add new items not replace
      $scope.showSelectedCategory = true;
      $scope.showCategoriesList = false;
      $scope.selectedCategory = category;
      $scope.selectedTag = tag;
      if (UtilsService.isNotEmptyArray($scope.itemsList.lostAndFoundItems)) {
        $scope.mapService.showMarkersForCategory($scope.selectedCategory);
      }
      deferred.resolve();
      $timeout(function () {
        // recalculate scroll heights
        $scope.calcScrollHeights();
        $('.items-list-scroll').nanoScroller();
      });
    };
    if (my) {
      ItemsService.crud.getMy({pageNumber: 0}, function (items) {
        callback(items);
        $scope.mapService.drawMarkersForMyItems($scope.itemsList.lostAndFoundItems);
      });
    } else {
      ItemsService.crud.getByCatAndTag({itemType: $scope.categoriesListType, category: category.name, tag: tag,
        cityId: $scope.currentCity.id, pageNumber: 0}, callback);
    }
    return deferred.promise;
  };

  $scope.goToSelectedCategory = function () {
    MapService.hideAllBalloons();
    $scope.selectedItem = null;
    $scope.showSelectedCategory = true;
    $scope.selectCategoryAndTag(
      $scope.selectedCategory,
      $scope.selectedTag,
      $scope.myItemsMode
    );
  };

  $scope.clearCategorySelection = function () {
    if ($scope.myItemsMode) {
      //total reload needed after my mode
      $scope.setCategoriesListType($scope.categoriesListType);
      return;
    }
    $scope.showSelectedCategory = false;
    $scope.showCategoriesList = true;
    $scope.selectedCategory = null;
    $scope.selectedTag = null;
    MapService.hideAllBalloons();
    $scope.selectedItem = null;
    $scope.mapService.showMarkersForCategory();
  };

  $scope.getItemLink = function (item) {
    console.log('item number: ' + item.number);
  };

  $scope.openItem = function (item) {
    console.log('Item: ' + JSON.stringify(item));
    $scope.mapService.hideBalloonForItem($scope.selectedItem);
    $scope.selectedItem = item;
    $scope.showSelectedCategory = false;
    $scope.mapService.showBalloonForItem(item);
  };

  $scope.hasNextItem = function (item, prev) {
    var indexOf = $scope.itemsList.lostAndFoundItems.indexOf(item);
    if (indexOf == 0 && prev) {
      return false;
    }
    if (indexOf + 1 == $scope.itemsList.lostAndFoundItems.length && !prev) {
      return false;
//      return UtilsService.isNotEmpty($scope.itemsList.page) && $scope.itemsList.page.lastPage;
    }
    return true;
  };

  $scope.openNextItem = function (selectedItem, prev) {
    var indexOf = $scope.itemsList.lostAndFoundItems.indexOf(selectedItem);
    if (prev) {
      $scope.openItem($scope.itemsList.lostAndFoundItems[indexOf - 1]);
    } else {
      $scope.openItem($scope.itemsList.lostAndFoundItems[indexOf + 1]);
    }
  };

  $scope.showMyItems = function () {
    var selectedCategory = {name: 'Мои объявления'};
    $scope.isCollapsed = true;
    if (UtilsService.isNotEmpty($scope.authService.currentUserHolder)
      && UtilsService.isNotEmpty($scope.authService.currentUserHolder.id)) {
      MapService.hideAllBalloons();
      $scope.selectedItem = null;
      $scope.showSelectedCategory = true;
      $scope.selectCategoryAndTag(selectedCategory, null, true);
      $scope.myItemsMode = true;
    } else {
      var modalInstance = $modal.open({
        templateUrl: 'login-win.html',
        controller: LoginModalController,
        windowClass: 'login-modal',
        scope: $scope
      });

      modalInstance.result.then(function () {
        MapService.hideAllBalloons();
        $scope.selectedItem = null;
        $scope.showSelectedCategory = true;
        $scope.selectCategoryAndTag(selectedCategory, null, true);
        $scope.myItemsMode = true;
      }, function () {
        console.log('Modal dismissed at: ' + new Date());
      });
    }
  };

  $scope.createItem = function (itemType) {
    var modalInstance = $modal.open({
      templateUrl: 'create-item-modal.html',
      controller: ItemCreateModalController,
      scope: $scope,
      resolve: {
        itemType: function () {
          return itemType;
        }
      }
    });

    modalInstance.result.then(function (itemType, laf) {
//      $scope.itemType = itemType;
//      $scope.laf = laf;
    }, function () {
      console.log('Modal dismissed at: ' + new Date());
    });
  };

  $scope.createMessage = function () {
    $modal.open({
      templateUrl: 'create-message-modal.html',
      controller: CreateMessageModalController,
      scope: $scope
    });
  };

  $scope.showFullSIzeImage = function (item) {
    if (UtilsService.isNotEmpty(item.photoId)) {
      $modal.open({
        templateUrl: 'full-size-image.html',
        controller: ImageViewModalController,
        windowClass: 'full-size-image-modal',
        resolve: {
          item: function () {
            return item;
          }
        }
      });
    }
  };

  $scope.showLoginModal = function () {
    if (UtilsService.isNotEmpty($scope.authService.currentUserHolder)) {
      $scope.authService.currentUserHolder = $scope.authService.currentUserHolder;
    } else {
      $modal.open({
        templateUrl: 'login-win.html',
        controller: LoginModalController,
        windowClass: 'login-modal',
        scope: $scope
      });
    }
  };

  $scope.showComplaintModal = function (item) {
    $modal.open({
      templateUrl: 'complaint-modal.html',
      controller: ComplaintModalController,
      windowClass: 'complaint-modal',
      resolve: {
        item: function () {
          return item;
        }
      }
    });
  }

  $scope.showBusy = function (message) {
    if (!$scope.lafBusy) {
      $scope.lafBusyMessage = $sce.trustAsHtml(message);
      $scope.lafBusy = true;
    }
  };

  $scope.hideBusy = function () {
    $scope.lafBusy = false;
    if (!$scope.$$phase) {
      $scope.$apply();
    }
    console.log($scope.lafBusy);
  };

  $scope.collapse = function () {
    $scope.isCollapsed = !$scope.isCollapsed;
  };

  $scope.joinTags = function (tags) {
    if (angular.isArray(tags) && tags.length > 0) {
      return tags.join(', ');
    }
    return null;
  };

  $scope.calcScrollHeights = function () {
    var topButtonsHeight = angular.element('.top-buttons').outerHeight(true);
    var searchBlockHeight = angular.element('.search-block').outerHeight(true);
    var selectedCategoryBlockHeight = angular.element('.selected-category').outerHeight(true);
    var bodyHeight = angular.element('body').outerHeight(true);
    $scope.categoriesBlockHeight = bodyHeight - topButtonsHeight - searchBlockHeight;
    $scope.itemsListHeight = bodyHeight - topButtonsHeight - searchBlockHeight - selectedCategoryBlockHeight;
  };

  $scope.testF = function () {
    console.log("It's alive!");
  };


  $timeout(function () {
    $scope.calcScrollHeights();
    $timeout(function () {
      $('.categories-list-scroll').nanoScroller();
      $(".pane").css("display", "block");
      $(".slider").css("display", "block");
    }, 2000);
    //todo move to directive
    //check if we loads item link
    var searchObj = $location.search();
    if (UtilsService.isNotEmpty(searchObj.number)) {
      $scope.loadItemByNumber(searchObj.number);
    }
    $scope.authService.currentUserHolder = $scope.authService.currentUserHolder;
  });

}

function joinTagsObjects(tags) {
  if (angular.isArray(tags) && tags.length > 0) {
    return tags.join(', ');
  }
  return null;
}

function generateUrl(item) {
  return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '') + '/#/home?number=' + item.number;
}

function thumbUrl(item) {
  return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '') + '/api/items/photo/' + item.thumbnailId;
}