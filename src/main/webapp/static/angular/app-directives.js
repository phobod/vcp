angular.module('app-directives', [])
.directive('imageonload', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('load', function() {
            	scope.$apply(attrs.imageonload);
            });
            element.bind('error', function(){
            	scope.$apply(attrs.imageonload);
            });
        }
    };
});