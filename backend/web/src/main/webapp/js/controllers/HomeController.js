function HomeController($scope, $modal, $timeout, $animate, $sce, GeoLocationService, UtilsService, ItemsService) {
  $scope.lafBusy = false;
  $scope.cities = [
    {
      name: 'Новосибирск',
      coords: {
        lng: 82.9203497573968,
        lat: 55.030282009814
      }
    }
  ];
  $scope.map = 'Новосибирск';
  $scope.currentCountry = 'Россия';
  $scope.currentCity = $scope.cities[0];
  $scope.dateFormat = 'dd/MM/yyyy';
  $scope.isCollapsed = false;
  $scope.categoriesListType = 'lost';
  $scope.laf = {when: '', where: '', what: '', creationDate: new Date().getTime(), tags: []};
  $scope.whatDict = ['ключи', 'телефон', 'кошелек', 'сумку', 'варежку'];
  $scope.categories = [
    {
      name: 'Животные',
      tags: [
        {
          name: 'Кошки',
          count: 12
        },
        {
          name: 'Собаки',
          count: 45
        },
        {
          name: 'Другие животные',
          count: 17
        }
      ]
    },
    {
      name: 'Документы',
      tags: [
        {
          name: 'Документы',
          count: 16
        },
        {
          name: 'Паспорт',
          count: 5
        },
        {
          name: 'Водительское удостоверение',
          count: 1
        },
        {
          name: 'Пенсионное удостоверение',
          count: 4
        },
      ]
    },
    {
      name: 'Деньги',
      tags: [
        {
          name: 'Карта',
          count: 1
        },
        {
          name: 'Кошелек',
          count: 8
        }
      ]
    },
    {
      name: 'Рег. Номер',
      tags: [
        {
          name: 'Рег. Номер',
          count: 3
        }
      ]
    },
    {
      name: 'Ключи',
      tags: [
        {
          name: 'Ключи от автомобиля',
          count: 2
        },
        {
          name: 'Ключи от дома',
          count: 5
        }
      ]
    },
    {
      name: 'Сумка',
      tags: [
        {
          name: 'Сумка',
          count: 3
        }
      ]
    },
    {
      name: 'Гаджеты',
      tags: [
        {
          name: 'Мобильный телефон',
          count: 1
        },
        {
          name: 'Флешкарта',
          count: 2
        },
        {
          name: 'Планшет',
          count: 1
        },
        {
          name: 'Ноутбук',
          count: 4
        },
        {
          name: 'Плеер',
          count: 7
        },
        {
          name: 'Видеокамера',
          count: 2
        },
        {
          name: 'Фотоаппарат',
          count: 5
        }
      ]
    },
    {
      name: 'Украшения',
      tags: [
        {
          name: 'Сережки',
          count: 1
        },
        {
          name: 'Кольцо',
          count: 2
        },
        {
          name: 'Браслет',
          count: 4
        },
        {
          name: 'Цепочка',
          count: 1
        },
        {
          name: 'Кулон',
          count: 3
        },
        {
          name: 'Часы',
          count: 1
        },
      ]
    },
    {
      name: 'Другое'
    }
  ];

  $scope.tagsIcons = {
    'Кошки': '/img/categories/Animal_02.png',
    'Собаки': '/img/categories/Animal_03.png',
    'Другие животные': '/img/categories/Animal_01.png',
    'Документы': '/img/categories/Doc_02.png',
    'Паспорт': '/img/categories/Doc_03.png',
    'Водительское удостоверение': '/img/categories/Doc_01.png',
    'Пенсионное удостоверение': '/img/categories/Doc_04.png',
    'Карта': '/img/categories/Money_02.png',
    'Кошелек': '/img/categories/Money_01.png',
    'Ключи от автомобиля': '/img/categories/Key_02.png',
    'Ключи от дома': '/img/categories/Key_01.png',
    'Сумка': '/img/categories/Bag_01.png',
    'Мобильный телефон': '/img/categories/Gadget_02.png',
    'Флешкарта': '/img/categories/Gadget_06.png',
    'Планшет': '/img/categories/Gadget_05.png',
    'Плеер': '/img/categories/Gadget_01.png',
    'Видеокамера': '/img/categories/Gadget_04.png',
    'Фотоаппарат': '/img/categories/Gadget_03.png',
    'Рег. Номер': '/img/categories/Auto_01.png',
    'Сережки': '/img/categories/Y_01.png',
    'Кольцо': '/img/categories/Y_02.png',
    'Браслет': '/img/categories/Y_04.png',
    'Цепочка': '/img/categories/Y_05.png',
    'Кулон': '/img/categories/Y_03.png',
    'Часы': '/img/categories/Y_06.png'
//    'Ноутбук': '/img/categories/'
  };
  $scope.searchQuery = null;
  $scope.selectedCategory = null;
  $scope.selectedTag = null;
  $scope.selectedItem = null;

  $scope.getIconByTag = function (tagName) {
    return $scope.tagsIcons[tagName];
  };

  $scope.lafListSelect = function (value) {
    $scope.categoriesListType = value;
    angular.element('.lost-b').toggleClass('active', angular.equals(value, 'lost'));
    angular.element('.found-b').toggleClass('active', angular.equals(value, 'found'));
  };

  $scope.lostAndFoundItems = [];

  for (var i = 0; i < 3; i++) {
    $scope.lostAndFoundItems.push({
      id: 'one_' + i,
      what: 'Паспорт на имя Человека',
      where: 'г. Новосибирск, ул. Кутателадзе 4г',
      when: new Date().getTime(),
      creationDate: new Date().getTime(),
      tags: ['документы', 'паспорт'],
      author: {
        firstName: 'Иван',
        lastName: 'Иванов',
        email: 'ivan.ivanov@gmail.com',
        phone: '+79236431122'
      },
      photosIds: ['/img/sample.png'],
      location: [],
      money: false,
      hasPrev: i != 0,
      hasNext: true
    });
    $scope.lostAndFoundItems.push({
      id: 'two_' + i,
      what: 'Ключи от гаража',
      where: 'г. Новосибирск, Советский район',
      when: new Date().getTime(),
      creationDate: new Date().getTime(),
      tags: [],
      author: {
        firstName: 'Петр',
        lastName: 'Растиряшкин',
        email: 'gde.moi.kluchi@mail.ru',
        phone: ''
      },
      photosIds: [],
      location: [],
      money: true,
      hasPrev: true,
      hasNext: true
    });
    $scope.lostAndFoundItems.push({
      id: 'three_' + i,
      what: 'Плеер',
      where: 'г. Новосибирск, ул. Полевая 3',
      when: new Date().getTime(),
      creationDate: new Date().getTime(),
      tags: ['плеер'],
      author: {
        firstName: 'Федя',
        lastName: 'Музыкант',
        email: 'boom.box@rambler.ru',
        phone: ''
      },
      photosIds: [],
      location: [],
      money: false,
      hasPrev: true,
      hasNext: i != 2
    });
  }

  $scope.showSearchResults = true;

  $scope.showSelectedCategory = false;
  $scope.showCategoriesList = true;

  $scope.selectCategoryAndTag = function (category, tag) {
    $scope.showSelectedCategory = true;
    $scope.showCategoriesList = false;
    $scope.selectedCategory = category;
    $scope.selectedTag = tag;

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
  };

  $scope.openItem = function (item) {
    console.log('Item: ' + JSON.stringify(item));
    $scope.selectedItem = item;
    $scope.showSelectedCategory = false;
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
  };

  $scope.createItem = function (itemType) {
    var modalInstance = $modal.open({
      templateUrl: 'create-item-modal.html',
      controller: CreateItemModalCtrl,
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

  var CreateItemModalCtrl = function ($scope, $modalInstance, itemType, laf, whatDict, categories, lostAndFoundItems) {

    $scope.laf = laf;
    $scope.itemType = itemType;
    $scope.whatDict = whatDict;
    $scope.categories = categories;
    $scope.lostAndFoundItems = lostAndFoundItems;
    $scope.foundOnCreation = [lostAndFoundItems[0], lostAndFoundItems[1], lostAndFoundItems[2], lostAndFoundItems[3]];

    $scope.addTag = function (tag) {
      if ($scope.laf.tags.indexOf(tag) == -1) {
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

  $scope.DGisMap;
  $scope.currentLocationMarker;
  $scope.activeBallon;

  $scope.initMap = function () {
    $(function () {
      $scope.DGisMap = new DG.Map('map-panel');
      //выкл стандартных балунов
      $scope.DGisMap.geoclicker.disable();
      $scope.DGisMap.setCenter(new DG.GeoPoint($scope.currentCity.coords.lng, $scope.currentCity.coords.lat), 15);
      $scope.currentLocationMarker = new DG.Markers.Common({
        geoPoint: new DG.GeoPoint($scope.currentCity.coords.lng, $scope.currentCity.coords.lat)});
      $scope.DGisMap.markers.add($scope.currentLocationMarker);

      $scope.DGisMap.onCurrentLocation = function (longitude, latitude) {
        $scope.currentLocationMarker.setPosition(new DG.GeoPoint(longitude, latitude));
        $scope.getAddress($scope.DGisMap, longitude, latitude)
      };
      //обработка клика по карте
      $scope.DGisMap.addEventListener($scope.DGisMap.getContainerId(), 'DgClick', function (e) {
        var geoPoint = e.getGeoPoint();
        if (UtilsService.isNotEmpty($scope.activeBallon)) {
          $scope.activeBallon.hide();
        }
        //определение нового адреса
        $scope.getAddress($scope.DGisMap, geoPoint.lon, geoPoint.lat, function () {
          if (UtilsService.isEmpty($scope.activeBallon)) {
            $scope.activeBallon = new DG.Balloons.Common({
              geoPoint: geoPoint,
              contentHtml: $scope.laf.where
            });
            $scope.DGisMap.balloons.add($scope.activeBallon);
          } else {
            $scope.activeBallon.setContent($scope.laf.where);
            $scope.activeBallon.setPosition(geoPoint);
            $scope.activeBallon.show();
          }
          geoPoint.where = $scope.laf.where;
          console.log(geoPoint);
        });
        //перемещение маркера в новое место клика
        $scope.currentLocationMarker.setPosition(geoPoint);
      });
      $scope.lafBusyMessage = $sce.trustAsHtml('Определение текущего<br/>местоположения...');
      $scope.lafBusy = true;
      GeoLocationService.location($scope.DGisMap);
      $scope.rebuildMarkers();
      $scope.DGisMap.addEventListener($scope.DGisMap.getContainerId(), 'DgMapMove', function (e) {
        //todo нужна задержка в несколько сек до запроса маркеров!

      });
      $scope.DGisMap.addEventListener($scope.DGisMap.getContainerId(), 'DgZoomChange', function (e) {
        //todo нужна задержка в несколько сек до запроса маркеров!
      });
    });

  };

//  todo $scope.DGisMap.setBoundsRestrictions(cityBounds , false, false);

  $scope.rebuildMarkers = function () {
    $scope.removeMarkers();
    var bounds = $scope.DGisMap.getBounds();
    var filterParams = {
      leftTop: bounds.getLeftTop(),
      rightBottom: bounds.getRightBottom(),
      type: $scope.categoriesListType
    };//todo selected category and tag
    var items = ItemsService.crud.getItemsByBounds(filterParams, function () {
    });
    for (var i = 0; i < items.length; i++) {
      $scope.createMarker(items[i]);
    }
  };

  $scope.removeMarkers = function () {
    var groupNames = ['1', '2', '3', '4'];
    for (var i = 0; i < groupNames.length; i++) {
      $scope.DGisMap.markers.removeGroup(groupNames[i]);
    }
  };

  $scope.createMarker = function (item) {
    var marker = new DG.Markers.MarkerWithBalloon({
      geoPoint: new DG.GeoPoint(item.location[0], item.location[1]),
      icon: new DG.Icon($scope.tagsIcons[item.tags[0]], new DG.Size(29, 29)),
      balloonOptions: {
        geoPoint: new DG.GeoPoint(item.location[0], item.location[1]),
        contentHtml: item.what
      }
    });
    $scope.DGisMap.markers.add(marker, item.mainCategory.name);
  };

  $scope.getAddress = function (mapObj, lng, lat, callback) {
    var unknownWhere = 'Неизвестное место на карте';
    mapObj.geocoder.get(new DG.GeoPoint(lng, lat),
      {
        types: ['house', 'street', 'district'],
        radius: 200,
        limit: 1,
        success: function (geocoderObjects) {
          $scope.lafBusy = false;
          var geocoderObject = geocoderObjects[0];
          var showBalloon = false;
          if (UtilsService.isNotBlank(geocoderObject.getName())) {
            $scope.laf.where = geocoderObject.getName();
            showBalloon = true;
          } else {
            $scope.laf.where = unknownWhere;
          }
          if (!$scope.$$phase) {
            $scope.$apply();
          }
          if (showBalloon && UtilsService.isFunction(callback)) {
            callback();
          }
        },
        failure: function (code, message) {
          $scope.lafBusy = false;
          $scope.laf.where = unknownWhere;
          if (!$scope.$$phase) {
            $scope.$apply();
          }
          console.log(code + ' ' + message);
        }
      });
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

  $timeout(function () {
    $scope.calcScrollHeights();
    $scope.initMap();
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
