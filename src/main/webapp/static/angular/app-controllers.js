angular.module('app-controllers', [ 'ngRoute' ]).config(
		function($routeProvider) {
			$routeProvider.when('/main', {
				templateUrl : 'static/html/main.html',
				controller : 'videoListController'
			});
			$routeProvider.when('/video/:videoId/:userId', {
				templateUrl : 'static/html/single.html',
				controller : 'videoController'
			});
			$routeProvider.when('/upload', {
				templateUrl : 'static/html/upload.html',
				controller : 'uploadController'
			});
			$routeProvider.otherwise({
				redirectTo : '/main'
			});
		})
		
		.controller('videoListController',
		[ '$scope', 'videoListService', function($scope, videoListService) {
			$scope.videoPopularList = videoListService.listPopular();			
			$scope.videoPage = videoListService.listAll();
		} ])
		
		.controller('videoController',
		[ '$scope', '$routeParams', 'videoService', function($scope, $routeParams, videoService) {
			console.log($routeParams, $routeParams.videoId, $routeParams.userId);
			$scope.currentVideo = videoService.videoById($routeParams.videoId);
			$scope.videoListCurrentUser = videoService.videoListByUser($routeParams.userId);
		} ])

		.controller('uploadController',
		[ '$scope', function($scope) {
			
		} ]);