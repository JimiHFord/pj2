$(document).ready(function() {
	/* FancyBox */
	$("a[rel^='fancybox']").fancybox({
		'transitionIn'		: 'none',
		'transitionOut'		: 'none',
		'titlePosition' 	: 'over',
		'titleFormat'		: function(title, currentArray, currentIndex, currentOpts) {
			return '<span id="fancybox-title-over">Image ' + (currentIndex + 1) + ' / ' + currentArray.length + (title.length ? ' &nbsp; ' + title : '') + '</span>';
		}
	});
	
	$("a img").hover(
		function() {
			jQuery(this).animate({"opacity": ".8"}, "fast");
			},
		function() {
			jQuery(this).animate({"opacity": "1"}, "fast");
	});
	
	$('#mobile-nav-btn').click(
		function () {
			$('.sf-menu').toggleClass("xactive");	
	});
	$('.mobnav-subarrow').click(
		function () {
			$(this).parent().toggleClass("xpopdrop");
	});
});