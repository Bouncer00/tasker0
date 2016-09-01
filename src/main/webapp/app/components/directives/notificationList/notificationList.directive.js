(function () {
    angular.module('tasker0App')
        .directive('notificationList', notificationList);

    notificationList.$inject = [];

    function notificationList() {
        return {
            templateUrl: 'app/components/directives/notificationList/notificationList.html',
            controller: 'notificationListCtrl',
            controllerAs: 'vm'
        }
    }
})();
