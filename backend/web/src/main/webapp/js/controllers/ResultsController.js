function ResultsController($scope, $routeParams, $location, QuestionService) {
    $scope.voteState = {
        "YES": "yes",
        "NO": "no",
        "ABSTAIN": "abstain"
    };

    $scope.yesUsers = [];
    $scope.noUsers = [];
    $scope.abstainUsers = [];
    $scope.yesVotes = 0;
    $scope.noVotes = 0;
    $scope.abstainVotes = 0;

    $scope.initVotesAndUsers = function (votes) {
        for (var i = 0; i < votes.length; i++) {
            if (votes[i].voteState == $scope.voteState.YES) {
                $scope.yesVotes++;
                $scope.yesUsers.push(votes[i].user);
            } else if (votes[i].voteState == $scope.voteState.NO) {
                $scope.noVotes++;
                $scope.noUsers.push(votes[i].user);
            } else if (votes[i].voteState == $scope.voteState.ABSTAIN) {
                $scope.abstainVotes++;
                $scope.abstainUsers.push(votes[i].user);
            }
        }
    };

    $scope.init = function () {
        $scope.meetingId = $routeParams.meetingId;
        $scope.questionNumber = $routeParams.questionNumber;

        QuestionService.crud.byMeetingAndNumber({meetingId: $scope.meetingId}, {questNumber: $scope.questionNumber},
            function (question) {
                $scope.question = question;
                $scope.initVotesAndUsers(question.votes);
            }, function (data) {
                console.log(data.data.message);
                $location.path("/home");
            })
    };

    $scope.init();

    $scope.goToPrevQuestion = function () {
        if ($scope.question.number > 1) {
            $location.path("/meeting/"+ $scope.meetingId +"/question-number/" + (--$scope.questionNumber));
        }
    };

    $scope.goToNextQuestion = function () {
        if ($scope.question.number < $scope.question.meeting.questionsCount) {
            $location.path("/meeting/"+ $scope.meetingId +"/question-number/" + (++$scope.questionNumber));
        }
    };

    $scope.goToMeeting = function(meetingId) {
        $location.path('/meeting/' + meetingId);
    };
}