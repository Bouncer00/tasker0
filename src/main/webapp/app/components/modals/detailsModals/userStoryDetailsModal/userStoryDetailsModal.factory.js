(function () {
    angular
        .module('tasker0App')
        .factory('userStoryDetailsModal', userStoryDetailsModal);

    userStoryDetailsModal.$inject = ['$uibModal'];

    function userStoryDetailsModal($uibModal) {

        var open = function(userStory) {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/detailsModals/userStoryDetailsModal/userStoryDetailsModal.html',
                controller: 'UserStoryDetailsModalCtrl',
                controllerAs: 'vm',
                size: "lg",
                resolve: {
                    userStory: function () {
                        return userStory;
                    }
                }
            });
        };

        return {
            open: open
        };
    }

})();
