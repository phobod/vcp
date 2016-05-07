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
		listAllVideosMyAccountByPage : function(page){
			return $resource('/my-account/video?page=:page&size=10&sort=type,desc',{page:page}).get();
		},
		listBySearchQuery : function(query, page){
			return $resource('/video/search?query=:query&page=:page&size=12',{query: query, page:page}).get();
		}
	}
}])
.service("userService", ['$resource', 'Upload', function($resource, Upload){
	return {
		uploadVideo : function(title, description, file){
			return Upload.upload({
                url: 'my-account/upload',
                method: "POST",
                data: {'title': title, 'description': description, file: file }
            });
		},
		saveVideo : function(videoId, title, description){
			return $resource('my-account/video/:videoId',{videoId:videoId}).save({},{'title': title, 'description': description});
		},
		deleteVideo : function(videoId){
			return $resource('my-account/video/:videoId',{videoId:videoId}).delete();
		}
	}
}])
.service("authService", ['$resource', function($resource){
	return {
		login : function(credentials, rememberMe){
			var data = 'username=' + encodeURIComponent(credentials.username) + '&password=' + encodeURIComponent(credentials.password) + '&rememberMe=' + encodeURIComponent(rememberMe);
			return $resource('login', {}, {
				login : {
					method : 'POST',
					headers : {'Content-Type': 'application/x-www-form-urlencoded'}
				}
			}).login(data);
		},
		logout : function(){
			return $resource('logout', {}).save();
		},
		isAuthenticated : function(){
			
		}
	}
}])

.service("recoveryService", ['$resource', function($resource){
	return {
		sendRestoreEmail : function(login){
			return $resource('recovery/:login',{login:login}).save();
		},
		restorePassword : function(userId, hash, password){
			return $resource('recovery/password').save({},{'id': userId, 'hash': hash, 'password': password});
		}
	}
}])

.service("adminService", ['$resource', 'Upload', function($resource, Upload){
	return {
		listAllUsersByPage : function(page) {
			return $resource('/admin/account?page=:page&size=10&sort=type,desc',{page:page}).get();
		},
		listAllCompaniesByPage : function(page, size) {
			return $resource('/admin/company?page=:page&size=:size&sort=type,desc',{page:page,size:size}).get();
		},
		saveUser : function(user, file) {
			return Upload.upload({
                url: 'admin/account',
                method: "POST",
                data: {'userJson': Upload.json(user), file: file}
            });
		},
		saveCompany : function(company) {
			return $resource('admin/company').save({},company);
		},
		deleteUser : function(userId) {
			return $resource('admin/account/:userId',{userId:userId}).delete();
		},
		deleteCompany : function(companyId) {
			return $resource('admin/company/:companyId',{companyId:companyId}).delete();
		}
	}
}]);