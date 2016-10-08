(function () {
    angular
        .module('tasker0App')
        .factory('createUserStoryModal', createUserStoryModal);

    createUserStoryModal.$inject = ['$uibModal'];

    function createUserStoryModal($uibModal) {

        var open = function(sprint) {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/newModals/createUserStoryModal/createUserStoryModal.html',
                controller: 'CreateUserStoryModalCtrl',
                controllerAs: 'vm',
                size: "lg",
                resolve: {
                    sprint: function () {
                        return sprint;
                    }
                }
            });
        };

        return {
            open: open
        };
    }

})();
