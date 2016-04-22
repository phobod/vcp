angular.module('app-controllers', ['ngRoute'])
		.config(function($routeProvider) {
			$routeProvider.when('/main', {
				templateUrl : 'static/html/main.html',
				controller : 'allListVideoController'
			});
			$routeProvider.when('/user/:userId/video/:videoId', {
				templateUrl : 'static/html/single.html',
				controller : 'listVideosByUserController'
			});
			$routeProvider.when('/upload', {
				templateUrl : 'static/html/upload.html',
				controller : 'uploadController'
			});
			$routeProvider.otherwise({
				redirectTo : '/main'
			});
		})
		
		.controller('allListVideoController',
		[ '$scope', 'videoService', function($scope, videoService) {
			$scope.videoPopularList = videoService.listPopularVideos();
			var nextPage = 0;
			var totalImages = 0;
			var loadedImages = 0;
			var lastPage = false;
			var countLoadedImages = 0;
			$scope.showMoreButton = false;
			$scope.loadingSpinner = false;
			$scope.loadedVideosId = [];
			$scope.videos = [];
			$scope.updateLoadingSpinner = function() {
				loadedImages++;
				if(loadedImages == totalImages){
					$scope.loadingSpinner = false;
					for(var i = countLoadedImages; i < loadedImages; i++){
						$scope.loadedVideosId[i] = $scope.videos[i].id;
					}
					countLoadedImages = loadedImages;
					if(!lastPage){
						$scope.showMoreButton = true;
					}
				}
			};
			$scope.showMore = function(){
				$scope.loadingSpinner = true;
				$scope.showMoreButton = false;		
				videoService.listAllVideosByPage(nextPage).$promise.then(function(value){
					totalImages += value.content.length;
					Array.prototype.push.apply($scope.videos, value.content);
					nextPage++;
					if(nextPage == value.page.totalPages){
						lastPage = true;
					}
				});
			};
			$scope.showMore();
		} ])
		
		.controller('listVideosByUserController',
		[ '$scope', '$routeParams', 'videoService', function($scope, $routeParams, videoService) {
			$scope.currentVideo = videoService.findVideoById($routeParams.videoId);
			var nextPage = 0;
			var totalImages = 0;
			var loadedImages = 0;
			var lastPage = false;
			var countLoadedImages = 0;
			$scope.showMoreButton = false;
			$scope.loadingSpinner = false;
			$scope.loadedVideosId = [];
			$scope.videos = [];
			$scope.updateLoadingSpinner = function() {
				loadedImages++;
				if(loadedImages == totalImages){
					$scope.loadingSpinner = false;
					for(var i = countLoadedImages; i < loadedImages; i++){
						$scope.loadedVideosId[i] = $scope.videos[i].id;
					}
					countLoadedImages = loadedImages;
					if(!lastPage){
						$scope.showMoreButton = true;
					}
				}
			};			
			$scope.showMore = function(){
				$scope.loadingSpinner = true;
				$scope.showMoreButton = false;		
				videoService.listVideosByUserByPage($routeParams.userId, $routeParams.videoId, nextPage).$promise.then(function(value){
					totalImages += value.content.length;
					Array.prototype.push.apply($scope.videos, value.content);
					nextPage++;
					if(nextPage == value.page.totalPages){
						lastPage = true;
					}
				});
			};
			$scope.showMore();
		} ])

		.controller('uploadController',
		[ '$scope', function($scope) {
			
		} ]);

