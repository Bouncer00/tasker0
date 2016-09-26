(function () {
    angular
        .module('tasker0App')
        .factory('createUserStoryModal', createUserStoryModal);

    createUserStoryModal.$inject = ['$uibModal'];

    function createUserStoryModal($uibModal) {

        var open = function(project) {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/createUserStoryModal/createUserStoryModal.html',
                controller: 'CreateUserStoryModalCtrl',
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
