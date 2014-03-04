SONIC_OPTS = {

  width: 110,
  height: 50,
  padding: 5,

  stepsPerFrame: 2,
  trailLength: 1,
  pointDistance: .03,

  strokeColor: '#000000',

  step: 'fader',

  multiplier: 2,

  setup: function () {
    this._.lineWidth = 5;
  },

  path: [

    ['arc', 10, 10, 10, -270, -90],
    ['bezier', 10, 0, 40, 20, 20, 0, 30, 20],
    ['arc', 40, 10, 10, 90, -90],
    ['bezier', 40, 0, 10, 20, 30, 0, 20, 20]
  ]
};
SPINER_OPTS = {
  lines: 9,
  length: 1,
  width: 3,
  radius: 6,
  corners: 1,
  rotate: 0,
  direction: 1,
  color: '#000',
  speed: 0.9,
  trail: 62,
  shadow: false,
  hwaccel: false,
  className: 'spinner',
  zIndex: 2e9,
  top: 'auto',
  left: 'auto'
};

WHAT_PREFFIX = {
  'LOST': 'Я потерял ',
  'FOUND': 'Я нашел '
};

WHAT_PREDEF = {
  'Кошка': 'Кошку',
  'Собака': 'Собаку',
  'Другое животное': 'Другое животное',
  'Документы': 'Документы',
  'Паспорт': 'Паспорт',
  'Водительское удостоверение': 'Водительское удостоверение',
  'Пенсионное удостоверение': 'Пенсионное удостоверение',
  'Карта': 'Карту',
  'Кошелек': 'Кошелек',
  'Ключи от автомобиля': 'Ключи от автомобиля',
  'Ключи от дома': 'Ключи от дома',
  'Сумка': 'Сумку',
  'Мобильный телефон': 'Мобильный телефон',
  'Флешкарта': 'Флешкарту',
  'Планшет': 'Планшет',
  'Плеер': 'Плеер',
  'Видеокамера': 'Видеокамеру',
  'Фотоаппарат': 'Фотоаппарат',
  'Ноутбук': 'Ноутбук',
  'Рег. Номер': 'Рег. Номер',
  'Сережки': 'Сережки',
  'Кольцо': 'Кольцо',
  'Браслет': 'Браслет',
  'Цепочка': 'Цепочку',
  'Кулон': 'Кулон',
  'Часы': 'Часы',
  'Другое': 'Другое'
};
TAGS_ICONS = {
  'Кошка': '/img/categories/Animal_02.png',
  'Собака': '/img/categories/Animal_03.png',
  'Другое животное': '/img/categories/Animal_01.png',
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
  'Ноутбук': '/img/categories/Gadget_07.png',
  'Рег. Номер': '/img/categories/Auto_01.png',
  'Сережки': '/img/categories/Y_01.png',
  'Кольцо': '/img/categories/Y_02.png',
  'Браслет': '/img/categories/Y_04.png',
  'Цепочка': '/img/categories/Y_05.png',
  'Кулон': '/img/categories/Y_03.png',
  'Часы': '/img/categories/Y_06.png',
  'Другое': '/img/categories/all.png'
};
PIN_ICONS = {
  'Кошка': '/img/pins/P-Animal_02.png',
  'Собака': '/img/pins/P-Animal_03.png',
  'Другое животное': '/img/pins/P-Animal_01.png',
  'Документы': '/img/pins/P-Doc_02.png',
  'Паспорт': '/img/pins/P-Doc_03.png',
  'Водительское удостоверение': '/img/pins/P-Doc_01.png',
  'Пенсионное удостоверение': '/img/pins/P-Doc_04.png',
  'Карта': '/img/pins/P-Money_02.png',
  'Кошелек': '/img/pins/P-Money_01.png',
  'Ключи от автомобиля': '/img/pins/P-Key_02.png',
  'Ключи от дома': '/img/pins/P-Key_01.png',
  'Сумка': '/img/pins/P-Bag_01.png',
  'Мобильный телефон': '/img/pins/P-Gadget_02.png',
  'Флешкарта': '/img/pins/P-Gadget_06.png',
  'Планшет': '/img/pins/P-Gadget_05.png',
  'Плеер': '/img/pins/P-Gadget_01.png',
  'Видеокамера': '/img/pins/P-Gadget_04.png',
  'Ноутбук': '/img/pins/P-Gadget_07.png',
  'Фотоаппарат': '/img/pins/P-Gadget_03.png',
  'Рег. Номер': '/img/pins/P-Auto_01.png',
  'Сережки': '/img/pins/P-Y_01.png',
  'Кольцо': '/img/pins/P-Y_02.png',
  'Браслет': '/img/pins/P-Y_04.png',
  'Цепочка': '/img/pins/P-Y_05.png',
  'Кулон': '/img/pins/P-Y_03.png',
  'Часы': '/img/pins/P-Y_06.png',
  'Другое': '/img/pins/P-all.png'
};