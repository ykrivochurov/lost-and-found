function ImageViewModalController($scope, $modalInstance, item) {
  $scope.item = item;

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
}