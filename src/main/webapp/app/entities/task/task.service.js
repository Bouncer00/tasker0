(function() {
    'use strict';
    angular
        .module('tasker0App')
        .factory('Task', Task);

    Task.$inject = ['$resource', 'DateUtils'];

    function Task ($resource, DateUtils) {
        var resourceUrl =  'api/tasks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.created = DateUtils.convertLocalDateFromServer(data.created);
                        data.updated = DateUtils.convertLocalDateFromServer(data.updated);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.created = DateUtils.convertLocalDateToServer(data.created);
                    data.updated = DateUtils.convertLocalDateToServer(data.updated);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.created = DateUtils.convertLocalDateToServer(data.created);
                    data.updated = DateUtils.convertLocalDateToServer(data.updated);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
