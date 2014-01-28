function HomeController($scope, $modal, $timeout, $animate, $sce, GeoLocationService, UtilsService, ItemsService, CategoriesService, MapService, AuthService) {
  $scope.authService = AuthService;
  $scope.mapService = MapService;
  $scope.lafBusy = false;
  $scope.cities = CITIES;
  $scope.map = 'Новосибирск';
  $scope.currentCountry = 'Россия';
  $scope.currentCity = $scope.cities[0];
  $scope.dateFormat = 'dd/MM/yyyy';
  $scope.isCollapsed = false;
  $scope.categoriesListType = 'lost';
  $scope.whatDict = ['ключи', 'телефон', 'кошелек', 'сумку', 'варежку'];
  $scope.categories = CategoriesService.crud.all();
  $scope.categoriesCounts = CategoriesService.crud.counts();
  $scope.tagsIcons = TAGS_ICONS;
  $scope.searchQuery = null;
  $scope.selectedCategory = null;
  $scope.selectedTag = null;
  $scope.selectedItem = null;
  $scope.lostAndFoundItems = LOST_AND_FOUND_ITEMS;

  $scope.showSearchResults = true;

  $scope.showSelectedCategory = false;
  $scope.showCategoriesList = true;

  $scope.renewLaf = function () {
    $scope.laf = {when: '', where: '', what: '', creationDate: new Date().getTime(), tags: []};
  };

  $scope.renewLaf();

  $scope.refreshCategories = function () {
    $scope.categories = CategoriesService.crud.all();
    $scope.categoriesCounts = CategoriesService.crud.counts();
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

  $scope.lafListSelect = function (value) {
    $scope.categoriesListType = value;
    angular.element('.lost-b').toggleClass('active', angular.equals(value, 'lost'));
    angular.element('.found-b').toggleClass('active', angular.equals(value, 'found'));
  };

  $scope.selectCategoryAndTag = function (category, tag) {
    $scope.showSelectedCategory = true;
    $scope.showCategoriesList = false;
    $scope.selectedCategory = category;
    $scope.selectedTag = tag;
    $scope.mapService.showMarkersForCategory($scope.selectedCategory);
    $timeout(function () {
      // recalculate scroll heights
      $scope.calcScrollHeights();
      $('.items-list-scroll').nanoScroller();
    });
  };

  $scope.goToSelectedCategory = function () {
    $scope.selectedItem = null;
    $scope.showSelectedCategory = true;
  };

  $scope.clearCategorySelection = function () {
    $scope.showSelectedCategory = false;
    $scope.showCategoriesList = true;
    $scope.selectedCategory = null;
    $scope.selectedTag = null;
    $scope.selectedItem = null;
    $scope.mapService.showMarkersForCategory();
  };

  $scope.openItem = function (item) {
    console.log('Item: ' + JSON.stringify(item));
    $scope.selectedItem = item;
    $scope.showSelectedCategory = false;
    $scope.mapService.showBalloonForItem(item);
  };

  $scope.openNextItem = function (selectedItem, prev) {
    for (var i = 0; i < $scope.lostAndFoundItems.length; i++) {
      var item = $scope.lostAndFoundItems[i];
      if (item.id == selectedItem.id) {
        if (prev && item.hasPrev) {
          $scope.selectedItem = $scope.lostAndFoundItems[i - 1];
          return;
        }
        if (!prev && item.hasNext) {
          $scope.selectedItem = $scope.lostAndFoundItems[i + 1];
          return;
        }
        $scope.goToSelectedCategory();
      }
    }
    $scope.mapService.showBalloonForItem($scope.selectedItem);
  };

  $scope.createItem = function (itemType) {
    var modalInstance = $modal.open({
      templateUrl: 'create-item-modal.html',
      controller: CreateItemModalCtrl,
      scope: $scope,
      resolve: {
        itemType: function () {
          return itemType;
        },
        laf: function () {
          return $scope.laf;
        },
        whatDict: function () {
          return $scope.whatDict;
        },
        categories: function () {
          return $scope.categories;
        },
        categoriesCounts: function () {
          return $scope.categoriesCounts;
        },
        lostAndFoundItems: function () {
          return $scope.lostAndFoundItems;
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

  var CreateItemModalCtrl = function ($scope, $modalInstance, AuthService, ItemsService, itemType, laf, whatDict, categories, categoriesCounts, lostAndFoundItems) {

    $scope.authService = AuthService;
    $scope.currentUser = $scope.authService.user.get();
    console.log($scope.currentUser);
    $scope.itemType = itemType;
    $scope.whatDict = whatDict;
    $scope.lostAndFoundItems = lostAndFoundItems;
    $scope.foundOnCreation = [lostAndFoundItems[0], lostAndFoundItems[1], lostAndFoundItems[2], lostAndFoundItems[3]];

    $scope.saveItem = function () {
      ItemsService.crud.create($scope.laf, function (item) {
        console.log('Item created: ' + JSON.stringify(item));
        $scope.refreshCategories();
        $scope.renewLaf();
        $modalInstance.dismiss('cancel');
      });
    };

    $scope.addTag = function (tag) {
      if ($scope.laf.tags.indexOf(tag) == -1 && UtilsService.isNotBlank(tag)) {
        $scope.laf.tags.push(tag);
      }
    };

    $scope.ok = function () {
      $modalInstance.close(itemType);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
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
    $scope.mapService.init($scope);
    $scope.mapService.initMap('map-panel');
    $timeout(function () {
      $('.categories-list-scroll').nanoScroller();
    });
  });

}

function joinTagsObjects(tags) {
  if (angular.isArray(tags) && tags.length > 0) {
    return tags.join(', ');
  }
  return null;
}
