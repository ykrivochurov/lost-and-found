angular.module('laf').
  factory('YMapService', function ($q, $timeout, $sce, $http, UtilsService, GeoLocationService, ItemsService) {
    moment.lang('ru');
    var defaultGroupName = 'default_markers';
    var controllerScope;
    var markersMap = {};

    function createBalloonContent(item) {
      var container = angular.element('<div></div>');
      container.append();

      var wrapper = angular.element('<a href="' + generateUrl(item) + '" class="clear-link"></a>');
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

          controllerScope.DGisMap.controls.add(
            new ymaps.control.ZoomControl()
          );
          controllerScope.DGisMap.markers = {
            markers: {},
            add: function (marker, groupName) {
              var markersForGroup = this.markers[groupName];
              if (markersForGroup == null) {
                markersForGroup = new ymaps.GeoObjectCollection({}, {});
                controllerScope.DGisMap.geoObjects.add(markersForGroup);
              }
              markersForGroup.add(marker);
              this.markers[groupName] = markersForGroup;
            },
            showGroup: function (groupName) {
              controllerScope.DGisMap.geoObjects.add(this.markers[groupName]);
            },
            getGroup: function (groupName) {
              return this.markers[groupName];
            },
            removeGroup: function (groupName) {
              controllerScope.DGisMap.geoObjects.remove(this.markers[groupName]);
            },
            showAllGroups: function () {
              for (var prop in this.markers) {
                if (this.markers.hasOwnProperty(prop)) {
                  var group = this.markers[prop];
                  controllerScope.DGisMap.geoObjects.add(group);
                }
              }
            },
            removeAllGroups: function () {
              for (var prop in this.markers) {
                if (this.markers.hasOwnProperty(prop)) {
                  var group = this.markers[prop];
                  controllerScope.DGisMap.geoObjects.remove(group);
                }
              }
            }
          };
          //выкл стандартных балунов
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
            var markersFromGroup = controllerScope.DGisMap.markers[groupNames[i].name];
            if (UtilsService.isNotEmptyArray(markersFromGroup)) {
              controllerScope.DGisMap.geoObjects.remove(markersFromGroup);
            }
          }
        }
      },

      removeMarker: function (item) {
        this.hideBalloonForItem(item);
//        controllerScope.DGisMap.markers.remove(markersMap[item.id]);

        var markersFromGroup = controllerScope.DGisMap.markers[groupNames[i].name];
        if (UtilsService.isNotEmptyArray(markersFromGroup)) {
          markersFromGroup.remove(markersMap[item.id]);
        }
      },

      moveMarker: function (item) {
        this.hideBalloonForItem(item);
        var marker = markersMap[item.id];
        if (UtilsService.isNotEmpty(marker)) {
          marker.geometry.setCoordinates(item.location[1], item.location[0]);
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

        var container = angular.element('<div></div>');
        container.append();

        var wrapper = angular.element('<a href="' + generateUrl(item) + '" class="clear-link"></a>');
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
          controllerScope.DGisMap.markers.removeAllGroups();
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

      //todo !!
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
    }
  })
