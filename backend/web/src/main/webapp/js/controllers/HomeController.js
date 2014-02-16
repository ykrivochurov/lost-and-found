function HomeController($scope, $modal, $timeout, $animate, $sce, GeoLocationService, UtilsService, ItemsService, CategoriesService, MapService, AuthService, CityService) {
  $scope.authService = AuthService;
  $scope.mapService = MapService;
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
  $scope.searchQuery = null;
  $scope.selectedCategory = null;
  $scope.selectedTag = null;
  $scope.selectedItem = null;

  $scope.showSearchResults = true;

  $scope.showSelectedCategory = false;
  $scope.showCategoriesList = true;

  $scope.itemsList = {
    page: null,
    lostAndFoundItems: []
  };

  $scope.renewLaf = function () {
    $scope.laf = {when: '', where: '', what: '', creationDate: new Date().getTime(), tags: []};
  };

  $scope.renewLaf();

  $scope.refreshCategories = function () {
    $scope.categoriesCounts = CategoriesService.crud.counts({itemType: $scope.categoriesListType}, function (counts) {
      if (UtilsService.isEmptyArray($scope.categories)) {
        $scope.categories = CategoriesService.crud.all();
      }
    });
  };

  $scope.refreshCategories();

  $scope.categoriesRedraw = function () {
    $scope.categories = $scope.categories;
  };

  $scope.itemAdded = function (item) {
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
    $scope.categoriesListType = value;
    angular.element('.lost-b').toggleClass('active', angular.equals(value, 'LOST'));
    angular.element('.found-b').toggleClass('active', angular.equals(value, 'FOUND'));
    $scope.refreshCategories();
    MapService.rebuildMarkers();
    $scope.clearCategorySelection();
  };

  $scope.selectCategoryAndTag = function (category, tag) {
    $scope.itemsList = {
      page: null,
      lostAndFoundItems: []
    };
    ItemsService.crud.getByCatAndTag({itemType: $scope.categoriesListType, category: category.name, tag: tag,
      cityId: $scope.currentCity.id, pageNumber: 0}, function (items) {
      $scope.itemsList.page = items;
      $scope.itemsList.lostAndFoundItems = items.content; //todo need to add new items not replace
      $scope.showSelectedCategory = true;
      $scope.showCategoriesList = false;
      $scope.selectedCategory = category;
      $scope.selectedTag = tag;
      if (UtilsService.isNotEmptyArray($scope.itemsList.lostAndFoundItems)) {
        $scope.mapService.showMarkersForCategory($scope.selectedCategory);
      }
      $timeout(function () {
        // recalculate scroll heights
        $scope.calcScrollHeights();
        $('.items-list-scroll').nanoScroller();
      });
    });
  };

  $scope.goToSelectedCategory = function () {
    MapService.hideBalloonForItem($scope.selectedItem);
    $scope.selectedItem = null;
    $scope.showSelectedCategory = true;
  };

  $scope.clearCategorySelection = function () {
    $scope.showSelectedCategory = false;
    $scope.showCategoriesList = true;
    $scope.selectedCategory = null;
    $scope.selectedTag = null;
    MapService.hideBalloonForItem($scope.selectedItem);
    $scope.selectedItem = null;
    $scope.mapService.showMarkersForCategory();
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

  $scope.createItem = function (itemType) {
    var modalInstance = $modal.open({
      templateUrl: 'create-item-modal.html',
      controller: CreateItemModalCtrl,
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

  // All about map
  $scope.$watch('laf.where', _.debounce(function () {
//    $scope.typed = $scope.typing;
//    $scope.$apply();
  }, 500));

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
    });
    //todo move to directive
  });

}

function joinTagsObjects(tags) {
  if (angular.isArray(tags) && tags.length > 0) {
    return tags.join(', ');
  }
  return null;
}

function CreateItemModalCtrl($scope, $modalInstance, $timeout, UtilsService, AuthService, ItemsService, UsersService, MapService, itemType) {

  $scope.authService = AuthService;
  console.log($scope.currentUser);
  $scope.itemType = itemType;
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

  $timeout(function () {
    $(".phone-mask").mask("+7 (999) 999-9999");
  });
}