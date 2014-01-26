angular.module('laf').
  factory('ItemsService', function ($resource) {
    return {
      crud: {
        getItemsByBounds: function (filterParams, callback) {
          //todo lost or found
          return LOST_AND_FOUND_ITEMS;
/*
          return [
            {
              id: 'lafDBId_1',
              tags: ['Кошки'],
              mainCategory: {name: '1'},
              when: 1390668423157,
              what: 'Рыжая кошечка',
              location: [83.191575917335, 54.936353575202],
              where: "Кольцово, Молодёжная, 6"
            },
            {
              id: 'lafDBId_2',
              tags: ['Собаки'],
              mainCategory: {name: '2'},
              when: 1390668423157,
              what: 'Белый пес',
              location: [83.190599593255, 54.936079295561],
              where: "Кольцово, Молодёжная, 8"
            },
            {
              id: 'lafDBId_3',
              tags: ['Другие животные'],
              mainCategory: {name: '3'},
              when: 1390668423157,
              what: 'Хомяк пропал, спасите',
              location: [83.185964736076, 54.938532336908],
              where: "Кольцово, Кольцово пос, 14"
            },
            {
              id: 'lafDBId_4',
              tags: ['Документы'],
              mainCategory: {name: '4'},
              when: 1390668423157,
              what: 'Все пропало!!',
              location: [83.185063513847, 54.942156154303],
              where: "Кольцово, Кольцово пос, 18а"
            },
            {
              id: 'lafDBId_5',
              tags: ['Кошки'],
              mainCategory: {name: '1'},
              when: 1390668423157,
              what: 'Черный кот выпрыгнул из окна и бежал',
              location: [83.107386740777, 54.86546873235],
              where: "Новосибирск, Полевая, 16"
            },
            {
              id: 'lafDBId_6',
              tags: ['Собаки'],
              mainCategory: {name: '2'},
              when: 1390668423157,
              what: 'Балонка пропала',
              location: [83.103073748681, 54.866888825367],
              where: "Новосибирск, Полевая, 5"
            },
            {
              id: 'lafDBId_7',
              tags: ['Документы'],
              mainCategory: {name: '3'},
              when: 1390668423157,
              what: 'Доки на квартиру',
              location: [83.10043445501, 54.86666655324],
              where: "Новосибирск, Советский"
            },
            {
              id: 'lafDBId_8',
              tags: ['Другие животные'],
              mainCategory: {name: '4'},
              when: 1390668423157,
              what: 'Тритоны уползли',
              location: [83.098868044946, 54.865283499138],
              where: "Новосибирск, Советский"
            },
            {
              id: 'lafDBId_9',
              tags: ['Кошки'],
              mainCategory: {name: '1'},
              when: 1390668423157,
              what: 'Три кошки ушли в троем',
              location: [83.09131494436, 54.865085916108],
              where: "Новосибирск, Молодёжи бульвар, 36Б"
            },
            {
              id: 'lafDBId_10',
              tags: ['Собаки'],
              mainCategory: {name: '2'},
              when: 1390668423157,
              what: 'Собака улыбака, хозяина бросака',
              location: [83.096636447045, 54.859797123031],
              where: "Новосибирск, Строителей проспект, 25 к2"
            },
            {
              id: 'lafDBId_11',
              tags: ['Документы'],
              mainCategory: {name: '3'},
              when: 1390668423157,
              what: 'Кирилл Кириллович Кириллов потерял паспорт на имя Иванова Ивана Ивановича',
              location: [83.101485880944, 54.860229391343],
              where: "Новосибирск, Кутателадзе, 7/3"
            },
            {
              id: 'lafDBId_12',
              tags: ['Другие животные'],
              mainCategory: {name: '4'},
              when: 1390668423157,
              what: 'Тюлень уплыл',
              location: [83.099608334634, 54.857203415854],
              where: "Новосибирск, Академика Лаврентьева проспект, 1 / Кутателадзе, 2"
            },
            {
              id: 'lafDBId_13',
              tags: ['Кошки'],
              mainCategory: {name: '1'},
              when: 1390668423157,
              what: 'Лысый кот ищет шерсть',
              location: [83.100305708977, 54.851181666812],
              where: "Новосибирск, Академика Лаврентьева проспект, 6/1"
            },
            {
              id: 'lafDBId_14',
              tags: ['Собаки'],
              mainCategory: {name: '2'},
              when: 1390668423157,
              what: 'Пропала собака',
              location: [83.109382304284, 54.843478693995],
              where: "Новосибирск, Терешковой, 19"
            },
            {
              id: 'lafDBId_15',
              tags: ['Документы'],
              mainCategory: {name: '3'},
              when: 1390668423157,
              what: 'Потерял военник, ну и шут с ним =)',
              location: [83.105734500023, 54.838665906097],
              where: "Новосибирск, Морской проспект"
            },
            {
              id: 'lafDBId_16',
              tags: ['Другие животные'],
              mainCategory: {name: '4'},
              when: 1390668423157,
              what: 'Бла бла бла',
              location: [83.106539162729, 54.838208692286],
              where: "Новосибирск, Советский"
            },
            {
              id: 'lafDBId_17',
              tags: ['Кошки'],
              mainCategory: {name: '1'},
              when: 1390668423157,
              what: 'Матроскин ищет матроску',
              location: [83.095477732751, 54.839296111238],
              where: "Новосибирск, Ильича, 6 / Цветной проезд, 2"
            },
            {
              id: 'lafDBId_18',
              tags: ['Документы'],
              mainCategory: {name: '2'},
              when: 1390668423157,
              what: 'Матроскин ищет документы на корову',
              location: [83.100970896813, 54.835143990333],
              where: "Новосибирск, Советский"
            }
          ];
*/
        }
      }
      /*
       crud: $resource('api/comments/:commentId', {commentId: '@commentId'},
       {
       create: {method: 'PUT'}
       })
       */
    };
  });