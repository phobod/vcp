angular.module('app-services', [ 'ngResource' ])
.service("videoListService",[ '$resource', function($resource) {
	return {
		listAll : function() {
			return $resource('/main').get();
		}
	}
} ])
.service("videoService", [ '$resource', function($resource) {
	return {
		videoById : function(id) {
			return $resource('/video/:videoId',{videoId:id}).get();
		}
	}
} ]);