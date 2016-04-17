angular.module('app-services', [ 'ngResource' ])
.service("videoListService",[ '$resource', function($resource) {
	return {
		listAll : function() {
			return $resource('/all').get();
		}, 
		listPopular : function() {
			return $resource('/popular').get();
		}
	}
} ])
.service("videoService", [ '$resource', function($resource) {
	return {
		videoById : function(id) {
			return $resource('/currentvideo/:videoId',{videoId:id}).get();
		},
		videoListByUser : function(id) {
			return $resource('/videobyuser/:userId',{userId:id}).get();
	}
	}
} ]);