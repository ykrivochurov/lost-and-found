function RootController($scope) {
  function safeApply() {
    $scope.isLocaleLoaded = true;
    if (!$scope.$$phase) {
      $scope.$apply();
    }
  }
}