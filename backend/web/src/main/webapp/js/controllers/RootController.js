function RootController($scope) {
  function safeApply() {
    if (!$scope.$$phase) {
      $scope.$apply();
    }
  }
}