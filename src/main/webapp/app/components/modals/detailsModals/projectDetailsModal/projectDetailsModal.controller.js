(function () {
    angular
        .module('tasker0App')
        .controller('ProjectDetailsModalCtrl', ProjectDetailsModalCtrl);

    ProjectDetailsModalCtrl.$inject = ['$scope', 'project'];

    function ProjectDetailsModalCtrl($scope, project) {
        $scope.project = project;
        console.log(project);
    }
})();
