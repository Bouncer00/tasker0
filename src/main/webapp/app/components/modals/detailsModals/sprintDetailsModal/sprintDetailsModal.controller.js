(function () {
    angular
        .module('tasker0App')
        .controller('SprintDetailsModalCtrl', SprintDetailsModalCtrl);

    SprintDetailsModalCtrl.$inject = ['$scope', 'sprint'];

    function SprintDetailsModalCtrl($scope, sprint) {
        $scope.sprint = sprint;
    }
})();
