function HomeController($scope, $filter, $location, $anchorScroll, $dialog, $templateCache, $compile, $timeout, MeetingService, HomeService, UserService, calendarClassPrefix) {
    $scope.meetingForPopover = {id: null, status: null};
    $scope.showSearchResults = true;
    $scope.meetingFilter = {};
    HomeService.crud.get(function (dto) {
        $scope.dto = dto;
        $scope.calendarDto.meetings = $scope.dto.filteredMeetings;
        $scope.initCompanies();
        // todo нужно сделать склонение и множественную форму лабелки
    });

    $scope.isSecretary = false;
    UserService.crud.current(function (user) {
        if (user.roles.indexOf('ROLE_SECRETARY') != -1) {
            $scope.isSecretary = true;
        }
    });

    $scope.initCompanies = function () {
        $scope.companies = [];
        for (var i = 0; i < $scope.dto.companies.length; i++) {
            $scope.companies.push({
                id: $scope.dto.companies[i].id,
                name: $scope.dto.companies[i].name,
                checked: false});
        }
    };

    // todo перенести json в файле на сервер, или в контроллер
    $scope.statuses = [
//        { name: 'ALL', checked: false, label: 'home.meeting.status_all' },
        { name: 'PUBLISHED', checked: false, label: 'home.meeting.status_published', hint: 'home.meeting.status_published_hint',
            label_single: 'meeting.status_published', style: 'publishedMeeting' },
        { name: 'PAST', checked: false, label: 'home.meeting.status_past', hint: 'home.meeting.status_past_hint',
            label_single: 'meeting.status_past', style: 'pastMeeting' },
        { name: 'FINISHED', checked: false, label: 'home.meeting.status_finished', hint: 'home.meeting.status_finished_hint',
            label_single: 'meeting.status_finished', style: 'finishedMeeting' },
        { name: 'DRAFT', checked: false, label: 'home.meeting.status_draft', hint: 'home.meeting.status_draft_hint',
            label_single: 'meeting.status_draft', style: 'draftMeeting' },
        { name: 'VOTING_OPENED', checked: false, label: 'home.meeting.status_voting_opened', hint: 'home.meeting.status_voting_opened_hint',
            label_single: 'meeting.status_voting_opened', style: 'voting_openedMeeting' }
    ];

    $scope.getStatusLabel = function (status) {
        if (status !== null) {
            for (var i = 0; i < $scope.statuses.length; i++) {
                if ($scope.statuses[i].name == status) {
                    return $scope.statuses[i].label_single;
                }
            }
        }
        return '';
    };

    $scope.applyFilter = function () {
        $scope.meetingFilter.statuses = $filter('objectsToStrings')(
            $filter('filter')($scope.statuses, {checked: true}),
            ['name']
        );
        $scope.meetingFilter.companies = $filter('dropProps')(
            $filter('filter')($scope.companies, {checked: true}),
            ['id']
        );
        MeetingService.crud.getFiltered($scope.meetingFilter, function (meetings) {
            $scope.dto.filteredMeetings = meetings;
            $scope.calendarDto.meetings = $scope.dto.filteredMeetings;
        })
    };

    $scope.createMeeting = function () {
        var modalDialogOptions = {
            backdrop: true,
            backdropFade: true,
            dialogFade: true,
            templateUrl: 'home-modal-content',
            controller: 'HomeModalDialogController',
            resolve: {
                companies: function () {
                    return angular.copy($scope.dto.companies);
                }
            }
        };
        var modalDialog = $dialog.dialog(modalDialogOptions);
        modalDialog.open().then(function (companyId) {
            if (companyId) {
                $location.path('/meeting').search('companyId=' + companyId);
            }
        });
    };

    $scope.calendarDto = {
        meetings: null,
        selectedDate: null
    };

    $scope.$watch('calendarDto.selectedDate', function (date) {
        if (date !== null) {
            var index = 0;
            var meeting = null;
            while (index < $scope.dto.filteredMeetings.length && meeting === null) {
                var meetingDate = new Date($scope.dto.filteredMeetings[index].dateAndTime);
                if (date.getFullYear() == meetingDate.getFullYear() && date.getMonth() == meetingDate.getMonth()
                    && date.getDate() == meetingDate.getDate()) {
                    meeting = $scope.dto.filteredMeetings[index];
                }
                index++;
            }
            if (meeting !== null) {
                $scope.watchMeeting(meeting);
            }
            $scope.calendarDto.selectedDate = null;
        }
    }, true);

    $scope.watchMeeting = function (meeting) {
        if ($scope.meetingForPopover !== null && $scope.meetingForPopover.id !== null) {
            //destroy prev popover
            var prevPopoverSelector = '.' + calendarClassPrefix + $scope.meetingForPopover.id + ' a';
            $(prevPopoverSelector).popover('destroy');
        }
        if ($scope.meetingForPopover !== meeting) {
            $scope.meetingForPopover = meeting;
            $timeout(function () {
                var element = angular.element('#popoverContent');
                var popoverSelector = '.' + calendarClassPrefix + meeting.id + ' a';
                $(popoverSelector).popover({
                    title: $filter('date')(meeting.dateAndTime, "dd MMMM yyyy  '['HH:mm']'"),
                    html: true,
                    content: element.html(),
                    trigger: 'manual'
                });
                $(popoverSelector).popover('show');
                $(popoverSelector).click(function (e) {
                    if ($(this).hasClass('go-to-meeting')) {
                        $scope.goToMeeting($scope.meetingForPopover);
                        e.preventDefault();
                    }
                });
            });
        } else {
            $scope.meetingForPopover = null;
        }
    };

    $scope.goToMeeting = function (meeting) {
        console.log('go to meeting ' + meeting.id);
        $location.path('/meeting/' + meeting.id);
    };

    $scope.showQuestionDetails = function (question) {
        $location.path('/question/' + question.id);
    };

    $scope.goToTop = function () {
        $location.hash('topElement');
        $anchorScroll();
    };
}

function HomeModalDialogController($scope, dialog, companies) {
    $scope.companies = companies;
    $scope.close = function (companyId) {
        dialog.close(companyId);
    };
}

function CalendarModalDialogController($scope, dialog, meeting) {
    $scope.meeting = meeting;
    $scope.close = function (meetingId) {
        dialog.close(meetingId);
    };
}