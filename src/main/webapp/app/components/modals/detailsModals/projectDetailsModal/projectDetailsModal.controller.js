(function () {
    angular
        .module('tasker0App')
        .controller('ProjectDetailsModalCtrl', ProjectDetailsModalCtrl);

    ProjectDetailsModalCtrl.$inject = ['$scope', 'Project', 'project'];

    function ProjectDetailsModalCtrl($scope, Project, project) {
        $scope.project = project;

        getProjectMembers();

        function getProjectMembers() {
            Project.getMembers({projectId: project.id}).$promise.then(function (result) {
                console.log(result);
            });
        }
    }
})();
