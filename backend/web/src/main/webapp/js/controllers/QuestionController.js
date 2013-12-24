function QuestionController($scope, $routeParams, $dialog, $document, $location,
                            QuestionService, MeetingService, CommentService, DocumentService) {
    $scope.questionId = $routeParams.questionId;
    $scope.question = QuestionService.crud.get({questionId: $scope.questionId}, function() {
        MeetingService.utils.addDateTimeToScope($scope, $scope.question.meeting);
    });

    $scope.newComment = {
        text: "",
        question: {
            id: $scope.questionId
        }
    };

    $scope.submitComment = function () {
        if ($scope.newComment.text.length > 0) {
            CommentService.crud.create($scope.newComment, function (comment) {
                $scope.question.comments.push(comment);
                $scope.newComment.text = "";
            })
        }
    };

    $scope.scrollCommentsAreaDown = function () {
        var f = $document.find('#commentsArea');
        f.scrollTop(f[0].scrollHeight);
    };

    $scope.addDocuments = function () {
        var modalDialogOptions = {
            backdrop: true,
            backdropFade: true,
            dialogFade: true,
            templateUrl: 'meeting-create-modal-content',
            controller: 'MeetingFormModalDialogController',
            resolve: {
                question: function () {
                    return angular.copy($scope.question);
                }
            }
        };
        var modalDialog = $dialog.dialog(modalDialogOptions);
        modalDialog.open().then(function (resources) {
            if (resources) {
                $scope.question.resources = resources;
            }
        });
    };

    $scope.removeDocument = function (document) {
        DocumentService.crud.delete({documentId: document.id}, function() {
            var documentIndex = $scope.question.resources.indexOf(document);
            $scope.question.resources.splice(documentIndex, 1);
        }, function(error) {
            console.log("Can't remove document: " + document.name + ". " + JSON.stringify(error));
        });
    };

    $scope.goBack = function () {
        $location.path("/meeting/" + $scope.question.meeting.id);
    };

}