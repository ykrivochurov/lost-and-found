function HomeController($q, $scope, $modal, $timeout, $sce, Analytics, UrlBuildingService, UtilsService, ItemsService, CategoriesService, MapService, AuthService, CityService, $location, ShareService, MessagesService, UsersService) {
  $scope.shareService = ShareService;
  $scope.urlBuildingService = UrlBuildingService;
  $scope.utilsService = UtilsService;

  $scope.authService = AuthService;
  $scope.authService.applyCallback = function () {
    if (!$scope.$$phase) {
      $scope.$apply();
    }
  };
  $scope.authService.refresh(null);

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
  $scope.rightPanelVisible = false;
  $scope.mapFullScreen = false;
  $scope.categoriesListType = 'LOST';
  $scope.tagsIcons = TAGS_ICONS;
  $scope.pinIcons = PIN_ICONS;
  $scope.searchQuery = null;
  $scope.selectedCategory = null;
  $scope.selectedTag = null;
  $scope.selectedItem = null;

  $scope.favoriteCategory = 'Избранное';
  $scope.myItemsCategory = 'Мои объявления';
  $scope.searchItemsCategory = 'Результаты поиска';

  $scope.showSearchResults = true;

  $scope.showSelectedCategory = false;
  $scope.showCategoriesList = true;
  $scope.myItemsMode = false;
  $scope.searchItemsMode = false;
  $scope.favoriteItemsMode = false;

  UsersService.crud.loadAll(function (users) {
    $scope.testUsers = users;
  });

  $scope.rightPanelStates = {
    my: {},
    search: {},
    categoryAndTag: {},
    itemDetails: {
      category: '',
      tag: '',

    }
  };

  $scope.itemsList = {
    page: null,
    lostAndFoundItems: []
  };

  $scope.loadCategoriesAndCounts = function () {
    var deferred = $q.defer();
    $scope.categoriesCounts = CategoriesService.crud.counts({itemType: $scope.categoriesListType}, function (counts) {
      if (UtilsService.isEmptyArray($scope.categories)) {
        $scope.categories = CategoriesService.crud.all();
      }
      deferred.resolve();
    });
    return deferred.promise;
  };

  $scope.loadCategoriesAndCounts();

  $scope.setCategoriesListType = function (value) {
    var deferred = $q.defer();
    $scope.myItemsMode = false;
    $scope.favoriteItemsMode = false;
    $scope.searchReset();
    $scope.categoriesListType = value;
    angular.element('.lost-b').toggleClass('active', angular.equals(value, 'LOST'));
    angular.element('.found-b').toggleClass('active', angular.equals(value, 'FOUND'));
    $scope.loadCategoriesAndCounts().then(function () {
      $scope.mapService.hideBalloonForItem($scope.selectedItem);
      $scope.mapService.rebuildMarkers().then(function () {
        $scope.clearCategorySelection();
        $scope.mapService.hideAllBalloons();
        deferred.resolve();
      });
    });
    return deferred.promise;
  };

  $scope.selectCategoryAndTag = function (category, tag, my, search, favorite) {
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
    if (search) {
      ItemsService.crud.search({query: $scope.searchQuery, itemType: $scope.categoriesListType}, function (items) {
        if ($scope.searchSpinner) {
          $scope.searchSpinner.addClass('hidden');
        }
        callback({content: items});
        $scope.mapService.drawMarkersDirectly($scope.itemsList.lostAndFoundItems);
      });
    } else if (favorite) {
      ItemsService.crud.favoriteItems(function (items) {
        callback({content: items});
        $scope.mapService.drawMarkersDirectly($scope.itemsList.lostAndFoundItems);
      });
    } else if (my) {
      ItemsService.crud.getMy({pageNumber: 0}, function (items) {
        callback(items);
        $scope.mapService.drawMarkersDirectly($scope.itemsList.lostAndFoundItems);
      });
    } else {
      ItemsService.crud.getByCatAndTag({itemType: $scope.categoriesListType, category: category.name, tag: tag,
        cityId: $scope.currentCity.id, pageNumber: 0}, callback);
    }
    return deferred.promise;
  };

  $scope.goBackToSelectedCategory = function () {
    $scope.mapService.hideAllBalloons();
    $scope.clearSelectedItem();
    $scope.showSelectedCategory = true;
    $scope.selectCategoryAndTag(
      $scope.selectedCategory,
      $scope.selectedTag,
      $scope.myItemsMode,
      $scope.searchItemsMode,
      $scope.favoriteItemsMode
    );
  };

  $scope.goBackToCategories = function () {
    $scope.setCategoriesListType($scope.categoriesListType);
  };

  $scope.clearCategorySelection = function () {
    $scope.searchQuery = null;
    $scope.showSelectedCategory = false;
    $scope.showCategoriesList = true;
    $scope.selectedCategory = null;
    $scope.selectedTag = null;
    $scope.mapService.hideAllBalloons();
    $scope.clearSelectedItem();
    $scope.mapService.showMarkersForCategory();
  };

  $scope.getNumberFromUrl = function () {
    var searchObj = $location.search();
    if (UtilsService.isNotEmpty(searchObj.number)) {
      return searchObj.number;
    } else {
      return null;
    }
  };

  $scope.loadAndOpenItemByNumber = function () {
    Analytics.trackPage($location.url());
    var number = $scope.getNumberFromUrl();
    if (number == null) {
      return;
    }
    if ($scope.selectedItem != null && $scope.selectedItem.number == number) {
      return;
    }
    $scope.showBusy('Загрузка объявления...');
    try {
      ItemsService.crud.getByNumber({numberOrId: number}, function (item) {
        if (UtilsService.isEmpty(item.id)) {
          //todo неудалось найти указанное объявление
          $scope.hideBusy();
          return;
        }
        var deferred = $q.defer();
        deferred.promise.then(function () {
          $scope.rightPanelVisible = true;
          $scope.selectCategoryAndTag($scope.getCategoryByName(item.mainCategory), item.tags[0], false, false, false).then(function () {
            $scope.openItem(item);
          });
        });
        $scope.setCategoriesListType(item.itemType).then(function () {
          deferred.resolve();
        });
      });
    } catch (e) {
      $scope.hideBusy();
    }
  };

  $scope.openItem = function (item) {
    console.log('Item: ' + JSON.stringify(item));
    $scope.showBusy('Загрузка объявления...');
    ItemsService.crud.getByNumber({numberOrId: item.number}, function (item) {
      if (UtilsService.isEmpty(item.id)) {
        $scope.hideBusy();
        alert('Объявление с номером ' + item.number + ' не существует!');
        return;
      }
      $scope.hideBusy();
      $scope.mapService.hideBalloonForItem($scope.selectedItem);
      $scope.showSelectedCategory = false;
      $scope.mapService.showBalloonForItem(item);
      if (item != null) {
        $location.search({number: item.number});
      }
      $scope.selectedItem = item;
      $timeout(function () {
        // recalculate scroll heights
        $scope.calcScrollHeights();
        $('.details-block-scroll').nanoScroller();
      });
    }, function (e) {
      console.log(e);
      alert('Объявление с номером ' + item.number + ' не существует!');
    });
  };

  $scope.markItemAsClosed = function (item) {
    ItemsService.crud.close({numberOrId: item.id}, function (item) {
      $scope.goBackToSelectedCategory();
      $scope.authService.refresh();
      $scope.mapService.removeMarker(item);
    });
  };

  $scope.hasNextItem = function (item, prev) {
    if (UtilsService.isEmpty(item)) {
      return false;
    }
    var indexOf;
    for (var i = 0; i < $scope.itemsList.lostAndFoundItems.length; i++) {
      var itemFromCollection = $scope.itemsList.lostAndFoundItems[i];
      if (itemFromCollection.id == item.id) {
        indexOf = i;
      }
    }
    if (indexOf == 0 && prev) {
      return false;
    }
    return !(indexOf + 1 == $scope.itemsList.lostAndFoundItems.length && !prev);
  };

  $scope.openNextItem = function (selectedItem, prev) {
    var indexOf;
    for (var i = 0; i < $scope.itemsList.lostAndFoundItems.length; i++) {
      var itemFromCollection = $scope.itemsList.lostAndFoundItems[i];
      if (itemFromCollection.id == selectedItem.id) {
        indexOf = i;
      }
    }
    if (prev) {
      $scope.openItem($scope.itemsList.lostAndFoundItems[indexOf - 1]);
    } else {
      $scope.openItem($scope.itemsList.lostAndFoundItems[indexOf + 1]);
    }
  };

  $scope.showMyItems = function () {
    var selectedCategory = {name: $scope.myItemsCategory};
    $scope.rightPanelVisible = true;
    if (UtilsService.isNotEmpty($scope.authService.currentUserHolder)
      && UtilsService.isNotEmpty($scope.authService.currentUserHolder.id)) {
      $scope.mapService.hideAllBalloons();
      $scope.clearSelectedItem();
      $scope.showSelectedCategory = true;
      $scope.selectCategoryAndTag(selectedCategory, null, true, false, false);
      $scope.myItemsMode = true;
    }
  };

  $scope.clearSelectedItem = function () {
    $scope.selectedItem = null;
  };

  $scope.showItemSettings = function (e) {
    var selector = angular.element(e.target).parent();
    var buttonLeft = selector.find('.settings-button').position().left;
    var buttonWidth = selector.find('.settings-button').outerWidth(true);
    var panelWidth = selector.outerWidth(true);
    var widthFix = 1;
    selector.find('.settings-block .arrow').css('right', (panelWidth - buttonLeft - buttonWidth - widthFix) + 'px');
    angular.element('.settings-block').hide();
    selector.find('.settings-block').show();
  };

  $scope.setNewPlace = function (cancel) {
    if ($scope.isItemCreation) {
      if (cancel) {
        $scope.middleStateItem.location = $scope.middleStateItem_loc;
        $scope.middleStateItem.where = $scope.middleStateItem_where;
      }
      $scope.createItemModal($scope.middleStateItem.itemType);
      $scope.mapService.deactivateMiddleStatePanel();
    } else {
      if (cancel) {
        $scope.middleStateItem.location = $scope.middleStateItem_loc;
        $scope.middleStateItem.where = $scope.middleStateItem_where;
      } else {
        $scope.middleStateItem.location = $scope.mapService.getLocationObject();
      }
      $scope.editItemModal($scope.middleStateItem, $scope.middleStateItem.item);
      $scope.mapService.deactivateMiddleStatePanel();
    }
  };

  $scope.search = function () {
    if ($scope.searchSpinner) {
      $scope.searchSpinner.addClass('hidden');
    }
    if ($scope.searchProimse) {
      $timeout.cancel($scope.searchProimse);
    }
    $scope.searchProimse = $timeout(function () {
      if ($scope.searchSpinner) {
        $scope.searchSpinner.removeClass('hidden');
      } else {
        $scope.searchSpinner = angular.element('.search-block img');
        $scope.searchSpinner.removeClass('hidden');
      }
      if (UtilsService.isBlank($scope.searchQuery)) {
        $scope.setCategoriesListType($scope.categoriesListType);
        return;
      }
      $scope.rightPanelVisible = true;
      $scope.mapService.hideAllBalloons();
      $scope.clearSelectedItem();
      $scope.showSelectedCategory = true;
      $scope.selectCategoryAndTag({name: $scope.searchItemsCategory}, null, false, true, false);
      $scope.searchItemsMode = true;
      console.log($scope.searchQuery);
    }, 1000);
  };

  $scope.searchReset = function () {
    $scope.searchQuery = null;
    if ($scope.searchSpinner) {
      $scope.searchSpinner.addClass('hidden');
    }
    $scope.searchItemsMode = false;
  };


  $scope.showFavoriteItems = function () {
    var selectedCategory = {name: $scope.favoriteCategory};
    $scope.rightPanelVisible = true;
    if (UtilsService.isNotEmpty($scope.authService.currentUserHolder)
      && UtilsService.isNotEmpty($scope.authService.currentUserHolder.id)) {
      $scope.mapService.hideAllBalloons();
      $scope.clearSelectedItem();
      $scope.showSelectedCategory = true;
      $scope.selectCategoryAndTag(selectedCategory, null, false, false, true);
      $scope.favoriteItemsMode = true;
    }
  };


  $scope.favoriteOnOff = function (item) {
    ItemsService.crud.favorite({itemId: item.id}, function (favorite) {
      if ($scope.authService.currentUserHolder != null) {
        $scope.authService.currentUserHolder.favorite = favorite;
      }
    });
  };

  $scope.isInFavorite = function (item) {
    if ($scope.authService.currentUserHolder != null
      && $scope.authService.currentUserHolder.id != null
      && item != null
      && $scope.authService.currentUserHolder.favorite != null) {
      return $scope.authService.currentUserHolder.favorite.favorites.indexOf(item.id) > -1;
    }
    return false;
  };

//  Busy indicator
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

// end  Busy indicator


//  Modals part

  $scope.editItemModal = function (laf, item) {
    //hide tooltip
    angular.element('.settings-block').hide();

    var modalInstance = $modal.open({
      templateUrl: 'modify-item-modal',
      controller: ItemModifyModalController,
      resolve: {
        item: function () {
          return item;
        },
        laf: function () {
          return laf;
        },
        categories: function () {
          return $scope.categories;
        },
        mapServiceInstance: function () {
          return $scope.mapService;
        }
      }
    });

    modalInstance.result.then(function (item) {
      $scope.loadCategoriesAndCounts().then(function () {
        $scope.mapService.moveMarker(item);
      });
      if ($scope.selectedCategory != $scope.myItemsCategory) {
      }
    }, function () {
      console.log('Modal dismissed at: ' + new Date());
    });
  };

  $scope.createItemModal = function (itemType) {
    var modalInstance = $modal.open({
      templateUrl: 'create-item-modal',
      controller: ItemCreateModalController,
      scope: $scope,
      resolve: {
        itemType: function () {
          return itemType;
        },
        mapServiceInstance: function () {
          return $scope.mapService;
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

  $scope.createMessageModal = function (item) {
    if (UtilsService.isEmpty($scope.authService.currentUserHolder)) {
      $scope.showLoginModal(function () {
        $scope.createMessageModal(item);
      });
    } else {
      $modal.open({
        templateUrl: 'create-message-modal',
        controller: CreateMessageModalController,
        windowClass: 'create-message-modal',
        scope: $scope,
        resolve: {
          item: function () {
            return item;
          }
        }
      });
    }
  };

  $scope.showFullSIzeImageModal = function (item) {
    if (UtilsService.isNotEmpty(item.photoId)) {
      $modal.open({
        templateUrl: 'full-size-image',
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

  $scope.showLoginModal = function (callback) {
    if (UtilsService.isNotEmpty($scope.authService.currentUserHolder)) {
      $scope.authService.currentUserHolder = $scope.authService.currentUserHolder;
    } else {
      var modalInstance = $modal.open({
        templateUrl: 'login-win',
        controller: LoginModalController,
        windowClass: 'login-modal',
        scope: $scope,
        resolve: {
          callback: function () {
            return callback;
          }
        }
      });
      modalInstance.result.then(function () {
      }, function () {
        console.log('Modal dismissed at: ' + new Date());
      });
    }
  };

  $scope.showComplaintModal = function (item) {
    $modal.open({
      templateUrl: 'complaint-modal',
      controller: ComplaintModalController,
      windowClass: 'complaint-modal',
      resolve: {
        item: function () {
          return item;
        }
      }
    });
  }

//  Modals part end

  //utils methods
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

  $scope.categoriesRedraw = function () {
    $scope.categories = $scope.categories;
  };

  $scope.getCountByTag = function (tag) {
    var count = $scope.categoriesCounts[tag];
    if (UtilsService.isEmpty(count)) {
      return 0;
    }
    return count;
  };

  $scope.collapse = function () {
    $scope.rightPanelVisible = !$scope.rightPanelVisible;
  };

  $scope.joinTags = function (tags) {
    if (angular.isArray(tags) && tags.length > 0) {
      return tags.join(', ');
    }
    return null;
  };

  //utils methods end


  $scope.calcScrollHeights = function () {
    var topButtonsHeight = angular.element('.top-buttons').outerHeight(true);
    var searchBlockHeight = angular.element('.search-block').outerHeight(true);
    var selectedCategoryBlockHeight = angular.element('.selected-category').outerHeight(true);
    var navBlockHeight = angular.element('.navigation-bar').outerHeight(true);
    var bodyHeight = angular.element('body').outerHeight(true);
    $scope.categoriesBlockHeight = bodyHeight - topButtonsHeight - searchBlockHeight;
    $scope.itemsListHeight = bodyHeight - topButtonsHeight - searchBlockHeight - selectedCategoryBlockHeight;
    $scope.detailsBlockHeight = bodyHeight - topButtonsHeight - searchBlockHeight - selectedCategoryBlockHeight - navBlockHeight;
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
    $scope.loadAndOpenItemByNumber();
    $scope.authService.currentUserHolder = $scope.authService.currentUserHolder;
  });

  $scope.$on('$routeUpdate', function (event) {
    $scope.loadAndOpenItemByNumber();
  });

}