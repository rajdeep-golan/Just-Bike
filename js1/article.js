const image = document.getElementById('input-image');

const headingLabel = document.getElementById('heading');
const contentLabel = document.getElementById('content');
const headingLink = document.getElementById('headingLink');
const otherTxt = document.getElementById('otherText');
const otherTxt2 = document.getElementById('otherText2');
const otherLnk = document.getElementById('otherLink');
const otherLnk2 = document.getElementById('otherLink2');
const submit = document.getElementById('submit');
var fileImage ;
var imageURLPic ="";

if(image!=null){
image.addEventListener('change', function(){
	const file = this.files[0];
	fileImage = file;
	if(file){
		const reader = new FileReader();
		reader.addEventListener('load', function(){
				imagePlace.setAttribute("src",this.result);
				imagePlace.setAttribute("style","width: 100px;height: 100px;");
				console.log(this.result);
		});
		reader.readAsDataURL(file);
	}
});
}
var n = Date.now();
if(image!=null){
submit.addEventListener('click',(e) =>{
	e.preventDefault();
	console.log('1st');
	var uploadTask = firebase.storage().ref('Blogs/'+n+".png").put(fileImage);

	console.log('2nd: '+fileImage.name);

	uploadTask.on('state_changed',function(snapshot){
		var progress = (snapshot.bytesTransferred / snapshot.totalBytes )* 100;
		document.getElementById('progressStatus').innerHTML = 'Uploading '+progress+"%";
	},
	function(error){
		alert('Error in saving Image');
	},
	function(){
		uploadTask.snapshot.ref.getDownloadURL().then(function(url){
			//console.log(url);
			imageURLPic = url;

console.log(otherTxt.value);
		
        firebase.database().ref('blog/' + n).set({
    heading: headingLabel.value,
    paragraph: content.value,
    image : imageURLPic,
    headingLink : headingLink.value,
    otherText : otherTxt.value,
    otherLink : otherLnk.value,
    otherText2 : otherTxt2.value,
    otherLink2 : otherLnk2.value

  }).then(function() {
    alert('Successfully Added!');
    location.reload();
  });
			

	}

	);
		});


});
}