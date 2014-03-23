angular.module('laf').
  factory('MapService', function ($q, $timeout, $sce, $http, $location, UtilsService, GeoLocationService, ItemsService) {
    moment.lang('ru');
    var defaultGroupName = 'default_markers';
    var controllerScope;
    var markersMap = {};

    function createBalloonContent(item) {
      var container = angular.element('<div></div>');
      container.append();

      var wrapper = angular.element('<a href="' + UtilsService.generateUrl(item) + '" class="clear-link"></a>');
      container.append(wrapper);

      angular.element('.balloon-content').removeClass('lost');
      angular.element('.balloon-content').removeClass('found');
      if (item.itemType == 'LOST') {
        angular.element('.balloon-content').addClass('lost');
      } else {
        angular.element('.balloon-content').addClass('found');
      }
      angular.element('.balloon-content .when').text(moment(item.when).format("DD MMM YYYY"));
      angular.element('.balloon-content .what').text(item.what);
      wrapper.append(angular.element('.balloon-content-wrapper').html());
      return container.html();
    }

    if ($location.url().indexOf('yandex') == -1) {
      return {
        init: function (scope) {
          controllerScope = scope;
        },
        getLocationObject: function () {
          var position = controllerScope.currentLocationMarker.getPosition();
          return [position.lon, position.lat];
        },
        initMap: function (mapContainerSelector) {
          var self = this;
          $(function () {
            controllerScope.DGisMap = new DG.Map(mapContainerSelector);
            //выкл стандартных балунов
            controllerScope.DGisMap.geoclicker.disable();
            controllerScope.DGisMap.setCenter(new DG.GeoPoint(controllerScope.currentCity.center[0], controllerScope.currentCity.center[1]), 15);
            /*
             var zoomControl = new DG.Controls.Zoom();
             controllerScope.DGisMap.zoomControl = zoomControl;
             controllerScope.DGisMap.controls.add(zoomControl);
             var zoomControlPosition = new DG.ControlPosition(DG.ControlPosition.TOP_LEFT, new DG.Point(54, 80));
             zoomControlPosition.apply(zoomControl.getContainer());
             */
            controllerScope.currentLocationMarker = new DG.Markers.Common({
              geoPoint: new DG.GeoPoint(controllerScope.currentCity.center[0], controllerScope.currentCity.center[1]),
              icon: new DG.Icon('/img/pins/P-ia_poterial_mini.png', new DG.Size(46, 68)),
              hoverIcon: new DG.Icon('/img/pins/P-ia_poterial.png', new DG.Size(80, 128))
            });
            controllerScope.DGisMap.markers.add(controllerScope.currentLocationMarker, defaultGroupName);

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
                self.hideAllBalloons();
                angular.element('.balloon-content .what').text(controllerScope.laf.where);
                if (UtilsService.isEmpty(controllerScope.activeBallon)) {
                  controllerScope.activeBallon = new DG.Balloons.Common({
                    geoPoint: geoPoint,
                    contentHtml: angular.element('.active-balloon-content-wrapper').html(),
                    isClosed: false
                  });
//                controllerScope.DGisMap.balloons.add(controllerScope.activeBallon);
                } else {
                  controllerScope.activeBallon.setContent(angular.element('.active-balloon-content-wrapper').html());
                  controllerScope.activeBallon.setPosition(geoPoint);
//                controllerScope.activeBallon.show();
                }
                geoPoint.where = controllerScope.laf.where;
              });
              //перемещение маркера в новое место клика
              controllerScope.currentLocationMarker.setPosition(geoPoint);
            });
//          todo пока не ясно, нужно ли нам определять текущее местополодение
//          controllerScope.showBusy('Определение текущего<br/>местоположения...');
//          GeoLocationService.location(controllerScope.DGisMap);


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
          var deferred = $q.defer();
          console.log('outside timer');
          controllerScope.rebuildMarkersPromise = $timeout(function () {
            console.log('inside timer');
            self.removeMarkers();
            var bounds = controllerScope.DGisMap.getBounds();
            ItemsService.crud.getMarkers({itemType: controllerScope.categoriesListType, cityId: controllerScope.currentCity.id},
              function (items) {
                for (var i = 0; i < items.length; i++) {
                  self.createMarker(items[i]);
                }
                deferred.resolve();
              });
//          controllerScope.hideBusy();
          }, 100);
          return deferred.promise;
        },

        drawMarkersDirectly: function (items) {
          this.removeMarkers();
          for (var i = 0; i < items.length; i++) {
            this.createMarker(items[i]);
          }
        },

        removeMarkers: function () {
          console.log('Remove markers');
          this.hideAllBalloons();
          var groupNames = controllerScope.categories;
          if (UtilsService.isNotEmptyArray(groupNames)) {
            for (var i = 0; i < groupNames.length; i++) {
              controllerScope.DGisMap.markers.removeGroup(groupNames[i].name);
            }
          }
        },

        removeMarker: function (item) {
          this.hideBalloonForItem(item);
          controllerScope.DGisMap.markers.remove(markersMap[item.id]);
        },

        moveMarker: function (item) {
          this.hideBalloonForItem(item);
          var marker = markersMap[item.id];
          if (UtilsService.isNotEmpty(marker)) {
            marker.setPosition(new DG.GeoPoint(item.location[0], item.location[1]));
            controllerScope.DGisMap.setCenter(marker.getPosition());
            this.showBalloonForItem(item);
          }
        },

        updateMarkerBalloon: function (item) {
          this.hideBalloonForItem(item);
          var marker = markersMap[item.id];
          if (UtilsService.isNotEmpty(marker)) {
            marker.setBalloonContent(createBalloonContent(item));
            this.showBalloonForItem(item);
          }
        },

        createMarker: function (item) {
          if (UtilsService.isEmpty(item.location)) {
            console.log('Unable create marker for item.id = ' + item.id);
            return;
          }

          var container = angular.element('<div></div>');
          container.append();

          var wrapper = angular.element('<a href="' + UtilsService.generateUrl(item) + '" class="clear-link"></a>');
          container.append(wrapper);

          angular.element('.balloon-content').removeClass('lost');
          angular.element('.balloon-content').removeClass('found');
          if (item.itemType == 'LOST') {
            angular.element('.balloon-content').addClass('lost');
          } else {
            angular.element('.balloon-content').addClass('found');
          }
          angular.element('.balloon-content .when').text(moment(item.when).format("DD MMM YYYY"));
          angular.element('.balloon-content .what').text(item.what);
          wrapper.append(angular.element('.balloon-content-wrapper').html());
          var marker = new DG.Markers.MarkerWithBalloon({
            geoPoint: new DG.GeoPoint(item.location[0], item.location[1]),
            icon: new DG.Icon(controllerScope.pinIcons[item.tags[0]], new DG.Size(46, 68)),
            balloonOptions: {
              geoPoint: new DG.GeoPoint(item.location[0], item.location[1]),
              contentHtml: createBalloonContent(item),
              showLatestOnly: true,
              isClosed: false
            }
          });
          markersMap[item.id] = marker;
          controllerScope.DGisMap.markers.add(marker, item.mainCategory);
        },

        showMarkersForCategory: function (selectedCategory) {
          var defaultGroup = controllerScope.DGisMap.markers.getGroup(defaultGroupName);
          var allGroupsNames = controllerScope.DGisMap.markers.getAllGroupsNames();
          if (UtilsService.isNotEmpty(selectedCategory)) {
            var selectedGroup = controllerScope.DGisMap.markers.getGroup(selectedCategory.name);
            for (var i = 0; i < allGroupsNames.length; i++) {
              var groupName = allGroupsNames[i];
              controllerScope.DGisMap.markers.getGroup(groupName).hide();
            }
            if (UtilsService.isNotEmpty(selectedGroup)) {
              selectedGroup.show();
            }
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

        hideBalloonForItem: function (item) {
          if (UtilsService.isNotEmpty(item)) {
            var marker = markersMap[item.id];
            controllerScope.DGisMap.setCenter(marker.getPosition());
            marker.hideBalloon();
          }
        },

        hideAllBalloons: function () {
          var all = controllerScope.DGisMap.markers.getAll();
//        var all = controllerScope.DGisMap.balloons.getAll();
          for (var i = 0; i < all.length; i++) {
            if (UtilsService.isNotEmpty(all[i]) && UtilsService.isFunction(all[i].hideBalloon)) {
              all[i].hideBalloon();
            }
          }
        },

        getAddress: function (mapObj, lng, lat, callback) {
          console.log('Get address');
          var unknownWhere = 'Неизвестное место на карте';
          mapObj.geocoder.get(new DG.GeoPoint(lng, lat),
            {
              types: ['house', 'street', 'city', 'district', 'living_area', 'place', 'station_platform', 'settlement'],
              radius: 200,
              limit: 4,
              success: function (geocoderObjects) {
                controllerScope.hideBusy();
                var geocoderObject;
                for (var i = 0; i < geocoderObjects.length; i++) {
                  var obj = geocoderObjects[i];
                  console.log('a: ' + obj.getName());
                  if (UtilsService.isEmpty(geocoderObject) && UtilsService.isNotBlank(obj.getName())) {
                    geocoderObject = obj;
                  }
                  if (UtilsService.isNotEmpty(geocoderObject)
                    && geocoderObject.getType() != 'house'
                    && obj.getType() == 'house'
                    && UtilsService.isNotBlank(obj.getName())) {
                    geocoderObject = obj;
                  }
                }
                var showBalloon = false;
                if (UtilsService.isNotEmpty(geocoderObject) && UtilsService.isNotBlank(geocoderObject.getName())) {
                  controllerScope.laf.where = geocoderObject.getName();
                  showBalloon = true;
                } else {
                  controllerScope.laf.where = unknownWhere;
                }
                if (UtilsService.isNotEmpty(controllerScope.middleStateItem)) {
                  controllerScope.middleStateItem.where = controllerScope.laf.where;
                  console.log('where: ' + controllerScope.middleStateItem.where);
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
                if (UtilsService.isNotEmpty(controllerScope.middleStateItem)) {
                  controllerScope.middleStateItem.where = controllerScope.laf.where;
                }
                if (!controllerScope.$$phase) {
                  controllerScope.$apply();
                }
                console.log(code + ' ' + message);
              }
            });
        },

        getPointByAddress: function (address, callback) {
          console.log('address: ' + address);
          var self = this;
          controllerScope.DGisMap.geocoder.get(address,
            {
              types: ['house', 'street', 'city', 'district', 'living_area', 'place', 'station_platform', 'settlement'],
              radius: 200,
              limit: 4,
              success: function (geocoderObjects) {
                controllerScope.hideBusy();
                for (var i = 0; i < geocoderObjects.length; i++) {
                  var geocoderObject = geocoderObjects[i];

                  var geoPoint = geocoderObject.getCenterGeoPoint();
                  if (UtilsService.isNotEmpty(controllerScope.activeBallon)) {
                    controllerScope.activeBallon.hide();
                  }
                  controllerScope.DGisMap.setCenter(geoPoint, 15);
                  //перемещение маркера в новое место
                  controllerScope.currentLocationMarker.setPosition(geoPoint);

                  controllerScope.laf.location = self.getLocationObject();
                  if (UtilsService.isNotEmpty(controllerScope.middleStateItem)) {
                    controllerScope.middleStateItem.location = controllerScope.laf.location;
                  }
                  if (!controllerScope.$$phase) {
                    controllerScope.$apply();
                  }
                  break;
                }
                if (UtilsService.isFunction(callback)) {
                  callback();
                }
              },
              failure: function (code, message) {
                console.log('Unable to get point: ' + message);
                controllerScope.hideBusy();
                controllerScope.laf.location = self.getLocationObject();
                if (UtilsService.isNotEmpty(controllerScope.middleStateItem)) {
                  controllerScope.middleStateItem.location = controllerScope.laf.location;
                }
                if (!controllerScope.$$phase) {
                  controllerScope.$apply();
                }
              }
            });
        },

        activateMiddleStatePanel: function (item, isCreation) {
          var middleStatePanel = angular.element('.marker-edit-mode');
          var middleStatePanelLeft = angular.element('body').outerWidth(true) / 2 -
            middleStatePanel.outerWidth(true) / 2;
          middleStatePanel.css('left', middleStatePanelLeft + 'px');
          controllerScope.middleStateItem = item;
          controllerScope.mapFullScreen = true;
          controllerScope.middleStateItem_loc = item.location;
          controllerScope.middleStateItem_where = item.where;
          controllerScope.isItemCreation = isCreation;
        },

        deactivateMiddleStatePanel: function () {
          controllerScope.mapFullScreen = false;
          controllerScope.middleStateItem = null;
          controllerScope.middleStateItem_loc = null;
          controllerScope.middleStateItem_where = null;
          controllerScope.isItemCreation = null;
        },

        whereTypeahead: function (val) {
          return $http({method: 'POST', url: 'https://dadata.ru/api/v1/suggest/address', headers: {
            'Authorization': 'Token 8e5f640a855aaa1afb4f2ae651528faacbeee155',
            'Content-Type': 'application/json'
          }, data: {
            query: controllerScope.cities[0].name + ' ' + val
          }}).then(function (data) {
            var addresses = [];
            angular.forEach(data.data.suggestions, function (item) {
              var address = '';
//            if (item.data.city_type) {
//              address = address.concat(item.data.city_type).concat(' ');
//            }
              if (item.data.city) {
                address = address.concat(item.data.city);
              }
//            if (item.data.street_type) {
//              address = address.concat(item.data.street_type).concat(' ');
//            }
              if (item.data.street) {
                if (address.length > 0) {
                  address = address.concat(', ');
                }
                address = address.concat(item.data.street);
              }
//            if (item.data.house_type) {
//              address = address.concat(item.data.house_type).concat(' ');
//            }
              if (item.data.house) {
                if (address.length > 0) {
                  address = address.concat(', ');
                }
                address = address.concat(item.data.house);
              }
              if (UtilsService.isNotBlank(address.trim())) {
                addresses.push(address);
              }
            });

            return addresses
          });
        }
      };
    } else {
      return {
        init: function (scope) {
          controllerScope = scope;
        },
        getLocationObject: function () {
          var position = controllerScope.currentLocationMarker.geometry.getCoordinates();
          return [position[1], position[0]];
        },
        initMap: function (mapContainerSelector) {
          var self = this;
          ymaps.ready(function () {
            controllerScope.DGisMap = new ymaps.Map(mapContainerSelector,
              {
                center: [controllerScope.currentCity.center[1], controllerScope.currentCity.center[0]],
                zoom: 15
              });
            controllerScope.DGisMap.behaviors.enable('scrollZoom');
            controllerScope.DGisMap.controls.add(
              new ymaps.control.ZoomControl()
            );
            controllerScope.DGisMap.markers = {
              markers: {},
              add: function (marker, groupName) {
                var markersForGroup = this.markers[groupName];
                if (UtilsService.isEmpty(markersForGroup)) {
                  console.log('add new collection: ' + groupName);
                  markersForGroup = new ymaps.GeoObjectCollection({}, {});
                }
                controllerScope.DGisMap.geoObjects.add(markersForGroup);
                console.log('add marker ' + groupName);
                markersForGroup.add(marker);
                this.markers[groupName] = markersForGroup;
              },
              showGroup: function (groupName) {
                console.log('show group ' + groupName);
                controllerScope.DGisMap.geoObjects.add(this.markers[groupName]);
              },
              getGroup: function (groupName) {
                console.log('get group ' + groupName);
                return this.markers[groupName];
              },
              removeGroup: function (groupName) {
                console.log('remove group ' + groupName);
                var markersFromGroup = this.markers[groupName];
                if (UtilsService.isNotEmpty(markersFromGroup)) {
                  controllerScope.DGisMap.geoObjects.remove(markersFromGroup);
                  this.markers[groupName] = null;
                }
              },
              showAllGroups: function () {
                console.log('show all groups');
                for (var prop in this.markers) {
                  if (this.markers.hasOwnProperty(prop)) {
                    console.log('show group: ' + prop);
                    var group = this.markers[prop];
                    if (UtilsService.isNotEmpty(group)) {
                      controllerScope.DGisMap.geoObjects.add(group);
                    }
                  }
                }
              },
              hideAllGroups: function () {
                console.log('remove all groups');
                for (var prop in this.markers) {
                  if (this.markers.hasOwnProperty(prop)) {
                    var group = this.markers[prop];
                    if (UtilsService.isNotEmpty(group)) {
                      controllerScope.DGisMap.geoObjects.remove(group);
                    }
                  }
                }
              }
            };

            controllerScope.DGisMap.geoObjects.options.set({
              zIndex: 1,
              hintHideTimeout: 10000000
            });

            controllerScope.DGisMap.events.add('click', function (e) {
              if (UtilsService.isNotEmpty(controllerScope.activeBallon)) {
                controllerScope.activeBallon.hide();
              }

              self.getAddress(controllerScope.DGisMap, e.get('coordPosition')[0], e.get('coordPosition')[1], function () {
                self.hideAllBalloons();
              });

              controllerScope.currentLocationMarker.geometry.setCoordinates(e.get('coordPosition'));
              e.preventDefault();
            });

            controllerScope.currentLocationMarker = new ymaps.Placemark(controllerScope.DGisMap.getCenter(), {},
              {
                iconImageHref: '/img/pins/P-ia_poterial_mini.png',
                iconImageSize: [46, 68],
                iconImageOffset: [-23, -68],
                draggable: false,
                hideIconOnBalloonOpen: false
              }
            );

            controllerScope.DGisMap.markers.add(controllerScope.currentLocationMarker, defaultGroupName);
            controllerScope.DGisMap.geoObjects.add(controllerScope.currentLocationMarker);

            self.rebuildMarkers();
          });
        },

        rebuildMarkers: function () {
          var self = this;
          if (UtilsService.isNotEmpty(controllerScope.rebuildMarkersPromise)) {
            $timeout.cancel(controllerScope.rebuildMarkersPromise);
          }

//        controllerScope.showBusy('Загрузка объявлений...');
          var deferred = $q.defer();
          controllerScope.rebuildMarkersPromise = $timeout(function () {
            self.removeMarkers();
            ItemsService.crud.getMarkers({itemType: controllerScope.categoriesListType, cityId: controllerScope.currentCity.id},
              function (items) {
                for (var i = 0; i < items.length; i++) {
                  self.createMarker(items[i]);
                }
                deferred.resolve();
              });
//          controllerScope.hideBusy();
          }, 100);
          return deferred.promise;
        },

        drawMarkersDirectly: function (items) {
          this.removeMarkers();
          for (var i = 0; i < items.length; i++) {
            this.createMarker(items[i]);
          }
        },

        removeMarkers: function () {
          console.log('Remove markers');
          this.hideAllBalloons();
          var groupNames = controllerScope.categories;
          if (UtilsService.isNotEmptyArray(groupNames)) {
            for (var i = 0; i < groupNames.length; i++) {
              controllerScope.DGisMap.markers.removeGroup([groupNames[i].name]);
            }
          }
        },

        removeMarker: function (item) {
          this.hideBalloonForItem(item);
//        controllerScope.DGisMap.markers.remove(markersMap[item.id]);

          var markersFromGroup = controllerScope.DGisMap.markers.markers[item.mainCategory];
          if (UtilsService.isNotEmptyArray(markersFromGroup)) {
            markersFromGroup.remove(markersMap[item.id]);
            controllerScope.DGisMap.markers.markers[item.mainCategory] = null;
          }
        },

        moveMarker: function (item) {
          this.hideBalloonForItem(item);
          var marker = markersMap[item.id];
          if (UtilsService.isNotEmpty(marker)) {
            marker.geometry.setCoordinates([item.location[1], item.location[0]]);
            controllerScope.DGisMap.setCenter(marker.geometry.getCoordinates());
            this.showBalloonForItem(item);
          }
        },

        updateMarkerBalloon: function (item) {
          this.hideBalloonForItem(item);
          var marker = markersMap[item.id];
          if (UtilsService.isNotEmpty(marker)) {
            marker.properties.set('balloonContentBody', createBalloonContent(item));
            this.showBalloonForItem(item);
          }
        },

        createMarker: function (item) {
          if (UtilsService.isEmpty(item.location)) {
            console.log('Unable create marker for item.id = ' + item.id);
            return;
          }
          console.log('Create marker id = ' + item.id);
          var container = angular.element('<div></div>');
          container.append();

          var wrapper = angular.element('<a href="' + UtilsService.generateUrl(item) + '" class="clear-link"></a>');
          container.append(wrapper);

          angular.element('.balloon-content').removeClass('lost');
          angular.element('.balloon-content').removeClass('found');
          if (item.itemType == 'LOST') {
            angular.element('.balloon-content').addClass('lost');
          } else {
            angular.element('.balloon-content').addClass('found');
          }
          angular.element('.balloon-content .when').text(moment(item.when).format("DD MMM YYYY"));
          angular.element('.balloon-content .what').text(item.what);
          wrapper.append(angular.element('.balloon-content-wrapper').html());

          var marker = new ymaps.Placemark([item.location[1], item.location[0]], {
              balloonContentBody: createBalloonContent(item)
            },
            {
              iconImageHref: controllerScope.pinIcons[item.tags[0]],
              iconImageSize: [46, 68],
              iconImageOffset: [-23, -68],
              draggable: false,
              hideIconOnBalloonOpen: false
            }
          );
          markersMap[item.id] = marker;
          controllerScope.DGisMap.markers.add(marker, item.mainCategory);
        },

        showMarkersForCategory: function (selectedCategory) {
          console.log('showMarkersForCategory');
//        var defaultGroup = controllerScope.DGisMap.markers.getGroup(defaultGroupName);
          if (UtilsService.isNotEmpty(selectedCategory)) {
            controllerScope.DGisMap.markers.hideAllGroups();
            if (UtilsService.isNotBlank(selectedCategory.name)) {
              controllerScope.DGisMap.markers.showGroup(selectedCategory.name);
            }
//          defaultGroup.show();
          } else {
            controllerScope.DGisMap.markers.showAllGroups();
          }
        },

        showBalloonForItem: function (item) {
          if (UtilsService.isNotEmpty(item)) {
            var marker = markersMap[item.id];
            controllerScope.DGisMap.setCenter(marker.geometry.getCoordinates());
            marker.balloon.open();
          }
        },

        hideBalloonForItem: function (item) {
          if (UtilsService.isNotEmpty(item)) {
            var marker = markersMap[item.id];
            controllerScope.DGisMap.setCenter(marker.geometry.getCoordinates());
            marker.balloon.close();
          }
        },

        hideAllBalloons: function () {
          var all = controllerScope.DGisMap.markers;
          for (var i = 0; i < all.length; i++) {
            if (UtilsService.isNotEmpty(all[i]) && UtilsService.isFunction(all[i].balloon.close)) {
              all[i].balloon.close();
            }
          }
        },

        getAddress: function (mapObj, lng, lat, callback) {
          var unknownWhere = 'Неизвестное место на карте';
          ymaps.geocode([lng, lat])
            .then(function (res) {
              var result = res.geoObjects.get(0);
              controllerScope.hideBusy();

              var showBalloon = false;
              if (UtilsService.isNotEmpty(result) && UtilsService.isNotBlank(result.properties.get('name'))) {
                controllerScope.laf.where = result.properties.get('name');
                showBalloon = true;
              } else {
                controllerScope.laf.where = unknownWhere;
              }
              if (UtilsService.isNotEmpty(controllerScope.middleStateItem)) {
                controllerScope.middleStateItem.where = controllerScope.laf.where;
                console.log('where: ' + controllerScope.middleStateItem.where);
              }
              if (!controllerScope.$$phase) {
                controllerScope.$apply();
              }

              if (showBalloon && UtilsService.isFunction(callback)) {
                callback();
              }
            }, function () {
              controllerScope.hideBusy();
              controllerScope.laf.where = unknownWhere;
              if (UtilsService.isNotEmpty(controllerScope.middleStateItem)) {
                controllerScope.middleStateItem.where = controllerScope.laf.where;
              }
              if (!controllerScope.$$phase) {
                controllerScope.$apply();
              }
            }
          );
        },

        getPointByAddress: function (address, callback) {
          console.log('address: ' + address);
          var self = this;
          ymaps.geocode(address, {
            results: 1
          }).then(function (result) {
            var geocoderObject = result.geoObjects.get(0);

            var geoPoint = geocoderObject.geometry.getCoordinates();
            if (UtilsService.isNotEmpty(controllerScope.activeBallon)) {
              controllerScope.activeBallon.hide();
            }
            controllerScope.DGisMap.setCenter(geoPoint);
            //перемещение маркера в новое место
            controllerScope.currentLocationMarker.geometry.setCoordinates(geoPoint);

            controllerScope.laf.location = self.getLocationObject();
            if (UtilsService.isNotEmpty(controllerScope.middleStateItem)) {
              controllerScope.middleStateItem.location = controllerScope.laf.location;
            }
            if (!controllerScope.$$phase) {
              controllerScope.$apply();
            }
          }, function (message) {
            console.log('Unable to get point: ' + message);
            controllerScope.hideBusy();
            controllerScope.laf.location = self.getLocationObject();
            if (UtilsService.isNotEmpty(controllerScope.middleStateItem)) {
              controllerScope.middleStateItem.location = controllerScope.laf.location;
            }
            if (!controllerScope.$$phase) {
              controllerScope.$apply();
            }
          });
        },

        activateMiddleStatePanel: function (item, isCreation) {
          var middleStatePanel = angular.element('.marker-edit-mode');
          var middleStatePanelLeft = angular.element('body').outerWidth(true) / 2 -
            middleStatePanel.outerWidth(true) / 2;
          middleStatePanel.css('left', middleStatePanelLeft + 'px');
          controllerScope.middleStateItem = item;
          controllerScope.mapFullScreen = true;
          controllerScope.middleStateItem_loc = item.location;
          controllerScope.middleStateItem_where = item.where;
          controllerScope.isItemCreation = isCreation;
        },

        deactivateMiddleStatePanel: function () {
          controllerScope.mapFullScreen = false;
          controllerScope.middleStateItem = null;
          controllerScope.middleStateItem_loc = null;
          controllerScope.middleStateItem_where = null;
          controllerScope.isItemCreation = null;
        },

        whereTypeahead: function (val) {
          return $http({method: 'POST', url: 'https://dadata.ru/api/v1/suggest/address', headers: {
            'Authorization': 'Token 8e5f640a855aaa1afb4f2ae651528faacbeee155',
            'Content-Type': 'application/json'
          }, data: {
            query: controllerScope.cities[0].name + ' ' + val
          }}).then(function (data) {
            var addresses = [];
            angular.forEach(data.data.suggestions, function (item) {
              var address = '';
//            if (item.data.city_type) {
//              address = address.concat(item.data.city_type).concat(' ');
//            }
              if (item.data.city) {
                address = address.concat(item.data.city);
              }
//            if (item.data.street_type) {
//              address = address.concat(item.data.street_type).concat(' ');
//            }
              if (item.data.street) {
                if (address.length > 0) {
                  address = address.concat(', ');
                }
                address = address.concat(item.data.street);
              }
//            if (item.data.house_type) {
//              address = address.concat(item.data.house_type).concat(' ');
//            }
              if (item.data.house) {
                if (address.length > 0) {
                  address = address.concat(', ');
                }
                address = address.concat(item.data.house);
              }
              if (UtilsService.isNotBlank(address.trim())) {
                addresses.push(address);
              }
            });

            return addresses
          });
        }
      }
    }
  });