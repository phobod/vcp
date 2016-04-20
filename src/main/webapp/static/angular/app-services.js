angular.module('app-services', [ 'ngResource' ])
.service("popularListVideoService",[ '$resource', function($resource) {
	return {
		listPopularVideos : function() {
			return $resource('/video/popular').query();
		} 
	}
} ])
.service("allListVideoService",[ '$resource', function($resource) {
	return {
		listAllVideosByPage : function(page) {
			return $resource('/video/all/:pageNum',{pageNum:page}).get();
		} 
	}
} ])
.service("chosenVideoService", [ '$resource', function($resource) {
	return {
		findVideoById : function(id) {
			return $resource('/video/:videoId',{videoId:id}).get();
		}
	}
} ])
.service("listVideoByUserService",['$resource', function($resource){
	return {
		listVideosByUserByPage : function(id,page){
			return $resource('/user/:userId/video/:pageNum',{userId:id,pageNum:page}).get();
		}
	}
}]);