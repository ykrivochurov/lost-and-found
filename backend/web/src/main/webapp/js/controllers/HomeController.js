function HomeController($scope, $modal, $timeout, $animate, GeoLocationService) {
  $scope.map = 'Новосибирск';
  $scope.currentCountry = 'Россия';
  $scope.currentCity = 'Новосибирск';
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

  $scope.showSearchResults = true;

  $scope.showSelectedCategory = false;
  $scope.showCategoriesList = true;

  $scope.selectCategoryAndTag = function (category, tag) {
    $scope.showSelectedCategory = true;
    $scope.showCategoriesList = false;
    $scope.selectedCategory = category;
    $scope.selectedTag = tag;
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
        photosIds: [],
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

  var CreateItemModalCtrl = function ($scope, $modalInstance, itemType, laf, whatDict, categories) {

    $scope.laf = laf;
    $scope.itemType = itemType;
    $scope.whatDict = whatDict;
    $scope.categories = categories;

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

  $scope.initMap = function () {
    //current location
    var location = GeoLocationService.location();
    location.then(function (loc) {
      console.log(loc);
      var coords = [loc.latitude, loc.longitude];
      $scope.locationPlacemark = new ymaps.Placemark(coords, {}, {
        iconImageHref: '/img/icon_03.png',
        iconImageSize: [24, 24],
        iconImageOffset: [-12, -12]
      });

      $scope.getAddress(coords);

      var cityObjects = ymaps.geoQuery(ymaps.geocode($scope.currentCountry + ', ' + $scope.currentCity, {kind: 'locality'}));

      cityObjects.then(function () {
        var bounds = cityObjects.get(0).properties.get('boundedBy');
        console.log('loc.zoom ' + loc.zoom);
        $scope.map = new ymaps.Map('map-panel', {
          center: coords,
          zoom: 15,
          behaviors: ['default', 'scrollZoom']
        }, {
          restrictMapArea: bounds
        });

        $scope.map.geoObjects.add($scope.locationPlacemark);
        $scope.map.container.fitToViewport();
        $scope.map.events.add('click', function (e) {
          var clickCoords = e.get('coordPosition');
          $scope.locationPlacemark.geometry.setCoordinates(clickCoords);
          $scope.getAddress(clickCoords);
        });
      });
    });
  };

  $scope.getAddress = function (coords) {
    ymaps.geocode(coords).then(function (res) {
      var firstGeoObject = res.geoObjects.get(0);
      $scope.laf.where = firstGeoObject.properties.get('name') + ', ' + firstGeoObject.properties.get('description');
//      $scope.laf.where = firstGeoObject.properties.get('text');
      console.log(firstGeoObject);
      if (!$scope.$$phase) {
        $scope.$apply();
      }
      console.log('$scope.laf.where ' + $scope.laf.where);
//      myPlacemark.properties
//        .set({
//          iconContent: firstGeoObject.properties.get('name'),
//          balloonContent: firstGeoObject.properties.get('text')
//        })
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
    ymaps.ready($scope.initMap);
    //todo doesn't work
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
