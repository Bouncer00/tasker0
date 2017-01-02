(function () {
    'use strict';
    angular.module('tasker0App')
        .directive('ngReallyClick', ngReallyClick);

        ngReallyClick.$inject = ['$uibModal'];

        function ngReallyClick($modal) {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    element.bind('click', function() {
                        var message = attrs.ngReallyMessage || "Are you sure ?";
                        var modalHtml = '<div class="modal-body">' + message + '</div>';
                        modalHtml += '<div class="modal-footer"><button class="btn btn-primary" ng-click="ok()">OK</button><button class="btn btn-warning" ng-click="cancel()">Cancel</button></div>';
                        var uibModalInstance = $modal.open({
                            template: modalHtml,
                            controller: 'ngReallyClickCtrl'
                        });
                        uibModalInstance.result.then(function() {
                            scope.$apply(attrs.ngReallyClick); //raise an error : $digest already in progress
                        }, function() {
                            //Modal dismissed
                        });
                        //*/
                    });
                }
            }
        }
})();
