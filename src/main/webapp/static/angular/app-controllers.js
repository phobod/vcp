angular.module('app-controllers', [ 'ngRoute' ]).config(
		function($routeProvider) {
			$routeProvider.when('/main', {
				templateUrl : 'static/html/main.html',
				controller : 'allListVideoController'
			});
			$routeProvider.when('/video/:videoId/:userId', {
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
		[ '$scope', 'popularListVideoService', 'allListVideoService', function($scope, popularListVideoService, allListVideoService) {
			$scope.videoPopularList = popularListVideoService.listPopularVideos();
			var nextPage = 0;
			$scope.hideButton = false;
			$scope.videos = [];
			$scope.showMore = function(){
				allListVideoService.listAllVideosByPage(nextPage).$promise.then(function(value){
					Array.prototype.push.apply($scope.videos, value.content);
					nextPage++;
					if(nextPage == value.totalPages){
						$scope.hideButton = true;
					}
				});
			};
			$scope.showMore();
		} ])
		
		.controller('listVideosByUserController',
		[ '$scope', '$routeParams', 'chosenVideoService', 'listVideoByUserService', function($scope, $routeParams, chosenVideoService, listVideoByUserService) {
			$scope.currentVideo = chosenVideoService.findVideoById($routeParams.videoId);
			var nextPage = 0;
			$scope.hideButton = false;
			$scope.listVideosByUser = [];
			$scope.showMore = function(){
				listVideoByUserService.listVideosByUserByPage($routeParams.userId, nextPage).$promise.then(function(value){
					Array.prototype.push.apply($scope.listVideosByUser, value.content);
					nextPage++;
					if(nextPage == value.totalPages){
						$scope.hideButton = true;
					}
				});
			};
			$scope.showMore();
		} ])

		.controller('uploadController',
		[ '$scope', function($scope) {
			
		} ]);