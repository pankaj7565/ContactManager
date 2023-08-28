/**
 * 
 */

 console.log("this is script file")
 
  /*for the navebar slider*/

     function openNav() {
  document.getElementById("mySidebar").style.width = "250px";
  document.getElementById("main").style.marginLeft = "250px";
}

/* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
function closeNav() {
  document.getElementById("mySidebar").style.width = "0";
  document.getElementById("main").style.marginLeft = "0";
}




// for search

const search = ()=>{
  // console.log("searching");

  let query = $("#search-input").val()
  console.log(query);

  if(query==''){
  $(".search-result").hide();
  }else{
    // search
    console.log(query);

    // /sending request ot server
let url = `http://localhost:8081/search/${query}`;

   fetch(url).then((response)=>{

    return response.json();



   })
   .then((data)=>{
// data
// console.log(data);


// target search result
let text = `<div class='list-group'>`;


data.forEach((contact) => {
  

  text+= `<a href='/user/${contact.cid}/contact' class='list-group-item 
  list-group-item-action'>
  ${contact.name}</a>`

});

text+=`</div>`;
 
$(".search-result").html(text);
$(".search-result").show();

   });
  

  }



};

// payment js first req to order

const paymentStart=()=>{

  console.log("payment started..");
  let amount=$("#payment_field").val();
  console.log(amount);
  if(amount =='' || amount==null){
    // alert('amount is required');
    swal("failed !!","amount is required", "error");
    return;
  }



// code
// ajax using to send request to server to create order jquery

$.ajax(
  {
    url : '/user/create_order',
data: JSON.stringify({amount:amount,info:"order_request"}),
contentType:"application/json",
type:"POST",
dataType:"json",
success:function(response){
  // invoke when success
console.log(response);

if(response.status == "created"){
  // open payment form


let options={
  key: 'rzp_test_2oewnfAt1uEEm2',
  amount:response.amount,
  currency:'INR',
  name:'Smart Contact Manager',
  description:'Donation',
  image:"https://c8.alamy.com/comp/2EMFX5K/vector-logo-of-money-donate-blood-donation-2EMFX5K.jpg",
  order_id:response.id,

  handler:function(response){
    console.log(response.razorpay_payment_id);
    console.log(response.razorpay_order_id);
    console.log(response.razorpay_signature);
    console.log('payment successfull !!');


    updatepaymenOnserver(response.razorpay_payment_id,
      response.razorpay_order_id,'paid' )

   swal("congrats","Congrats !! payment is succesfull", "Success")

  },

  prefill: {
    name: "",
    email: "",
    contact: ""
    },

    notes: {
      address: "learncodewithpankaj"
      },

      "theme": {
      "color": "#3399cc"
      }
     };


    //  payment initiate
    // create razor pay obj

    let rzp =  new Razorpay(options);
    
    rzp.on('payment.failed', function (response){
      console.log(response.error.code);
      console.log(response.error.description);
      console.log(response.error.source);
      console.log(response.error.step);
      console.log(response.error.reason);
      console.log(response.error.metadata.order_id);
      console.log(response.error.metadata.payment_id);
      swal("failed" ,"oops payment failed!!","error");
     });
     


    rzp.open();
}

},
error:function(error){
  // invoke when error
  console.log(error);
  console.log("something went wrong");

}

  }
)







// updatepaymenOnserver

function updatepaymenOnserver(payment_id,order_id,status)
{
  $.ajax({
      url : '/user/update_order',
  data: JSON.stringify({
    payment_id:payment_id,
    order_id:order_id,
    status:status,
}),
  contentType:"application/json",
  type:"POST",
  dataType:"json",
  success:function(response){
    swal("congrats","Congrats !! payment is succesfull", "Success")
  },
  error:function(error){
    swal("failed" ,"your payment is successfull, but we did not get on server, we will contact you","error");

  }



})

}


}
 