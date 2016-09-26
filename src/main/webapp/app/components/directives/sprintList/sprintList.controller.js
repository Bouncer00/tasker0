(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('sprintListCtrl', sprintListCtrl);

    sprintListCtrl.$inject = ['$scope', 'Sprint'];

    function sprintListCtrl($scope, Sprint) {
        $scope.fetchSprints = fetchSprints;
        $scope.control.fetchSprints = function (projectId) {
            fetchSprints(projectId)
        };

        function fetchSprints(projectId) {
            Sprint.getByProject({projectId: projectId}).$promise.then(function (result) {
                $scope.sprints = result.content;
            });
        }
    }
})();
