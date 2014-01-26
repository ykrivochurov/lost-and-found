angular.module('laf').
  factory('MapService', function ($timeout, $sce, UtilsService, GeoLocationService, ItemsService) {
    var controllerScope;
    var markersMap = {};
    return {
      init: function (scope) {
        controllerScope = scope;
      },
      initMap: function (mapContainerSelector) {
        var self = this;
        $(function () {
          controllerScope.DGisMap = new DG.Map(mapContainerSelector);
          //выкл стандартных балунов
          controllerScope.DGisMap.geoclicker.disable();
          controllerScope.DGisMap.setCenter(new DG.GeoPoint(controllerScope.currentCity.coords.lng, controllerScope.currentCity.coords.lat), 15);
          controllerScope.currentLocationMarker = new DG.Markers.Common({
            geoPoint: new DG.GeoPoint(controllerScope.currentCity.coords.lng, controllerScope.currentCity.coords.lat)});
          controllerScope.DGisMap.markers.add(controllerScope.currentLocationMarker);

          controllerScope.DGisMap.onCurrentLocation = function (longitude, latitude) {
            if (UtilsService.isNotEmpty(longitude) && UtilsService.isNotEmpty(latitude)) {
              controllerScope.currentLocationMarker.setPosition(new DG.GeoPoint(longitude, latitude));
              self.getAddress(controllerScope.DGisMap, longitude, latitude)
            } else {
              controllerScope.hideBusy();
              console.log('hide busy');
            }
          };
          //обработка клика по карте
          controllerScope.DGisMap.addEventListener(controllerScope.DGisMap.getContainerId(), 'DgClick', function (e) {
            var geoPoint = e.getGeoPoint();
            if (UtilsService.isNotEmpty(controllerScope.activeBallon)) {
              controllerScope.activeBallon.hide();
            }
            //определение нового адреса
            self.getAddress(controllerScope.DGisMap, geoPoint.lon, geoPoint.lat, function () {
              if (UtilsService.isEmpty(controllerScope.activeBallon)) {
                controllerScope.activeBallon = new DG.Balloons.Common({
                  geoPoint: geoPoint,
                  contentHtml: controllerScope.laf.where
                });
                controllerScope.DGisMap.balloons.add(controllerScope.activeBallon);
              } else {
                controllerScope.activeBallon.setContent(controllerScope.laf.where);
                controllerScope.activeBallon.setPosition(geoPoint);
                controllerScope.activeBallon.show();
              }
              geoPoint.where = controllerScope.laf.where;
            });
            //перемещение маркера в новое место клика
            controllerScope.currentLocationMarker.setPosition(geoPoint);
          });
          controllerScope.showBusy('Определение текущего<br/>местоположения...');
          GeoLocationService.location(controllerScope.DGisMap);


          self.rebuildMarkers();
          /* todo на данный момент мы грузим все маркеры для выбранного города... Для этого нужно ограничить область города... и хранить эти границы в базе
           controllerScope.DGisMap.addEventListener(controllerScope.DGisMap.getContainerId(), 'DgMapMove', function (e) {
           self.rebuildMarkers();
           });
           controllerScope.DGisMap.addEventListener(controllerScope.DGisMap.getContainerId(), 'DgZoomChange', function (e) {
           self.rebuildMarkers();
           });
           */
        });

      },

      rebuildMarkers: function () {
        var self = this;
        if (UtilsService.isNotEmpty(controllerScope.rebuildMarkersPromise)) {
          $timeout.cancel(controllerScope.rebuildMarkersPromise);
        }

//        controllerScope.showBusy('Загрузка объявлений...');

        console.log('outside timer');
        controllerScope.rebuildMarkersPromise = $timeout(function () {
          console.log('inside timer');
          self.removeMarkers();
          var bounds = controllerScope.DGisMap.getBounds();
          var filterParams = {
            leftTop: bounds.getLeftTop(),
            rightBottom: bounds.getRightBottom(),
            type: controllerScope.categoriesListType
          };//todo selected category and tag
          var items = ItemsService.crud.getItemsByBounds(filterParams, function () {
          });
          for (var i = 0; i < items.length; i++) {
            self.createMarker(items[i]);
          }
//          controllerScope.hideBusy();
        }, 100);
      },

      removeMarkers: function () {
        console.log('Remove markers');
        var groupNames = ['1', '2', '3', '4'];
        for (var i = 0; i < groupNames.length; i++) {
          controllerScope.DGisMap.markers.removeGroup(groupNames[i]);
        }
      },

      createMarker: function (item) {
        console.log('Create marker');
        var marker = new DG.Markers.MarkerWithBalloon({
          geoPoint: new DG.GeoPoint(item.location[0], item.location[1]),
          icon: new DG.Icon(controllerScope.tagsIcons[item.tags[0]], new DG.Size(29, 29)),
          balloonOptions: {
            geoPoint: new DG.GeoPoint(item.location[0], item.location[1]),
            contentHtml: item.what
          }
        });
        markersMap[item.id] = marker;
        controllerScope.DGisMap.markers.add(marker, item.mainCategory);
      },

      showMarkersForCategory: function (selectedCategory) {
        var defaultGroup = controllerScope.DGisMap.markers.getDefaultGroupName();
        var allGroupsNames = controllerScope.DGisMap.markers.getAllGroupsNames();
        if (UtilsService.isNotEmpty(selectedCategory)) {
          var selectedGroup = controllerScope.DGisMap.markers.getGroup(selectedCategory.name);
          for (var i = 0; i < allGroupsNames.length; i++) {
            var groupName = allGroupsNames[i];
            controllerScope.DGisMap.markers.getGroup(groupName).hide();
          }
          selectedGroup.show();
          defaultGroup.show();
        } else {
          for (var i = 0; i < allGroupsNames.length; i++) {
            var groupName = allGroupsNames[i];
            controllerScope.DGisMap.markers.getGroup(groupName).show();
          }
        }
      },

      showBalloonForItem: function (item) {
        if (UtilsService.isNotEmpty(item)) {
          var marker = markersMap[item.id];
          controllerScope.DGisMap.setCenter(marker.getPosition());
          marker.showBalloon();
        }
      },

      getAddress: function (mapObj, lng, lat, callback) {
        console.log('Get address');
        var unknownWhere = 'Неизвестное место на карте';
        mapObj.geocoder.get(new DG.GeoPoint(lng, lat),
          {
            types: ['house', 'street', 'district'],
            radius: 200,
            limit: 3,
            success: function (geocoderObjects) {
              controllerScope.hideBusy();
              var geocoderObject;
              for (var i = 0; i < geocoderObjects.length; i++) {
                var obj = geocoderObjects[i];
                if (UtilsService.isEmpty(geocoderObject)) {
                  geocoderObject = obj;
                }
                if (geocoderObject.getType() != 'house'
                  && obj.getType() == 'house'
                  && UtilsService.isNotBlank(obj.getName())) {
                  geocoderObject = obj;
                }
              }
              var showBalloon = false;
              if (UtilsService.isNotBlank(geocoderObject.getName())) {
                controllerScope.laf.where = geocoderObject.getName();
                showBalloon = true;
              } else {
                controllerScope.laf.where = unknownWhere;
              }
              if (!controllerScope.$$phase) {
                controllerScope.$apply();
              }
              if (showBalloon && UtilsService.isFunction(callback)) {
                callback();
              }
            },
            failure: function (code, message) {
              controllerScope.hideBusy();
              controllerScope.laf.where = unknownWhere;
              if (!controllerScope.$$phase) {
                controllerScope.$apply();
              }
              console.log(code + ' ' + message);
            }
          });
      }
    };
  });