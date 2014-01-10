angular.module('laf').
  factory('GeoLocationService', function () {
    return {
      location: function () {
        var service = new GeolocationService();
        return service.getLocation({
          // Режим получения наиболее точных данных.
          enableHighAccuracy: true,
          // Максимальное время ожидания ответа (в миллисекундах).
          timeout: 10000,
          // Максимальное время жизни полученных данных (в миллисекундах).
          maximumAge: 1000
        });
      }
    };
  });
