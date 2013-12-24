function CompaniesController($scope, $location, CompanyService, CommitteeService, PositionService, UserService) {

    $scope.status = {
        "ACTIVE": "ACTIVE",
        "ARCHIVE": "ARCHIVE"
    };

    Object.freeze($scope.status);

    $scope.currentPageCount = 5;
    $scope.maxPageShow = 5;

    $scope.loadCompanies = function () {
        $scope.companies = CompanyService.crud.getAll();
    };

    $scope.loadCommittees = function () {
        $scope.committees = CommitteeService.crud.byCompany({companyId: $scope.selectedCompanyId});
    };

    $scope.loadCompanies();

    //************************Committee-users**************************

    $scope.deleteUserFromCommittee = function (userId) {
        CommitteeService.crud.deleteUser({committeeId: $scope.selectedCommitteeId}, {userId: userId},
            function () {
                $scope.loadCommitteePositions();
            })
    };

    $scope.addUserToCommittee = function () {
        $scope.newCommitteeUser = {
            id: null
        };
        UserService.crud.byCommittee({compOrCommId: $scope.selectedCommitteeId}, function (users) {
            $scope.possibleUsers = users;
        })
    };

    $scope.confirmUserAdditionToCommittee = function () {
        if ($scope.newCommitteeUser.id != null) {
            CommitteeService.crud.addUser({committeeId: $scope.selectedCommitteeId},
                {userId: $scope.newCommitteeUser.id},
                function () {
                    $scope.cancelUserAdditionToCommittee();
                    $scope.loadCommitteePositions();
                })
        }
    };

    $scope.cancelUserAdditionToCommittee = function () {
        $scope.newCommitteeUser = null;
    };

    //************************Company position*************************

    $scope.deleteUserFromCompany = function (userId) {
        PositionService.crud.delete({companyId: $scope.selectedCompanyId, commOrUserId: userId}, function () {
            $scope.loadCompanyPositions();
        })
    };

    $scope.addUserToCompany = function () {
        $scope.newCompanyPosition = {
            userCompany: {
                company: {
                    id: $scope.selectedCompanyId
                },
                user: {
                    id: null
                }
            }
        };
        UserService.crud.byCompany({compOrCommId: $scope.selectedCompanyId}, function (users) {
            $scope.possibleUsers = users;
        })
    };

    $scope.confirmUserAdditionToCompany = function () {
        if ($scope.newCompanyPosition.userCompany.user.id != null && $scope.newCompanyPosition.position.length > 0) {
            PositionService.crud.create($scope.newCompanyPosition, function (position) {
                $scope.cancelUserAdditionToCompany();
                $scope.loadCompanyPositions();
                console.log('Adding position to company' + position);
            })
        }
    };

    $scope.cancelUserAdditionToCompany = function () {
        $scope.newCompanyPosition = null;
    };

    //************************Pagination*************************

    $scope.setPage = function () {
        if ($scope.selectedCommitteeId == null) {
            $scope.loadCompanyPositions();
        } else {
            $scope.loadCommitteePositions();
        }
    };

    $scope.$watch('currentPage', $scope.setPage);

    //************************Existing positions*************************

    $scope.loadCompanyPositions = function () {
        PositionService.crud.getByCompany({companyId: $scope.selectedCompanyId},
            {pageNumber: $scope.currentPage - 1, pageCount: $scope.currentPageCount}, function (positions) {
                $scope.positions = positions.content;
                $scope.noOfPages = positions.totalPages;
            });
    };

    $scope.loadCommitteePositions = function () {
        PositionService.crud.getByCommittee({commOrUserId: $scope.selectedCommitteeId},
            {pageNumber: $scope.currentPage - 1, pageCount: $scope.currentPageCount},
            function (positions) {
                $scope.positions = positions.content;
                $scope.noOfPages = positions.totalPages;
            });
    };

    //************************Select Company*************************

    $scope.selectedCompanyId = null;

    $scope.selectCompany = function (companyId) {
        $scope.selectedCompanyId = companyId;
        $scope.editedCompanyId = null;
        $scope.selectedCommitteeId = null;
        $scope.cancelUserAdditionToCommittee();
        $scope.cancelUserAdditionToCompany();
        $scope.loadCommittees();
        $scope.currentPage = 1;
        $scope.loadCompanyPositions();
    };

    //************************Create New Company*********************

    $scope.createCompany = function () {
        $scope.cancelCompanyEdition();
        $scope.newCompany = {
            name: "",
            "status": $scope.status.ACTIVE
        }
    };

    $scope.saveCompany = function () {
        if ($scope.newCompany.name.length > 0) {
            CompanyService.crud.create($scope.newCompany, function (company) {
                $scope.newCompany = null;
                $scope.companies.push(company);
                $scope.selectCompany(company.id);
                console.log("Create new company");
            })
        }
    };

    $scope.cancelCompanyCreation = function () {
        $scope.newCompany = null;
    };

    //************************Edit Company***************************

    $scope.editCompany = function (companyId) {
        $scope.editedCompanyId = companyId;
        CompanyService.crud.getOne({companyId: companyId}, function (company) {
            $scope.editedCompany = company;
            $scope.canDeleteCompany = company.canDelete;
            $scope.canArchiveCompany = company.canArchive;
        });
        $scope.loadCompanies();
    };

    $scope.saveCompanyName = function (oldName) {
        if ($scope.editedCompany.name.length > 0 && $scope.editedCompany.name != oldName) {
            CompanyService.crud.update({companyId: null}, $scope.editedCompany, function (company) {
                $scope.cancelCompanyEdition();
                console.log("Change company name");
            });
        }
    };

    $scope.removeCompany = function () {
        if ($scope.canDeleteCompany) {
            CompanyService.crud.delete({companyId: $scope.editedCompanyId}, function() {
                $scope.cancelCompanyEdition();
                $scope.selectedCompanyId = null;
                console.log("Delete company");
            });
        }
    };

    $scope.changeCompanyStatus = function(status) {
        if ($scope.canArchiveCompany) {
            CompanyService.crud.updateStatus({companyId: $scope.editedCompanyId}, {newStatus: status}, function() {
                $scope.editedCompany.status = status;
                console.log("Change company status");
            });
        }
    };

    $scope.cancelCompanyEdition = function () {
        $scope.editedCompany = null;
        $scope.editedCompanyId = null;
        $scope.canDeleteCompany = null;
        $scope.canArchiveCompany = null;
        $scope.loadCompanies();
    };

    //************************Select Committee*************************

    $scope.selectedCommitteeId = null;

    $scope.selectCommittee = function (committeeId) {
        $scope.selectedCommitteeId = committeeId;
        $scope.editedCommitteeId = null;
        $scope.cancelUserAdditionToCommittee();
        $scope.cancelUserAdditionToCompany();
        $scope.currentPage = 1;
        $scope.loadCommitteePositions();
    };

    //************************Create New Committee*********************

    $scope.addCommittee = function () {
        $scope.newCommittee = {
            name: "",
            company: {
                "id": $scope.selectedCompanyId
            },
            "status": $scope.status.ACTIVE
        }
    };

    $scope.saveCommittee = function () {
        if ($scope.newCommittee.name.length > 0) {
            CommitteeService.crud.create($scope.newCommittee, function (committee) {
                $scope.newCommittee = null;
                $scope.committees.push(committee);
                $scope.selectCommittee(committee.id);
                console.log("Create new committee");
            })
        }
    };

    $scope.cancelCommitteeCreation = function () {
        $scope.newCommittee = null;
    };

    //************************Edit Committee***************************

    $scope.editCommittee = function (committeeId) {
        $scope.editedCommitteeId = committeeId;
        CommitteeService.crud.getOne({committeeId: committeeId}, function (committee) {
            $scope.editedCommittee = committee;
            $scope.canDeleteCommittee = committee.canDelete;
            $scope.canArchiveCommittee = committee.canArchive;
        });
        $scope.loadCommittees();
    };

    $scope.saveCommitteeName = function (oldName) {
        if ($scope.editedCommittee.name.length > 0 && $scope.editedCommittee.name != oldName) {
            CommitteeService.crud.update({committeeId: null}, $scope.editedCommittee, function (committee) {
                $scope.cancelCommitteeEdition();
                console.log("Change committee name");
            });
        }
    };

    $scope.removeCommittee = function () {
        if ($scope.canDeleteCommittee) {
            CommitteeService.crud.delete({committeeId: $scope.editedCommitteeId}, function() {
                $scope.cancelCommitteeEdition();
                $scope.selectedCommitteeId = null;
                console.log("Delete committee");
            });
        }
    };

    $scope.changeCommitteeStatus = function(status) {
        if ($scope.canArchiveCommittee) {
            CommitteeService.crud.updateStatus({committeeId: $scope.editedCommitteeId}, {newStatus: status},
                function() {
                    $scope.editedCommittee.status = status;
                    console.log("Change committee status");
                });
        }
    };

    $scope.cancelCommitteeEdition = function () {
        $scope.editedCommittee = null;
        $scope.editedCommitteeId = null;
        $scope.canDeleteCommittee = null;
        $scope.canArchiveCommittee = null;
        $scope.loadCommittees();
    };

    //************************Service***************************

    $scope.goBack = function () {
        $location.path("/home");
    };
}
