angular.module('app-services', [ 'ngResource' ])
.service("videoService",[ '$resource', function($resource) {
	return {
		listPopularVideos : function() {
			return $resource('/video/popular').query();
		},
		listAllVideosByPage : function(page) {
			return $resource('/video/all?page=' + page + '&size=12&sort=type,desc').get();
		},
		findVideoById : function(videoId) {
			return $resource('/video/:videoId',{videoId:videoId}).get();
		},
		listVideosByUserByPage : function(userId, excludedVideoId, page){
			return $resource('/user/:userId/video/:excludedVideoId?page=' + page + '&size=10&sort=type,desc',{userId:userId,excludedVideoId:excludedVideoId}).get();
		}
	}
}]);