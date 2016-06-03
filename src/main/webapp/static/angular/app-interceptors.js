angular.module('app-interceptors', [])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('httpResponseInterceptor');
    $httpProvider.interceptors.push('csrfTokenInterceptor');
}])
.factory("httpResponseInterceptor", ['$q','$location', '$rootScope', function($q, $location, $rootScope){
	$rootScope.principal = {
			auth : false,
			name : '',
			role : 'anonym'
	};
	$rootScope.errorMessage = {
			message : '',
			description : '', 
			code : ''
	};
	return {
        response: function(response){
        	var name = response.headers('PrimcipalName');
        	var role = response.headers('PrimcipalRole');
        	if(name != undefined && role != undefined) {
        		$rootScope.principal = {
                	auth : true,
        			name : name,
                	role : role
                };
        	} else{
        		$rootScope.principal = {
        			auth : false,
        			name : '',
                	role : 'anonym'
                };
        	}
            return response;
        },
        responseError: function(rejection) {
            if (rejection.status === 401) {
                $location.path('/login').search('returnTo', $location.path());
            } else if (rejection.status === 403) {
                $location.path('/access-denied').search('returnTo', $location.path());
            } else if (rejection.status != 500 || rejection.data.message != 'Not valid') {
            	$rootScope.errorMessage = {
            			message : rejection.data.message,
            			description : rejection.data.description, 
            			code : rejection.status
            	};
            	$location.path('/error');
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