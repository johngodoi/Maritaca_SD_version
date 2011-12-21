FacesTraceUtilities = function() {}
		
FacesTraceUtilities.prototype.toggleSection = function(section) {

	var element = section.getElementsByTagName('div')[0];
	if(element.style.display == 'none')
		element.style.display = 'block';
	else
		element.style.display = 'none';
}
			
var FacesTraceUtils = new FacesTraceUtilities();

//Smart Folding Menu tree- By Dynamic Drive (rewritten 03/03/02)
//For full source code and more DHTML scripts, visit http://www.dynamicdrive.com
//This credit MUST stay intact for use

var head="display:''"
img1=new Image()
img1.src="url(plus.gif)"
img2=new Image()
img2.src="url(minus.gif)"

var ns6=(document.getElementById&&!document.all||window.opera)
var ie4=document.all&&navigator.userAgent.indexOf("Opera")==-1

function checkcontained(e){
var iscontained=0
cur=ns6? e.target : event.srcElement
i=0
if (cur.id=="foldheader")
iscontained=1
else
while (ns6&&cur.parentNode||(ie4&&cur.parentElement)){
if (cur.id=="foldheader"||cur.id=="foldinglist"){
iscontained=(cur.id=="foldheader")? 1 : 0
break
}
cur=ns6? cur.parentNode : cur.parentElement
}

if (iscontained){
var foldercontent=ns6? cur.nextSibling.nextSibling : cur.all.tags("UL")[0]
if (foldercontent.style.display=="none"){
foldercontent.style.display=""
cur.style.listStyleImage="url(plus.gif)"
}
else{
foldercontent.style.display="none"
cur.style.listStyleImage="url(minus.gif)"
}
}
}

if (ie4||ns6)
document.onclick=checkcontained