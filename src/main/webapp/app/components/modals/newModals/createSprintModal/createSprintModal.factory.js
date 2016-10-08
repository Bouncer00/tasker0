(function () {
    angular
        .module('tasker0App')
        .factory('createSprintModal', createSprintModal);

    createSprintModal.$inject = ['$uibModal'];

    function createSprintModal($uibModal) {

        var open = function(project) {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/newModals/createSprintModal/createSprintModal.html',
                controller: 'CreateSprintModalCtrl',
                controllerAs: 'vm',
                size: "lg",
                resolve: {
                    project: function () {
                        return project;
                    }
                }
            });
        };

        return {
            open: open
        };
    }

})();
