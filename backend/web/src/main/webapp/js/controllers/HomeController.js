function HomeController($scope, $filter, $location, $anchorScroll, $timeout, $animate) {
  $scope.currentCity = 'Новосибирск';
  $scope.dateFormat = 'dd/MM/yyyy';
  $scope.isCollapsed = true;
  $scope.categoriesListType = 'lost';
  $scope.laf = {when: '', where: '', what: ''};
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
    'Фотоаппарат': '/img/categories/Gadget_03.png'
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
        location: []
      });
      $scope.lostAndFoundItems.push({
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
        location: []
      });
      $scope.lostAndFoundItems.push({
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
        location: []
      });
    }
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

  $scope.joinTags = function (tags) {
    if (angular.isArray(tags) && tags.length > 0) {
      return tags.join(', ');
    }
    return null;
  };

  //todo investigate
  $scope.hideWithAnimation = function (event) {
    $animate.addClass(angular.element(event.target), 'hide-to-right');
  };

  $timeout(function () {
    //todo doesn't work
    $('.scroll-content').scrollbars();
  });
}