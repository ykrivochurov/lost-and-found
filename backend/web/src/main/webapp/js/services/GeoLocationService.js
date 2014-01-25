angular.module('laf').
  factory('GeoLocationService', function (UtilsService) {
    return {
      location: function (DGisMap) {
        if (navigator.geolocation) {
          navigator.geolocation.getCurrentPosition(function (position) {
            var longitude = position.coords.longitude,
              latitude = position.coords.latitude;
            DGisMap.setCenter(new DG.GeoPoint(longitude, latitude));
            DGisMap.setZoom(DGisMap.getMaxZoom());

            if (UtilsService.isFunction(DGisMap.onCurrentLocation)) {
              DGisMap.onCurrentLocation(longitude, latitude);
            }
          }, function (error) {
            switch (error.code) {
              case error.TIMEOUT:
                alert('Определить местоположение не удалась, поскольку был превышен интервал ожидания окончания процеса!');
                break;
              case error.POSITION_UNAVAILABLE:
                alert('Местоположение не может быть определено! Например, один или более провайдеров, используемых в процессе определения местоположения, сообщил о внутренней ошибке, которая вызвала сбой процесса полностью.');
                break;
              case error.PERMISSION_DENIED:
                alert('Определить местоположение не удалась, поскольку документ не имеет права использовать Geolocation API!');
                break;
              case error.UNKNOWN_ERROR:
                alert('Определить местоположение не удалась! Неизвестная ошибка.');
                break;
            }
          }, {timeout: 10000});
        } else {
          alert('Ваш браузер не поддерживает Geolocation API! Попробуйте использовать современные браузеры: Chrome, Firefox, Safari, Opera');
        }
      }
    };
  });
