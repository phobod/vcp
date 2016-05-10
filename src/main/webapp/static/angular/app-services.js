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
.service("authService", ['$http', function($http){
	return {
		login : function(credentials, rememberMe){
			return $http.post('/login', $.param({'username':credentials.username,'password':credentials.password,'remember-me':rememberMe}), {
				headers : {'Content-Type': 'application/x-www-form-urlencoded'}
			});
		},
		logout : function(){
			return $http.post('/logout', {});
		}
	}
}])

.service("recoveryService", ['$resource', '$http', function($resource, $http){
	return {
		sendRestoreEmail : function(login){
			return $resource('recovery/:login',{login:login}).save();
		},
		restorePassword : function(userId, hash, password){
			console.log('recoveryService: restorePassword');
//			return $http.post('/recovery/password', $.param({'id': userId, 'hash': hash, 'password': password}));
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
		saveUser : function(user) {
			return $resource('admin/account').save({},user);
		},
		getAvatarUrl : function(email){
			var form = {email: email};
			return $resource('admin/emailhash').save({},{email: email});
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