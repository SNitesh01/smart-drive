
 
/* function deleteContact(cId)
 {
	swal({
  title: "Are you sure?",
  text: "Once deleted, you will not be able to recover this Contact..!",
  icon: "warning",
  buttons: true,
  dangerMode: true,
})
.then((willDelete) => {
  if (willDelete) {
	window.location="/user/delete/" +cId;
    /*swal("Poof! Your imaginary file has been deleted!", {
      icon: "success",
    });
  } else {
    swal("Your Contact is safe..!!");
  }
});	
}*/

  const search = ( )=> {
	//console.log("searching....");
	let query=$("#search-input").val()
	if(query=='')
	{
	  $(".search-result").hide();

	}else{
		//search
	    console.log(query);
	    //sending req to server
	    let url='http://localhost:9099/search/${query}';
	    fetch(url).then(response=>{
		return response.json();
	    
	    }).then(data) => {
		//data result
		//console.log(data);'
		let text='<div class='list-group'>';
		data.forEach(contact=>{
			text+='<a href="/user/${contacts.cId}/contacts" class='list-group-item list-group-item-action'>${contacts.name}</a>
		});
		
		
		
		text+='</div>'
		$(".search-result").html(text);
		$(".search-result").show();
	});
	    
	   
	}
};


 