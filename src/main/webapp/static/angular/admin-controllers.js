angular.module('admin-controllers', ['ngRoute'])
	.config(function($routeProvider){
		$routeProvider.when('/admin/account', {
			templateUrl : 'static/html/page/account.html',
			controller : 'accountController'
		});
		$routeProvider.when('/admin/company', {
			templateUrl : 'static/html/page/company.html',
			controller : 'companyController'
		});
		$routeProvider.when('/admin/statistics', {
			templateUrl : 'static/html/page/statistics.html',
			controller : 'statisticsController'
		});
	})

	.controller('accountController', ['$scope', 'adminService', 'controllersFactory', function($scope, adminService, controllersFactory){
		controllersFactory.createPaginationController($scope, {getData : adminService.listAllUsers});
		var maxInt = 2147483647;
		$scope.allCompanies = adminService.listAllCompanies(0,maxInt);
		$scope.clearData = function(){
			$scope.error = false;
			$scope.selectedAccount = null;
			$scope.company = null;
			$scope.avatarPreview = 'http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm&s=300';
			$scope.selectedAccountIndex = -1;			
		};
		$scope.getAvatarUrl = function(value){
			if (value) {
				adminService.getAvatarUrl($scope.selectedAccount.email).$promise.then(function(data){
					$scope.selectedAccount.avatarUrl = data.url;
					$scope.avatarPreview = $scope.selectedAccount.avatarUrl + '&s=300';
				});
			}
		}
		$scope.saveUser = function(){
			$scope.selectedAccount.company = $scope.company;				
			adminService.saveUser($scope.selectedAccount).$promise.then(function(resp){
				if ($scope.selectedAccountIndex > -1){
					$scope.records.content[$scope.selectedAccountIndex] = resp;
				} else {
					$scope.records.content.push(resp);
				}
				$scope.clearData();
			},function(error){
				$scope.error = true;
				$scope.errorMessage = error.data.description;
			})
		}
		$scope.editUser = function(idx){
			$scope.selectedAccountIndex = idx;
			$scope.selectedAccount = angular.copy($scope.records.content[idx]);
			$scope.company = $scope.allCompanies.content.find(function(item){
				return item.id == $scope.selectedAccount.company.id;
			});
			$scope.avatarPreview = $scope.selectedAccount.avatarUrl + '&s=300';
		}
		$scope.deleteUser = function(idx){
			adminService.deleteUser($scope.records.content[idx].id).$promise.then(function(resp){
				$scope.records.content.splice(idx,1);					
			});
		}
		$scope.clearData();
	}])
	
	.controller('companyController', ['$scope', 'adminService', 'controllersFactory', function($scope, adminService, controllersFactory){
		controllersFactory.createPaginationController($scope, {getData : function(page){return adminService.listAllCompanies(page, 10)}});
		$scope.clearData = function(){
			$scope.error = false;
			$scope.selectedCompany = null;
			$scope.selectedCompanyIndex = -1;			
		};
		$scope.saveCompany = function(){
			adminService.saveCompany($scope.selectedCompany).$promise.then(function(resp){
				if ($scope.selectedCompanyIndex > -1) {
					$scope.records.content[$scope.selectedCompanyIndex] = resp;
				} else {
					$scope.records.content.push(resp);
				}
				$scope.clearData();
			},function(error){
				$scope.error = true;
				$scope.errorMessage = error.data.description;
			})
		}
		$scope.editCompany = function(idx){
			$scope.selectedCompanyIndex = idx;
			$scope.selectedCompany = angular.copy($scope.records.content[idx]);
		}
		$scope.deleteCompany = function(idx){
			adminService.deleteCompany($scope.records.content[idx].id).$promise.then(function(resp){
				$scope.records.content.splice(idx,1);					
			});
		}
		$scope.clearData();
	}])
	
	.controller('statisticsController', ['$scope', 'adminService', function($scope, adminService){
		$scope.videoStatisticsList = adminService.listVideoStatistics();
	}]);