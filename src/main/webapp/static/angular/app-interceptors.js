angular.module('app-interceptors', [])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('authHttpResponseInterceptor');
}])
.factory("authHttpResponseInterceptor", ['$q','$location',function($q,$location){
	return {
        response: function(response){
            return response || $q.when(response);
        },
        responseError: function(rejection) {
            if (rejection.status === 401) {
                $location.path('/login').search('returnTo', $location.path());
            }
            if (rejection.status === 403) {
                $location.path('/access-denied').search('returnTo', $location.path());
            }
            return $q.reject(rejection);
        }
    }
}]);