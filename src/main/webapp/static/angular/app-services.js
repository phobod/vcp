angular.module('app-services', [ 'ngResource', 'ngFileUpload' ])
.service("videoService",[ '$resource', function($resource) {
	return {
		listPopularVideos : function() {
			return $resource('/video/popular').query();
		},
		listAllVideosByPage : function(page) {
			return $resource('/video/all?page=:page&size=12&sort=type,desc',{page:page}).get();
		},
		findVideoById : function(videoId) {
			return $resource('/video/:videoId',{videoId:videoId}).get();
		},
		listVideosByUserByPage : function(userId, excludedVideoId, page){
			return $resource('/user/:userId/video/:excludedVideoId?page=:page&size=10&sort=type,desc',{userId:userId, excludedVideoId:excludedVideoId, page:page}).get();
		},
		listAllVideosByUserByPage : function(userId, page){
			return $resource('/user/:userId/video?page=:page&size=12&sort=type,desc',{userId:userId, page:page}).get();
		},
		listBySearchQuery : function(query, page){
			return $resource('/video/search?query=:query&page=:page&size=12',{query: query, page:page}).get();
		}
	}
}])
.service("uploadService", ['Upload', function(Upload){
	return {
		uploadVideo : function(title, description, file){
			return Upload.upload({
                url: 'my-account/upload',
                method: "POST",
                data: {'title': title, 'description': description, file: file }
            })
		}
	}
}])
.service("authServisce", ['$resource', function($resource){
	return {
		getPrincipal : function(){
			return $resource('user').get();
		},
		login : function(data){
			return $resource('login', {}, {
				login : {
					method : 'POST',
					headers : {'Content-Type': 'application/x-www-form-urlencoded'}
				}
			}).login(data);
		},
		logout : function(){
			return $resource('logout', {}).save();
		}
	}
}])

.service("adminService", ['$resource', function($resource){
	return {
		listAllUsers : function() {
			return $resource('/admin/account').get();
		}
	}
}]);