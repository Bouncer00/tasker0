(function () {
    angular
        .module('tasker0App')
        .controller('ProjectDetailsModalCtrl', ProjectDetailsModalCtrl);

    ProjectDetailsModalCtrl.$inject = ['$scope', 'Project', 'project'];

    function ProjectDetailsModalCtrl($scope, Project, project) {
        $scope.project = project;
        $scope.addMember = addMember;

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
    }
})();
;
