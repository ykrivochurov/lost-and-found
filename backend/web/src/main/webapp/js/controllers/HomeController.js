function HomeController($q, $scope, $modal, $timeout, $animate, $sce, GeoLocationService, UtilsService, ItemsService, CategoriesService, MapService, AuthService, CityService, $location, ShareService, MessagesService) {
  $scope.shareService = ShareService;
  $scope.authService = AuthService;
  $scope.authService.applyCallback = function () {
    if (!$scope.$$phase) {
      $scope.$apply();
    }
  };

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
  $scope.tagsIcons = TAGS_ICONS;
  $scope.pinIcons = PIN_ICONS;
  $scope.searchQuery = null;
  $scope.selectedCategory = null;
  $scope.selectedTag = null;
  $scope.selectedItem = null;

  $scope.myItemsCategory = 'Мои объявления';
  $scope.searchItemsCategory = 'Результаты поиска';

  $scope.showSearchResults = true;

  $scope.showSelectedCategory = false;
  $scope.showCategoriesList = true;
  $scope.myItemsMode = false;
  $scope.searchItemsMode = false;

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
    $scope.searchItemsMode = false;
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

  $scope.selectCategoryAndTag = function (category, tag, my, search) {
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
    MapService.hideAllBalloons();
    $scope.clearSelectedItem();
    $scope.showSelectedCategory = true;
    $scope.selectCategoryAndTag(
      $scope.selectedCategory,
      $scope.selectedTag,
      $scope.myItemsMode,
      $scope.searchItemsMode
    );
  };

  $scope.clearCategorySelection = function () {
    if ($scope.myItemsMode || $scope.searchItemsMode) {
      //total reload needed after my mode
      $scope.setCategoriesListType($scope.categoriesListType);
      return;
    }
    $scope.searchQuery = null;
    $scope.showSelectedCategory = false;
    $scope.showCategoriesList = true;
    $scope.selectedCategory = null;
    $scope.selectedTag = null;
    MapService.hideAllBalloons();
    $scope.clearSelectedItem();
    $scope.mapService.showMarkersForCategory();
  };

  $scope.loadAndOpenItemByNumber = function () {
    var searchObj = $location.search();
    var number;
    if (UtilsService.isNotEmpty(searchObj.number)) {
      number = searchObj.number;
    } else {
      return;
    }
    if ($scope.isSelectedItemOpened(number)) {
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
          $scope.isCollapsed = true;
          $scope.selectCategoryAndTag($scope.getCategoryByName(item.mainCategory), item.tags[0], false, false).then(function () {
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
      $scope.selectCategoryAndTag($scope.getCategoryByName(item.mainCategory), item.tags[0], false, false).then(function () {
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

  $scope.itemClosed = function (item) {
    $scope.authService.refresh();
    if ($scope.categoriesListType == item.itemType) {
      var firstTag = item.tags[0];
      var countForTag = $scope.categoriesCounts[firstTag];
      if (UtilsService.isNotEmpty(countForTag)) {
        countForTag--;
        $scope.categoriesCounts[firstTag] = countForTag;
        $scope.categoriesRedraw();
      }
      MapService.removeMarker(item);
    }
  };

  $scope.openItem = function (item) {
    console.log('Item: ' + JSON.stringify(item));
    $scope.mapService.hideBalloonForItem($scope.selectedItem);
    $scope.setSelectedItem(item);
    $scope.showSelectedCategory = false;
    $scope.mapService.showBalloonForItem(item);
  };

  $scope.closeItem = function (item) {
    ItemsService.crud.close({numberOrId: item.id}, function (item) {
      $scope.goBackToSelectedCategory();
      $scope.itemClosed(item);
    });
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
    var selectedCategory = {name: $scope.myItemsCategory};
    $scope.isCollapsed = true;
    if (UtilsService.isNotEmpty($scope.authService.currentUserHolder)
      && UtilsService.isNotEmpty($scope.authService.currentUserHolder.id)) {
      MapService.hideAllBalloons();
      $scope.clearSelectedItem();
      $scope.showSelectedCategory = true;
      $scope.selectCategoryAndTag(selectedCategory, null, true, false);
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
        $scope.clearSelectedItem();
        $scope.showSelectedCategory = true;
        $scope.selectCategoryAndTag(selectedCategory, null, true, false);
        $scope.myItemsMode = true;
      }, function () {
        console.log('Modal dismissed at: ' + new Date());
      });
    }
  };

  $scope.clearSelectedItem = function () {
    if ($scope.selectedItem != null) {
      $location.search('');
    }
    $scope.selectedItem = null;
  };

  $scope.setSelectedItem = function (item) {
    if (item != null) {
      $location.search({number: item.number});
    }
    $scope.selectedItem = item;
    $timeout(function () {
      // recalculate scroll heights
      $scope.calcScrollHeights();
      $('.details-block-scroll').nanoScroller();
    });
  };

  $scope.isSelectedItemOpened = function (number) {
    return $scope.selectedItem != null && $scope.selectedItem.number == number;
  };


  $scope.showItemSettings = function () {
    var buttonLeft = angular.element('.settings-button').position().left;
    var buttonWidth = angular.element('.settings-button').outerWidth(true);
    var panelWidth = angular.element('.details-block .creation-date').outerWidth(true);
    var widthFix = 1;
    angular.element('.settings-block .arrow').css('right', (panelWidth - buttonLeft - buttonWidth - widthFix) + 'px');
    $scope.showSettings = !$scope.showSettings;
  };

  $scope.setNewPlace = function (cancel) {
    if ($scope.isItemCreation) {
      if (cancel) {
        $scope.middleStateItem.location = $scope.middleStateItem_loc;
        $scope.middleStateItem.where = $scope.middleStateItem_where;
      }
      $scope.createItem($scope.middleStateItem.itemType);
      $scope.deactivateMiddleStatePanel();
    } else {
      if (cancel) {
        $scope.middleStateItem.location = $scope.middleStateItem_loc;
        $scope.middleStateItem.where = $scope.middleStateItem_where;
      } else {
        $scope.middleStateItem.location = $scope.mapService.getLocationObject();
      }
      $scope.editItem($scope.middleStateItem, $scope.middleStateItem.item);
      $scope.mapService.deactivateMiddleStatePanel();
    }
  };

//  Modals part

  $scope.editItem = function (laf, item) {
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
      $scope.refreshCategories();
      $scope.mapService.moveMarker(item);
      if ($scope.selectedCategory != $scope.myItemsCategory) {
      }
    }, function () {
      console.log('Modal dismissed at: ' + new Date());
    });
  };

  $scope.createItem = function (itemType) {
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

  $scope.createMessage = function (item) {
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
  };

  $scope.showFullSIzeImage = function (item) {
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

  $scope.showLoginModal = function () {
    if (UtilsService.isNotEmpty($scope.authService.currentUserHolder)) {
      $scope.authService.currentUserHolder = $scope.authService.currentUserHolder;
    } else {
      $modal.open({
        templateUrl: 'login-win',
        controller: LoginModalController,
        windowClass: 'login-modal',
        scope: $scope
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
      $scope.isCollapsed = true;
      MapService.hideAllBalloons();
      $scope.clearSelectedItem();
      $scope.showSelectedCategory = true;
      $scope.selectCategoryAndTag({name: $scope.searchItemsCategory}, null, false, true);
      $scope.searchItemsMode = true;
      console.log($scope.searchQuery);
    }, 1000);
  }


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

function generateUrl(item) {
  return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '') + '/#/home?number=' + item.number;
}

function thumbUrl(item) {
  return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '') + '/api/items/photo/' + item.thumbnailId;
}