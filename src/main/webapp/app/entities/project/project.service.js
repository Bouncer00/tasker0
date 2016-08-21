(function() {
    'use strict';
    angular
        .module('tasker0App')
        .factory('Project', Project);

    Project.$inject = ['$resource', 'DateUtils'];

    function Project ($resource, DateUtils) {
        var resourceUrl =  'api/projects/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.created = DateUtils.convertLocalDateFromServer(data.created);
                        data.deadLine = DateUtils.convertLocalDateFromServer(data.deadLine);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.created = DateUtils.convertLocalDateToServer(data.created);
                    data.deadLine = DateUtils.convertLocalDateToServer(data.deadLine);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.created = DateUtils.convertLocalDateToServer(data.created);
                    data.deadLine = DateUtils.convertLocalDateToServer(data.deadLine);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
