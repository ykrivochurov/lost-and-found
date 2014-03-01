function ComplaintModalController($scope, $modalInstance, $timeout, UtilsService, ComplaintService, item) {

  $scope.reasons = [
    'В объявлении обман',
    'Фотографии не соответствуют объявлению',
    'Неверные контактные данные',
    'Это просто глупое баловство',
    'Мошенники',
    'Объявление дублируется',
    'Другая причина'
  ];
  $scope.complaint = {
    reason: $scope.reasons[0]
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