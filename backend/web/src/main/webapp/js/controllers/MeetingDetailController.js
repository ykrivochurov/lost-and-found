function MeetingDetailController($scope, $routeParams, $location, MeetingService, UserService) {
    $scope.meetingId = $routeParams.meetingId;
    $scope.statuses = [
        { status: 'DRAFT', label: 'meeting.status_draft' },
        { status: 'PUBLISHED', label: 'meeting.status_published' },
        { status: 'VOTING_OPENED', label: 'meeting.status_voting_opened' },
        { status: 'PAST', label: 'meeting.status_past' },
        { status: 'FINISHED', label: 'meeting.status_finished' }
    ];
    Object.freeze($scope.statuses);

    $scope.meeting = MeetingService.crud.get({meetingId: $scope.meetingId}, function(meeting) {
        MeetingService.utils.addDateTimeToScope($scope, $scope.meeting);
        for (var i = 0; i < $scope.statuses.length; i++) {
            if (meeting.status == $scope.statuses[i].status) {
                $scope.selectedStatus = $scope.statuses[i];
            }
        }
    });

    $scope.changeStatus = function() {
        $scope.meeting.status = $scope.selectedStatus.status;
        $scope.meeting.$save(function () {
            console.log('Meeting status changed');
        });
    };

    $scope.isSecretary = false;
    UserService.crud.current(function (user) {
        if (user.roles.indexOf("ROLE_SECRETARY") != -1) {
            $scope.isSecretary = true;
        }
    });

    $scope.goBack = function () {
        $location.path("/home");
    };

    $scope.voteState = {
        "YES": "yes",
        "NO": "no",
        "ABSTAIN": "abstain"
    };

    $scope.getVotes = function (votes, votestate) {
        var count = 0;
        for (var i = 0; i < votes.length; i++) {
            if (votes[i].voteState == votestate) {
                count++;
            }
        }
        return count;
    };

    $scope.showMeetingResultsForQuestion = function (questionNumber) {
        $location.path("/meeting/"+ $scope.meetingId +"/question-number/" + questionNumber);
    };

    $scope.personalPresenceType = function() {
        if ($scope.meeting.personalPresence) {
            return 'meeting.personalPresenceOnline';
        }
        return 'meeting.personalPresenceOffline';
    };

    $scope.showQuestionDetails = function (question) {
        $location.path("/question/" + question.id);
    };

    $scope.editMeeting = function () {
        $location.path('/meeting').search('meetingId=' + $scope.meetingId);
    };
}