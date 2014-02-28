function ComplaintModalController($scope, $modalInstance, $timeout, UtilsService, ComplaintService, item) {

  $scope.complaint = {
    reason: 0
  };
  $scope.item = item;
  $scope.save = function () {
    if (UtilsService.isNotEmpty($scope.item)) {
      $scope.complaint.itemId = $scope.item.id;
      ComplaintService.crud.create($scope.complaint, function (complaint) {
        $modalInstance.dismiss('cancel');
      });
    }
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

}