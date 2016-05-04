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
.service("userService", ['Upload', function(Upload){
	return {
		uploadVideo : function(title, description, file){
			return Upload.upload({
                url: 'my-account/upload',
                method: "POST",
                data: {'title': title, 'description': description, file: file }
            });
		},
		saveVideo : function(videoId, title, description){
			return Upload.upload({
	            url: 'my-account/video/' + videoId,
	            method: "POST",
	            data: {'title': title, 'description': description}
	        });
		},
		deleteVideo : function(videoId){
			return Upload.upload({
	            url: 'my-account/video/' + videoId,
	            method: "DELETE"
	        });
		}
	}
}])
.service("authService", ['$resource', function($resource){
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

.service("adminService", ['$resource', 'Upload', function($resource, Upload){
	return {
		listAllUsersByPage : function(page) {
			return $resource('/admin/account?page=:page&size=10&sort=type,desc',{page:page}).get();
		},
		listAllCompaniesByPage : function(page) {
			return $resource('/admin/company?page=:page&size=10&sort=type,desc',{page:page}).get();
		},
		listAllCompanies : function() {
			return $resource('/admin/company/all').query();
		},
		addUser : function(name, surname, login, password, email, companyId, role, avatar) {
			return Upload.upload({
                url: 'admin/account',
                method: "POST",
                data: {'name': name, 'surname': surname, 'login': login, 'password': password, 'email': email, 'companyId': companyId, 'role': role, avatar: avatar }
            });
		},
		addCompany : function(name, address, email, phone) {
			return Upload.upload({
                url: 'admin/company',
                method: "POST",
                data: {'name': name, 'address': address, 'email': email, 'phone': phone}
            });
		},
		saveUser : function(userId, name, surname, email, companyId) {
			return Upload.upload({
                url: 'admin/account/' + userId,
                method: "POST",
                data: {'name': name, 'surname': surname, 'email': email, 'companyId': companyId}
            });
		},
		saveCompany : function(companyId, address, email, phone) {
			return Upload.upload({
                url: 'admin/company/' + companyId,
                method: "POST",
                data: {'address': address, 'email': email, 'phone': phone}
            });
		},
		deleteUser : function(userId) {
			return Upload.upload({
                url: 'admin/account/' + userId,
                method: "DELETE"
            });
		},
		deleteCompany : function(companyId) {
			return Upload.upload({
                url: 'admin/company/' + companyId,
                method: "DELETE"
            });
		}
	}
}]);