(function () {
    angular
        .module('tasker0App')
        .factory('createProjectModal', createProjectModal);

    createProjectModal.$inject = ['$uibModal'];

    function createProjectModal($uibModal) {

        var open = function() {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/createProjectModal/createProjectModal.html',
                controller: 'CreateProjectModalCtrl',
                controllerAs: 'vm',
                size: "lg"
            });
        };

        return {
            open: open
        };
    }

})();
