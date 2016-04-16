angular.module('app-controllers', [ 'ngRoute' ]).config(
		function($routeProvider) {
			$routeProvider.when('/main', {
				templateUrl : 'static/html/main.html',
				controller : 'videoListController'
			});
			$routeProvider.when('/video/:videoId', {
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
			$scope.videoPage = videoListService.listAll();
		} ])
		
		.controller('videoController',
		[ '$scope', '$routeParams', 'videoService', function($scope, $routeParams, videoService) {
			console.log($routeParams, $routeParams.videoId);
			$scope.video = videoService.videoById($routeParams.videoId);
		} ])

		.controller('uploadController',
		[ '$scope', function($scope) {
			
		} ]);