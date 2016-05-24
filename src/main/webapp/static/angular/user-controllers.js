angular.module('user-controllers', ['ngRoute'])
	.config(['$routeProvider', function($routeProvider){
		$routeProvider.when('/my-account/video', {
			templateUrl : 'static/html/page/myaccount.html',
			controller : 'allListVideoMyAccountController'
		});
		$routeProvider.when('/my-account/upload', {
			templateUrl : 'static/html/page/upload.html',
			controller : 'uploadVideoController'
		});
		$routeProvider.when('/my-account/video/:videoId', {
			templateUrl : 'static/html/page/editvideo.html',
			controller : 'editVideoController'
		});
	}])

	.controller('allListVideoMyAccountController',[ '$scope', '$window', 'userService', 'controllersFactory', function($scope, $window, userService, controllersFactory) {
		controllersFactory.createPaginationController($scope, {getData : userService.listVideos});
		$scope.deleteVideo = function(idx){
			var video = $scope.records.content[idx];
			userService.deleteVideo(video.id).$promise.then(function(resp){
				$scope.records.content.splice(idx,1);
			}, function(resp){
				$window.alert("File delete FAILED");
			});
		};
	} ])
	
	.controller('uploadVideoController', ['$scope', '$window', '$location', 'userService', function ($scope, $window, $location, userService) {
		$scope.submit = function(){
			userService.uploadVideo($scope.title, $scope.description, $scope.file).then(function(resp){
				$window.alert("File upload SUCCESS");
				$scope.clearData();
			}, function(resp){
				$window.alert("File upload FAILED");
				$scope.clearData();
			}, function(evt){
				$scope.progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
			});
		};
		$scope.clearData = function(){
	        $scope.title = null;
	        $scope.description = null;
	        $scope.file = null;	
	        $scope.progressPercentage = null;
		};
	}])
	
	.controller('editVideoController',[ '$scope', '$window', '$location', '$routeParams', 'videoService', 'userService', 'controllersFactory', function($scope, $window, $location, $routeParams, videoService, userService, controllersFactory) {
		videoService.findVideoById($routeParams.videoId).$promise.then(function(data){
			$scope.currentVideo = data;
			$scope.title = data.title;
			$scope.description = data.description;
		});
		$scope.saveVideo = function(){
			userService.saveVideo($routeParams.videoId, $scope.title, $scope.description).$promise.then(function(resp){
				$window.alert("File save SUCCESS");
				$location.path('/my-account/video');
			}, function(resp){
				$window.alert("File save FAILED");
			});
		};
	}]);
	