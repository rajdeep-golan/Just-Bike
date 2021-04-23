var rightColumn = document.getElementById('rightcolumn');
var blogNo = location.search.substring(1);
     
loadData()

function loadData(){
	var headingData,paraData,imageData,headingLink,otherText,otherLink,otherText2,otherLink2;
	firebase.database().ref('blog/'+blogNo).once('value',   function(snapshot) {
    var blogKey = snapshot.key;
    snapshot.forEach(function(infoSnap) {
      
 var infoKey = infoSnap.key;
      var infoData = infoSnap.val();
      if(infoKey == "heading")
      	headingData = infoData;
       if(infoKey == "paragraph")
      	paraData = infoData;
       if(infoKey == "image")
      	imageData = infoData;
      if(infoKey == "headingLink")
      	headingLink = infoData;
      if(infoKey == "otherText")
      	otherText = infoData;
       if(infoKey == "otherLink")
      	otherLink = infoData;   
      	if(infoKey == "otherText2")
      	otherText2 = infoData;
       if(infoKey == "otherLink2")
      	otherLink2 = infoData;    
      });
      createHeading(headingData,headingLink+"?"+blogKey);
      createPara(paraData);
      createAhref(otherText,otherLink);
      createImage(imageData);
      createAhref(otherText2,otherLink2);
    });
 
}
function createHeading(text,goLink) {
	var x = document.createElement("BR");
	rightColumn.appendChild(x);

	var a = document.createElement("a");
	var link = document.createTextNode(text);
                  
                // Append the text node to anchor element.
                a.appendChild(link); 
                  
                // Set the title.
               // a.title = "This is Link"; 
                  
                // Set the href property.
                a.href = goLink; 
                  
                // Append the anchor element to the body.
               // document.body.prepend(a); 
		  var h1 = document.createElement('h1');
      //  h1.textContent = text;
        h1.setAttribute('class', 'note');
        h1.style.color = "#3F51B5";
         

h1.appendChild(a);

        rightColumn.appendChild(h1);
}
function createPara(text) {
	  var p = document.createElement('p');
        p.textContent = text;
        p.setAttribute('class', 'note');
         p.style.fontSize= '16px';
        rightColumn.appendChild(p);
	}
      
function createImage(text) {
        var x = document.createElement('IMG');
        x.setAttribute("src", text);
  x.setAttribute("alt", "The Pulpit Rock");
  x.style.maxWidth = "100%";
    x.style.height = "auto";
    x.style.width = "auto\9";
        x.setAttribute('class', 'note');

        rightColumn.appendChild(x);

      
}
function createAhref(text,goLink) {
	var x = document.createElement("BR");
	rightColumn.appendChild(x);

	var a = document.createElement("a");
	var link = document.createTextNode(text);
                  
                // Append the text node to anchor element.
                a.appendChild(link); 
                  
                // Set the title.
               // a.title = "This is Link"; 
                  
                // Set the href property.
                a.href = goLink; 
                  
                // Append the anchor element to the body.
               // document.body.prepend(a); 
		  var h1 = document.createElement('h6');
      //  h1.textContent = text;
        h1.setAttribute('class', 'note');
        h1.style.color = "#3F51B5";
         

h1.appendChild(a);

        rightColumn.appendChild(h1);
}