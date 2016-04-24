angular.module('app-controllers', ['ngRoute', 'ngFileUpload'])
		.config(function($routeProvider) {
			$routeProvider.when('/main', {
				templateUrl : 'static/html/main.html',
				controller : 'allListVideoController'
			});
			$routeProvider.when('/user/:userId/video/:videoId', {
				templateUrl : 'static/html/single.html',
				controller : 'listVideosByUserController'
			});
			$routeProvider.when('/user/:userId/video', {
				templateUrl : 'static/html/uservideo.html',
				controller : 'allListVideoByUserController'
			});
			$routeProvider.when('/upload', {
				templateUrl : 'static/html/upload.html',
				controller : 'uploadController'
			});
			$routeProvider.otherwise({
				redirectTo : '/main'
			});
		})
		
		.factory('controllersFactory',function(){
			return{
				createPaginationController : function(scope, service){
					scope.videos = service.getData(0);
					scope.loading = false;
					scope.showMore = function (){
						scope.loading = true;
						var nextPage = scope.videos.page.number + 1;
						service.getData(nextPage).$promise.then(function (value) {
							value.content = scope.videos.content.concat(value.content);
							scope.videos = value;
							scope.loading = false;
						});
					};
				}
			}
		})
		
		.controller('allListVideoController',
		[ '$scope', 'videoService', 'controllersFactory', function($scope, videoService, controllersFactory) {
			$scope.videoPopularList = videoService.listPopularVideos();
			controllersFactory.createPaginationController($scope, {getData : videoService.listAllVideosByPage});
		} ])
		
		.controller('listVideosByUserController',
		[ '$scope', '$routeParams', 'videoService', 'controllersFactory', function($scope, $routeParams, videoService, controllersFactory) {
			$scope.currentVideo = videoService.findVideoById($routeParams.videoId);
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listVideosByUserByPage($routeParams.userId, $routeParams.videoId, page); 
				}
			});				
		} ])

		.controller('allListVideoByUserController',
		[ '$scope', '$routeParams', 'videoService', 'controllersFactory', function($scope, $routeParams, videoService, controllersFactory) {
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listAllVideosByUserByPage($routeParams.userId, page); 
				}
			});	
		} ])
		
		.controller('uploadController', ['$scope', '$window', 'Upload', function ($scope, $window, Upload) {
			$scope.submit = function(){
				Upload.upload({
                    url: 'user/upload',
                    method: "POST",
                    data: {'title': $scope.title, 'description': $scope.description, file: $scope.file }
                }).success(function (data, status, headers, config) {
                    $scope.title = null;
                    $scope.description = null;
                    $scope.file = null;
                    $window.alert("File upload SUCCESS");
                }).error(function (data, status) {
                    $scope.title = null;
                    $scope.description = null;
                    $scope.file = null;
                	$window.alert("File upload FAILED");
                });
			};			
		}]);

