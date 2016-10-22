(function () {
    angular
        .module('tasker0App')
        .controller('ProjectDetailsModalCtrl', ProjectDetailsModalCtrl);

    ProjectDetailsModalCtrl.$inject = ['$scope', 'Project', 'Board', 'project'];

    function ProjectDetailsModalCtrl($scope, Project, Board, project) {
        $scope.project = project;
        $scope.addMember = addMember;
        $scope.addBoard = addBoard;
        $scope.deleteBoard = deleteBoard;

        console.log(project);

        getProjectMembers();

        function getProjectMembers() {
            Project.getMembers({projectId: project.id}).$promise.then(function (result) {
                $scope.members = result.content;
            });
        }

        function addMember(email) {
            Project.addMember({projectId: $scope.project.id, email:email})
                .$promise.then(function (result) {
                getProjectMembers();
            });
        }

        function addBoard(board) {
            board.projectId = project.id;
            Board.save(board).$promise.then(function (result) {
                project.boards.push(result);
            });
        }

        function deleteBoard(board) {
            Board.delete({boardId: board.id}).$promise.then(function () {
                _.remove(project.boards, function (b) {
                    return b === board;
                })
            });
        }
    }
})();
