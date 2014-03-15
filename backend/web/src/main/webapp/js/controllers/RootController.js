function RootController($scope) {
  function safeApply() {
    if (!$scope.$$phase) {
      $scope.$apply();
    }
  }

  $scope.bodyClick = function() {
    angular.element('.settings-block').hide();
  }

}