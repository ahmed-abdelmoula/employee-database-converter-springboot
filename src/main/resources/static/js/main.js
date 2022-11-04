/**
 * 
 */
$(document).ready(function()
{

$('.nBtn,.table .eBtn').on('click',function(event){
	event.preventDefault();
	var href=$(this).attr('href');
	var text=$(this).text();
	if(text=='Edit')
		{
		
		
	$.get(href,function(user,status){
		
		$('.myForm #id').val(user.id);
		$('.myForm #nom').val(user.nom);
		$('.myForm #prenom').val(user.prenom);
		$('.myForm #cin').val(user.cin);

	});
	
	
$('.myForm #exampleModal').modal();
		}	else {
			$('.myForm #exampleModal').modal();

			$('.myForm #id').val('');
			$('.myForm #nom').val('');
			$('.myForm #prenom').val('');
			$('.myForm #cin').val('');

			
			
		}

});
$('.table .delBtn').on('click',function(event){
	event.preventDefault();
	var href=$(this).attr('href');
	$('#myModal #delRef').attr('href',href);
	$('#myModal').modal();
});



});