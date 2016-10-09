(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('sprintListCtrl', sprintListCtrl);

    sprintListCtrl.$inject = ['$scope', 'Sprint', 'sprintDetailsModal'];

    function sprintListCtrl($scope, Sprint, sprintDetailsModal) {
        $scope.fetchSprints = fetchSprints;
        $scope.openDetails = openDetails;

        if($scope.control) {
            $scope.control.resetSprints = resetSprints;
            $scope.control.fetchSprints = fetchSprints;
        }

        function resetSprints() {
            $scope.sprints = [];
        }

        function fetchSprints(projectId) {
            Sprint.getByProject({projectId: projectId}).$promise.then(function (result) {
                $scope.sprints = result.content;
            });
        }
        
        function openDetails(sprint){
            sprintDetailsModal.open(sprint);
        }
    }
})();
