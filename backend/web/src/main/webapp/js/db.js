CITIES = [
  {
    name: 'Новосибирск',
    coords: {
      lng: 82.9203497573968,
      lat: 55.030282009814
    }
  }
];

CATEGORIES = [
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

TAGS_ICONS = {
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

LOST_AND_FOUND_ITEMS = [];
(function () {
  for (var i = 0; i < CATEGORIES.length; i++) {
    var category = CATEGORIES[i];
    for (var j = 0; j < category.tags.length; j++) {
      var tag = category.tags[j];
      LOST_AND_FOUND_ITEMS.push({
        id: category.name + j,
        what: 'Потерял ' + tag.name,
        where: 'г. Новосибирск, ул. Кутателадзе 4г',
        when: new Date().getTime(),
        creationDate: new Date().getTime(),
        mainCategory: category.name,
        tags: [tag.name, 'паспорт'],
        author: {
          firstName: 'Иван',
          lastName: 'Иванов',
          email: 'ivan.ivanov@gmail.com',
          phone: '+79236431122'
        },
        photosIds: ['/img/sample.png'],
        location: [randomBuilder(83.041939445684, 83.151802726935), randomBuilder(54.840497170055, 54.862238749854)],
        money: false,
        hasPrev: i != 0,
        hasNext: true
      });
    }
  }
  /*
   for (var i = 0; i < 3; i++) {
   LOST_AND_FOUND_ITEMS.push({
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
   LOST_AND_FOUND_ITEMS.push({
   id: 'two_' + i,
   what: 'Ключи от гаража',
   where: 'г. Новосибирск, Советский район',
   when: new Date().getTime(),
   creationDate: new Date().getTime(),
   tags: [],
   author: {
   firstName: 'Петр',
   lastName: 'Растеряшкин',
   email: 'gde.moi.kluchi@mail.ru',
   phone: ''
   },
   photosIds: [],
   location: [],
   money: true,
   hasPrev: true,
   hasNext: true
   });
   LOST_AND_FOUND_ITEMS.push({
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
   */
})();

function randomBuilder(min, max) {
  return Math.random() * (max - min) + min;
}