angular.module('app-interceptors', [])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('authHttpResponseInterceptor');
    $httpProvider.interceptors.push('csrfTokenInterceptor');
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
}])
.factory('csrfTokenInterceptor', function () {
	var csrfToken = null;
	return {
		response: function (response) {
			if (response.headers('X-CSRF-TOKEN')) {
				csrfToken = response.headers('X-CSRF-TOKEN');
			}
			return response;
		},
		request: function (config) {
			if ((config.method == 'POST' || config.method == 'PUT' || config.method == 'DELETE')&& csrfToken) {
				config.headers['X-CSRF-TOKEN'] = csrfToken;
			}
			return config;
		}
	}
});