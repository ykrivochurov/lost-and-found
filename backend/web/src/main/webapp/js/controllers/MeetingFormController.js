function MeetingFormController($scope, $location, $filter, $dialog,
                               MeetingService, CommitteeService, QuestionService, DocumentService) {
//    $scope.setPageName("meeting.title");
    $scope.date;
    $scope.time;
    $scope.meetingNotValid = false;
    $scope.questionNotValid = false;
    $scope.newQuestion = null;

    $scope.initEdition = function () {
        var companyId = $location.search()['companyId'];
        var meetingId = $location.search()['meetingId'];
        if (companyId === undefined && meetingId === undefined) {
            $location.path("/home");
        }
        if (companyId !== undefined)
        {
            $scope.company = {id: companyId};
            $scope.meeting = {
                company: $scope.company
            };
            $scope.meeting = MeetingService.crud.create($scope.meeting, function (data) {
                if ($scope.meeting.dateAndTime) {
                    var date = new Date($scope.meeting.dateAndTime);
                    $scope.date = date;
                    $scope.time = date.getHours() + ":" + date.getMinutes();
                }
                $scope.meeting.status = "DRAFT";
                $scope.meeting.personalPresence = true;
            }, function (data, status, headers, config) {
                console.log('Failed to load temp meeting');
            });
            $scope.committees = CommitteeService.crud.byCompany({companyId: $scope.company.id});
        } else {
            $scope.meeting = MeetingService.crud.getById({meetingId: meetingId}, function (data) {
                if ($scope.meeting.dateAndTime) {
                    var date = new Date($scope.meeting.dateAndTime);
                    $scope.date = date;
                    $scope.time = date.getHours() + ":" + date.getMinutes();
                }
                $scope.company = $scope.meeting.company;
                $scope.committees = CommitteeService.crud.byCompany({companyId: $scope.company.id});
            }, function (data, status, headers, config) {
                console.log('Failed to load existing meeting');
            });
        }
    };

    $scope.initEdition();

//   Work with meeting

    $scope.save = function () {
        if ($scope.meetingForm.$valid && $scope.meeting.committees.length > 0) {
            $scope.meeting.dateAndTime = $filter("addTimeToDate")($scope.date, $scope.time).getTime();
            $scope.meeting.$save({meetingId: null}, function (data) {
                console.log($scope.meeting);
                $location.path("/home").search({});
            }, function (data, status, headers, config) {
                console.log('Failed to load draft meeting');
            });
        } else {
            $scope.meetingNotValid = true;
        }
    };

//    Work with committee

    $scope.selectCommittee = function (committee) {
        $scope.meeting.committees = $filter('filter')($scope.committees, {checked: true});
        console.log($scope.meeting);
    };
    $scope.isCommitteeChecked = function (committee) {
        if ($scope.meeting.committees) {
            for (var i = 0; i < $scope.meeting.committees.length; i++) {
                if (committee.id == $scope.meeting.committees[i].id) {
                    return committee.checked = true;
                }
            }
        }
        return committee.checked = false;
    };

//    Work with question
    $scope.createQuestion = function () {
        $scope.newQuestion = {
            question: "",
            solution: "",
            committee: "",
            performerUser: ""
        };
        $scope.errors = {};
        console.log("Create new question");
    };
    $scope.removeNewQuestion = function () {
        $scope.newQuestion = null;
        console.log("remove new question");
        $scope.questionNotValid = false;
    };
    $scope.saveQuestion = function () {
        if ($scope.questionForm.$valid) {
            $scope.newQuestion.meeting = $scope.meeting;
            $scope.newQuestion.number = $scope.meeting.questions.length + 1;
            QuestionService.crud.create($scope.newQuestion,
                function (question) {
                    console.log(question);
                    $scope.meeting.questions.push(question);
                    console.log($scope.meeting.questions);
                    console.log($scope.newQuestion);
                    $scope.newQuestion = null;
                    $scope.questionNotValid = false;
                },
                function (response) {
                    console.log("Can't create question")
                });
        } else {
            $scope.questionNotValid = true;
        }
    };

//    Working with documents
    $scope.addDocuments = function (question) {
        var modalDialogOptions = {
            backdrop: true,
            backdropFade: true,
            dialogFade: true,
            templateUrl: 'meeting-create-modal-content',
            controller: 'MeetingFormModalDialogController',
            resolve: {
                question: function () {
                    return angular.copy(question);
                }
            }
        };
        var modalDialog = $dialog.dialog(modalDialogOptions);
        modalDialog.open().then(function (resources) {
            if (resources) {
                question.resources = resources;
            }
        });
    };

    $scope.removeDocument = function (question, document) {
        DocumentService.crud.delete({documentId: document.id}, function() {
            var documentIndex = question.resources.indexOf(document);
            question.resources.splice(documentIndex, 1);
        }, function(error) {
            console.log("Can't remove document: " + document.name + ". " + JSON.stringify(error));
        });
    };

    $scope.goBack = function () {
        $location.path("/home").search({});
    };

    $scope.changedQuestion = {
        startIndex: null,
        newIndex: null
    };

    $scope.updateQuestions = function () {
        QuestionService.crud.byMeeting({meetingId: $scope.meeting.id}, function (questions) {
            for (var i = 0; i < $scope.meeting.questions.length; i++) {
                var toChangeId = $scope.meeting.questions[i].id;
                for (var j = 0; j < questions.length; j++) {
                    if (questions[j].id == toChangeId) {
                        $scope.meeting.questions[i].number = questions[j].number;
                        break;
                    }
                }
            }
            $scope.meeting.questions.sort(function (q1, q2) {
                return q1.number - q2.number;
            });
            console.log("reload questions numbers");
        });
    };

    $scope.$watch("changedQuestion", function(question) {
        if (question.startIndex !== null && question.newIndex !== null && question.startIndex != question.newIndex) {
            console.log("Change number from " + (question.startIndex + 1) + " to " + (question.newIndex + 1));
            var questionToChange = $scope.meeting.questions[question.startIndex];
            questionToChange.number = question.newIndex + 1;
            QuestionService.crud.save(questionToChange, function (quest) {
                console.log("change number");
                $scope.updateQuestions();
            });
            $scope.changedQuestion.startIndex = null;
            $scope.changedQuestion.newIndex = null;
        }
    },true);
}

function MeetingFormModalDialogController($scope, dialog, question, DocumentService, QuestionService) {
    $scope.documents = {
        question: question,
        files: [
            {}
        ]
    };

    $scope.addDocumentInput = function () {
        $scope.documents.files.push({});
    };

    $scope.removeDocumentInput = function (file) {
        $scope.documents.files.pop(file);
    };

    $scope.documentsModalClose = function (resources) {
        dialog.close(resources);
    };

    $scope.uploadDocuments = function () {
        // workaround because model doesn't work with type="file"
        var documentInputs = $('input[name="documents"]');
        var formData = {
            id: $scope.documents.question.id
        };
        $.each(documentInputs, function (index, input) {
            formData['documents' + index] = input.files[0];
        });

        DocumentService.upload(formData, function (data, status) {
            console.log("good");
            QuestionService.crud.get(
                {questionId: $scope.documents.question.id},
                function (question) {
                    $scope.documentsModalClose(question.resources);
                });
        }, function (data, status) {
            console.log("bad");
        });
    };
}